# Manual de Uso

## 1. Objetivo

Este projeto simula um e-commerce distribuído com:

- uma **API principal** em Spring Boot
- uma **API de pagamentos** em FastAPI
- um **frontend web** em PHP

O manual abaixo cobre instalação, inicialização, uso e troubleshooting.

## 2. Pré-requisitos

- Java 17 ou superior
- Python 3.10 ou superior
- PHP 8.1 ou superior
- `curl`

## 3. Portas padrão

| Serviço | URL |
|---|---|
| Frontend | `http://127.0.0.1:8090` |
| API principal | `http://127.0.0.1:8082` |
| Payment API | `http://127.0.0.1:8000` |

## 4. Como iniciar tudo de uma vez

Na raiz do projeto:

```bash
./scripts/dev.sh start
```

Esse comando:

1. cria a pasta local `.dev-runtime/`
2. sobe a API Spring Boot
3. cria a virtualenv da Payment API se ela ainda não existir
4. instala dependências Python no primeiro uso
5. sobe a Payment API
6. sobe o frontend PHP já configurado para conversar com as APIs

## 5. Como parar tudo

```bash
./scripts/dev.sh stop
```

## 6. Como ver status e logs

### Status

```bash
./scripts/dev.sh status
```

### Logs

```bash
./scripts/dev.sh logs
```

Os arquivos também ficam em:

```text
.dev-runtime/
```

## 7. Primeiro acesso

Abra:

```text
http://127.0.0.1:8090
```

Usuário administrador padrão:

- **e-mail:** `admin@e-commerce.com`
- **senha:** `admin123`

## 8. Fluxo de uso no frontend

### 8.1 Visitante

Sem login, você pode:

- ver a home
- abrir páginas de produto
- acessar login e cadastro

### 8.2 Compra

Fluxo disponível no frontend:

1. fazer login
2. abrir um produto
3. adicionar ao carrinho
4. acessar `/carrinho`
5. seguir para `/checkout`
6. confirmar a compra
7. visualizar o pedido em `/pedidos`

### 8.3 Administração

Com o usuário admin, você pode:

1. acessar `/admin`
2. criar produtos em `/admin/products.php`
3. visualizar os pedidos do usuário autenticado em `/admin/orders.php`

## 9. Observações importantes

### 9.1 Sobre o checkout

O frontend atualmente valida os campos do checkout e cria o pedido com sucesso na API principal.

### 9.2 Sobre a Payment API

A Payment API sobe normalmente e pode ser testada pelo Swagger:

```text
http://127.0.0.1:8000/docs
```

Mas o frontend PHP **ainda não expõe um fluxo visual completo** de aprovação/rejeição de pagamento. Hoje ela está mais preparada como serviço de apoio e evolução da arquitetura do que como uma etapa final já totalmente refletida na interface.

## 10. Operação manual por serviço

### 10.1 Spring Boot

```bash
./mvnw spring-boot:run
```

### 10.2 Payment API

```bash
cd payment-api
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --host 127.0.0.1 --port 8000
```

### 10.3 Frontend

```bash
cd frontend
php -S 127.0.0.1:8090 router.php
```

## 11. Variáveis úteis

O frontend aceita sobrescrita das URLs de API:

```bash
export ECOMMERCE_API_BASE_URL="http://127.0.0.1:8082/api/v1"
export ECOMMERCE_PAYMENT_API_URL="http://127.0.0.1:8000"
```

O script `scripts/dev.sh` também aceita portas diferentes:

```bash
SPRING_PORT=9082 PAYMENT_PORT=9000 FRONTEND_PORT=9090 ./scripts/dev.sh start
```

## 12. Troubleshooting

### Porta em uso

Rode:

```bash
./scripts/dev.sh stop
```

e depois:

```bash
./scripts/dev.sh start
```

### Frontend abre, mas não autentica

Confira se a API principal está ativa:

```bash
curl http://127.0.0.1:8082/api/v1/products
```

### Payment API não sobe

Verifique a virtualenv e dependências:

```bash
cd payment-api
python3 -m venv .venv
. .venv/bin/activate
pip install -r requirements.txt
```

### Ver logs rapidamente

```bash
./scripts/dev.sh logs
```
