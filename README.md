# 🛒 E-Commerce Distribuído

Projeto de portfólio com três aplicações trabalhando em conjunto:

```text
[ Frontend PHP ] -> [ API Spring Boot ] -> [ Payment API FastAPI ]
```

## Visão geral

| Serviço | Stack | Porta padrão | Função |
|---|---|---:|---|
| API principal | Java 17 + Spring Boot | `8082` | autenticação, usuários, produtos, carrinho e pedidos |
| Payment API | Python + FastAPI | `8000` | simulador de pagamentos |
| Frontend | PHP puro | `8090` | interface web |

## Subida rápida

```bash
./scripts/dev.sh start
```

Depois disso:

- **Frontend:** http://127.0.0.1:8090
- **API Spring Boot:** http://127.0.0.1:8082
- **H2 Console:** http://127.0.0.1:8082/h2-console
- **Payment API Docs:** http://127.0.0.1:8000/docs

Para ver status:

```bash
./scripts/dev.sh status
```

Para parar tudo:

```bash
./scripts/dev.sh stop
```

## Credenciais padrão

O projeto sobe um administrador seedado:

- **E-mail:** `admin@e-commerce.com`
- **Senha:** `admin123`

## Fluxos validados

Validados localmente pelo frontend:

- home, login, cadastro (renderização), produto e 404
- login admin
- área admin
- criação de produto
- adicionar ao carrinho
- carrinho
- checkout
- listagem de pedidos
- cancelamento de pedido

## Estado atual da integração de pagamento

O checkout do frontend já cria o pedido com sucesso na API principal. A **Payment API existe, sobe junto pelo script e pode ser explorada via Swagger**, mas o fluxo visual completo de aprovação/rejeição de pagamento **ainda não está exposto no frontend PHP**.

## Estrutura

```text
e-commerce/
├── frontend/              # Frontend PHP
├── payment-api/           # Simulador de pagamentos em FastAPI
├── src/                   # API principal em Spring Boot
├── scripts/dev.sh         # Script único para start/stop/status/logs
├── MANUAL_DE_USO.md       # Manual operacional do projeto
├── pom.xml
└── mvnw
```

## Pré-requisitos

- Java 17+
- Python 3.10+
- PHP 8.1+
- `curl`

## Operação manual

### API principal

```bash
./mvnw spring-boot:run
```

### Payment API

```bash
cd payment-api
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --host 127.0.0.1 --port 8000
```

### Frontend

```bash
cd frontend
php -S 127.0.0.1:8090 router.php
```

Se quiser sobrescrever as URLs consumidas pelo frontend:

```bash
export ECOMMERCE_API_BASE_URL="http://127.0.0.1:8082/api/v1"
export ECOMMERCE_PAYMENT_API_URL="http://127.0.0.1:8000"
```

## Logs

Quando iniciado por `./scripts/dev.sh start`, os logs ficam em:

```text
.dev-runtime/
```

Comando rápido:

```bash
./scripts/dev.sh logs
```

## Documentação complementar

O guia operacional completo está em [MANUAL_DE_USO.md](./MANUAL_DE_USO.md).
