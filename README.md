# 🛒 ESPECIFICAÇÃO COMPLETA — E-COMMERCE API (VERSÃO EVOLUÍDA)

---

## 1. 📌 Descrição do sistema

Sistema de e-commerce distribuído composto por três aplicações:

* **API principal (Java / Spring Boot)**
  Responsável por catálogo, carrinho, pedidos e autenticação.

* **API de pagamento (Python)**
  Simula um gateway de pagamento (estilo PIX), gerando links de pagamento e confirmando transações.

* **Frontend (PHP)**
  Interface web que consome a API Java e exibe o fluxo de compra ao usuário.

### 🔗 Integração entre serviços

```text
[ PHP Frontend ]
        ↓
[ Java API - E-commerce ]
        ↓
[ Python API - Payment Gateway ]
```

---

## 2. 🎯 Objetivo do projeto

Simular um fluxo real de e-commerce com integração externa de pagamento, demonstrando:

* Arquitetura modular e distribuída
* Comunicação entre serviços (HTTP)
* Modelagem de domínio realista
* Regras de negócio complexas
* Segurança com JWT
* Separação de responsabilidades entre sistemas

---

## 3. ✅ Requisitos funcionais

### Usuário

* Cadastro
* Login
* Visualizar perfil autenticado

---

### Produto

* Criar produto (ADMIN)
* Atualizar produto
* Listar produtos
* Buscar por ID
* Filtrar por nome e faixa de preço

---

### Carrinho

* Adicionar item
* Remover item
* Atualizar quantidade
* Visualizar carrinho

---

### Pedido

* Criar pedido a partir do carrinho
* Integrar com API de pagamento
* Armazenar link de pagamento
* Listar pedidos do usuário
* Visualizar detalhes do pedido
* Atualizar status após pagamento

---

### Pagamento (API Python)

* Criar pagamento
* Gerar link de pagamento (simulação PIX)
* Confirmar pagamento manualmente (simulação)

---

## 4. ⚙️ Requisitos não funcionais

* APIs RESTful padronizadas
* Comunicação HTTP entre serviços
* Autenticação via JWT (Java)
* Banco relacional (PostgreSQL)
* Código desacoplado e modular
* Tratamento global de exceções
* Paginação e filtros
* Logging estruturado
* Suporte a Docker (multi-container)
* Separação clara entre serviços

---

## 5. 📏 Regras de negócio

### Produto

* Estoque ≥ 0
* Não permitir venda sem estoque suficiente

---

### Carrinho

* Um carrinho por usuário
* Itens devem ter quantidade ≥ 1

---

### Pedido

* Pedido só pode ser criado se carrinho não estiver vazio
* Total = soma dos itens
* Preço do produto é congelado no momento da compra
* Pedido inicia com status `CREATED`

---

### Pagamento

* Pedido deve ser enviado para API de pagamento
* Após criação do pagamento:

  * status → `WAITING_PAYMENT`
  * salvar `paymentId` e `paymentUrl`
* Pedido só muda para `PAID` após confirmação
* Usuário só pode acessar seus próprios pedidos

---

## 6. 🏗️ Arquitetura do sistema

### Tipo

* Monolito modular (Java) + serviços externos

---

### Estilo

Arquitetura em camadas:

```text
Controller → Service → Domain → Repository
```

---

### Separação por módulos (Java)

* auth
* user
* product
* cart
* order
* payment (integração externa)

---

### Integração externa

* API Python via HTTP (RestTemplate/WebClient)

---

## 7. 🧰 Tecnologias recomendadas

### Java API

* Java 17+
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* PostgreSQL
* Flyway
* Lombok

---

### Python API

* FastAPI ou Flask
* UUID
* Simulação de pagamento

---

### Frontend

* PHP (simples, sem framework ou com Laravel opcional)

---

### Infra

* Docker
* Docker Compose

---

## 8. 🧱 Entidades do sistema

### User

* id (UUID)
* name
* email
* password
* createdAt

---

### Product

* id (UUID)
* name
* description
* price
* stock
* createdAt

---

### Cart

* id (UUID)
* userId

---

### CartItem

* id (UUID)
* cartId
* productId
* quantity

---

### Order (ATUALIZADA)

* id (UUID)
* userId
* total
* status
  (`CREATED`, `WAITING_PAYMENT`, `PAID`, `CANCELLED`)
* paymentId
* paymentUrl
* createdAt

---

### OrderItem

* id (UUID)
* orderId
* productId
* quantity
* price (snapshot)

---

## 9. 🔗 Relacionamentos

* User 1:N Order
* User 1:1 Cart
* Cart 1:N CartItem
* Order 1:N OrderItem
* Product 1:N CartItem
* Product 1:N OrderItem

---

## 10. 🌐 Endpoints da API

### Auth

* POST /auth/register
* POST /auth/login

---

### User

* GET /users/me

---

### Product

* POST /products
* PUT /products/{id}
* GET /products
* GET /products/{id}

---

### Cart

* POST /cart/items
* PUT /cart/items/{itemId}
* DELETE /cart/items/{itemId}
* GET /cart

---

### Order

* POST /orders → cria pedido + chama pagamento
* GET /orders
* GET /orders/{id}

---

### Integração interna (Java → Python)

#### Criar pagamento

```http
POST /payments
```

#### Confirmar pagamento (simulação)

```http
POST /payments/{id}/confirm
```

---

## 11. 📥 Request DTO

### Criar pedido

```json
{
  "cartId": "uuid"
}
```

---

### Request para API Python

```json
{
  "orderId": "uuid",
  "amount": 100.0
}
```

---

## 12. 📤 Response DTO

### Pedido (ATUALIZADO)

```json
{
  "id": "uuid",
  "total": 10000.00,
  "status": "WAITING_PAYMENT",
  "paymentUrl": "http://fake-pix.com/pay/uuid",
  "items": [
    {
      "productId": "uuid",
      "quantity": 2,
      "price": 5000.00
    }
  ]
}
```

---

## 13. 📁 Estrutura de pastas (Java)

```bash
config/
security/

controller/
service/
repository/

domain/
  model/
  enums/

dto/
  request/
  response/

integration/
  payment/

exception/
```

---

## 14. 📦 Padrão de resposta da API

### Sucesso

```json
{
  "success": true,
  "data": {},
  "timestamp": "2026-03-29T12:00:00Z"
}
```

---

### Erro

```json
{
  "success": false,
  "errors": [
    {
      "message": "Produto sem estoque",
      "code": "OUT_OF_STOCK"
    }
  ]
}
```

---

## 15. 🔐 Autenticação e autorização

### Java API

* JWT (Bearer Token)
* BCrypt para senha

### Perfis

* USER → compra, carrinho
* ADMIN → gerenciar produtos

---

### Python API

* Pode ser:

  * Aberta (simples)
  * Protegida com API Key (melhor prática)

---

## 16. 🚀 Possíveis melhorias futuras

* Webhook de pagamento (Python → Java)
* Cache com Redis
* Mensageria (Kafka/RabbitMQ)
* Microservices reais
* Sistema de cupons
* Upload de imagens
* Avaliações
* Rate limiting
* Observabilidade (logs + métricas)

---

## 17. 📊 Nível do projeto

👉 **Intermediário → Profissional (com arquitetura distribuída)**

