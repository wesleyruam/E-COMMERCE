#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RUNTIME_DIR="$ROOT_DIR/.dev-runtime"
SPRING_PID_FILE="$RUNTIME_DIR/spring.pid"
PAYMENT_PID_FILE="$RUNTIME_DIR/payment.pid"
FRONTEND_PID_FILE="$RUNTIME_DIR/frontend.pid"
SPRING_LOG="$RUNTIME_DIR/spring.log"
PAYMENT_LOG="$RUNTIME_DIR/payment.log"
FRONTEND_LOG="$RUNTIME_DIR/frontend.log"

HOST="${HOST:-127.0.0.1}"
SPRING_PORT="${SPRING_PORT:-8082}"
PAYMENT_PORT="${PAYMENT_PORT:-8000}"
FRONTEND_PORT="${FRONTEND_PORT:-8090}"

usage() {
    cat <<EOF
Uso: ./scripts/dev.sh <start|stop|status|logs>

Comandos:
  start   Sobe Spring Boot, Payment API e frontend PHP
  stop    Encerra os serviços iniciados pelo script
  status  Mostra o estado atual dos serviços
  logs    Exibe o caminho dos logs e as últimas linhas

Variáveis opcionais:
  HOST
  SPRING_PORT
  PAYMENT_PORT
  FRONTEND_PORT
EOF
}

require_cmd() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "Comando obrigatório não encontrado: $1" >&2
        exit 1
    fi
}

ensure_runtime_dir() {
    mkdir -p "$RUNTIME_DIR"
}

is_pid_running() {
    local pid="$1"
    kill -0 "$pid" >/dev/null 2>&1
}

read_pid() {
    local file="$1"
    if [[ -f "$file" ]]; then
        cat "$file"
    fi
}

port_pids() {
    local port="$1"

    if command -v lsof >/dev/null 2>&1; then
        lsof -ti tcp:"$port" -sTCP:LISTEN 2>/dev/null | sort -u
        return
    fi

    ss -ltnp "sport = :$port" 2>/dev/null \
        | awk 'NR>1 {print $NF}' \
        | sed -n 's/.*pid=\([0-9]\+\).*/\1/p' \
        | sort -u
}

check_port_free() {
    local port="$1"
    if [[ -z "$(port_pids "$port")" ]]; then
        return
    fi

    echo "A porta $port já está em uso." >&2
    exit 1
}

wait_for_http() {
    local url="$1"
    local label="$2"

    for _ in $(seq 1 60); do
        if curl -fsS "$url" >/dev/null 2>&1; then
            echo "$label pronto em $url"
            return 0
        fi
        sleep 1
    done

    echo "Timeout aguardando $label em $url" >&2
    return 1
}

start_spring() {
    check_port_free "$SPRING_PORT"
    (
        cd "$ROOT_DIR"
        nohup ./mvnw spring-boot:run >"$SPRING_LOG" 2>&1 &
        echo $! >"$SPRING_PID_FILE"
    )
    wait_for_http "http://$HOST:$SPRING_PORT/api/v1/products" "API Spring Boot"
}

ensure_payment_venv() {
    local payment_dir="$ROOT_DIR/payment-api"

    if [[ ! -x "$payment_dir/.venv/bin/python" ]]; then
        echo "Criando virtualenv da Payment API..."
        python3 -m venv "$payment_dir/.venv"
        "$payment_dir/.venv/bin/python" -m pip install --upgrade pip >/dev/null
        "$payment_dir/.venv/bin/python" -m pip install -r "$payment_dir/requirements.txt"
    fi
}

start_payment() {
    check_port_free "$PAYMENT_PORT"
    ensure_payment_venv
    (
        cd "$ROOT_DIR/payment-api"
        nohup ./.venv/bin/python -m uvicorn main:app --host "$HOST" --port "$PAYMENT_PORT" >"$PAYMENT_LOG" 2>&1 &
        echo $! >"$PAYMENT_PID_FILE"
    )
    wait_for_http "http://$HOST:$PAYMENT_PORT/docs" "Payment API"
}

start_frontend() {
    check_port_free "$FRONTEND_PORT"
    (
        cd "$ROOT_DIR/frontend"
        nohup env \
            ECOMMERCE_API_BASE_URL="http://$HOST:$SPRING_PORT/api/v1" \
            ECOMMERCE_PAYMENT_API_URL="http://$HOST:$PAYMENT_PORT" \
            php -S "$HOST:$FRONTEND_PORT" router.php >"$FRONTEND_LOG" 2>&1 &
        echo $! >"$FRONTEND_PID_FILE"
    )
    wait_for_http "http://$HOST:$FRONTEND_PORT/" "Frontend PHP"
}

terminate_pid() {
    local pid="$1"
    local label="$2"

    if ! is_pid_running "$pid"; then
        return
    fi

    kill "$pid" >/dev/null 2>&1 || true

    for _ in $(seq 1 5); do
        if ! is_pid_running "$pid"; then
            return
        fi
        sleep 1
    done

    kill -9 "$pid" >/dev/null 2>&1 || true
    echo "$label finalizado à força (PID $pid)"
}

stop_service_file() {
    local label="$1"
    local file="$2"

    if [[ ! -f "$file" ]]; then
        echo "$label: sem PID registrado"
        return
    fi

    local pid
    pid="$(cat "$file")"

    if [[ -n "$pid" ]] && is_pid_running "$pid"; then
        terminate_pid "$pid" "$label"
        echo "$label encerrado (PID $pid)"
    else
        echo "$label: processo já não está em execução"
    fi

    rm -f "$file"
}

stop_port_listener() {
    local label="$1"
    local port="$2"

    mapfile -t pids < <(port_pids "$port")
    if [[ ${#pids[@]} -eq 0 ]]; then
        return
    fi

    for pid in "${pids[@]}"; do
        [[ -n "$pid" ]] || continue
        terminate_pid "$pid" "$label"
        echo "$label encerrado pela porta $port (PID $pid)"
    done
}

clear_stale_pid_file() {
    local file="$1"
    local pid
    pid="$(read_pid "$file" || true)"

    if [[ -n "${pid:-}" ]] && ! is_pid_running "$pid"; then
        rm -f "$file"
    fi
}

show_status() {
    for item in \
        "Spring Boot:$SPRING_PID_FILE:http://$HOST:$SPRING_PORT/api/v1/products" \
        "Payment API:$PAYMENT_PID_FILE:http://$HOST:$PAYMENT_PORT/docs" \
        "Frontend PHP:$FRONTEND_PID_FILE:http://$HOST:$FRONTEND_PORT/"; do
        IFS=":" read -r label file url <<<"$item"
        pid="$(read_pid "$file" || true)"
        if [[ -n "${pid:-}" ]] && is_pid_running "$pid"; then
            echo "$label: rodando (PID $pid) -> $url"
        else
            echo "$label: parado"
        fi
    done
}

show_logs() {
    ensure_runtime_dir
    echo "Logs em: $RUNTIME_DIR"
    for file in "$SPRING_LOG" "$PAYMENT_LOG" "$FRONTEND_LOG"; do
        if [[ -f "$file" ]]; then
            echo
            echo "==> $file <=="
            tail -n 20 "$file"
        fi
    done
}

start_all() {
    require_cmd curl
    require_cmd php
    require_cmd python3
    ensure_runtime_dir

    if [[ -x "$ROOT_DIR/mvnw" ]]; then
        :
    else
        echo "mvnw não encontrado na raiz do projeto." >&2
        exit 1
    fi

    clear_stale_pid_file "$SPRING_PID_FILE"
    clear_stale_pid_file "$PAYMENT_PID_FILE"
    clear_stale_pid_file "$FRONTEND_PID_FILE"

    if [[ -f "$SPRING_PID_FILE" || -f "$PAYMENT_PID_FILE" || -f "$FRONTEND_PID_FILE" ]]; then
        echo "Há PID files existentes. Rode './scripts/dev.sh stop' antes de iniciar novamente." >&2
        exit 1
    fi

    start_spring
    start_payment
    start_frontend

    echo
    echo "Ambiente iniciado com sucesso:"
    echo "- Frontend:    http://$HOST:$FRONTEND_PORT"
    echo "- Spring Boot: http://$HOST:$SPRING_PORT"
    echo "- Payment API: http://$HOST:$PAYMENT_PORT/docs"
    echo
    echo "Use './scripts/dev.sh logs' para acompanhar a saída."
}

stop_all() {
    stop_service_file "Frontend PHP" "$FRONTEND_PID_FILE"
    stop_service_file "Payment API" "$PAYMENT_PID_FILE"
    stop_service_file "Spring Boot" "$SPRING_PID_FILE"
    stop_port_listener "Frontend PHP" "$FRONTEND_PORT"
    stop_port_listener "Payment API" "$PAYMENT_PORT"
    stop_port_listener "Spring Boot" "$SPRING_PORT"
}

case "${1:-}" in
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    *)
        usage
        exit 1
        ;;
esac
