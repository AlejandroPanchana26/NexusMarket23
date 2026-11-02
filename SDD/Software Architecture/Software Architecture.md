# Software Architecture

## 1. Overview

NexusMarket is a marketplace backend built with **Java 17**, **Spring Boot 4** and a **hexagonal architecture** (ports and adapters) with **Domain-Driven Design**.

The business rules live in the domain, which does not depend on any framework detail: databases, HTTP and security are adapters connected through ports. Any adapter can be replaced without changing the domain.

---

## 2. Technology Stack

| Concern | Technology |
| ------- | ---------- |
| Language and framework | Java 17, Spring Boot 4.1 |
| REST API | Spring Web MVC, Bean Validation |
| Business data | MySQL 8 with Spring Data JPA (Hibernate) |
| Inventory movement history | MongoDB 6 with Spring Data MongoDB |
| Security | Spring Security, JWT (JJWT 0.12.6), BCrypt, CORS |
| Boilerplate reduction | Lombok |
| Tests | JUnit 5 |
| Build | Maven (wrapper included) |
| Deployment | Docker and Docker Compose |

---

## 3. Layers and Packages

```text
application
├── NexusMarketApplication.java
├── domain                      ← business rules (no framework dependencies except @Service)
│   ├── models                  ← entities with behavior
│   ├── valueobjects            ← catalogs (statuses, roles, types)
│   ├── enums                   ← ProductType
│   ├── exceptions              ← domain exception hierarchy
│   ├── services                ← business operations
│   └── ports
│       ├── in                  ← input ports: one menu per role
│       └── out                 ← output ports: what the domain needs from outside
├── adapters
│   ├── rest                    ← input adapter: controllers, DTOs, mappers, error handling
│   ├── useCases                ← implementation of the input ports
│   ├── persistence
│   │   ├── jpa                 ← MySQL adapters
│   │   └── mongodb             ← MongoDB adapter
│   └── security                ← JWT, Spring Security, CORS, BCrypt
└── infrastructure
    └── seed                    ← initial data
```

### Dependency Rule

Dependencies always point **towards the domain**:

* The domain depends on nothing outside itself. It defines the ports it needs.
* The adapters depend on the domain: they implement its output ports or use its input ports.
* The REST controllers never call services or repositories directly; they only use input ports.
* DTOs never enter the domain, and domain models never leave the API.

---

## 4. C4 Model

The architecture is described with the C4 model in the folder `SDD/C4`:

| Level | Document | Shows |
| ----- | -------- | ----- |
| 1. Context | `01-level-1-context.md` | NexusMarket and its five user roles |
| 2. Containers | `02-level-2-containers.md` | Application, MySQL and MongoDB |
| 3. Components | `03-level-3-components.md` | Components of the application and their dependencies |
| 4. Code | `04-level-4-code.md` | Main classes of the purchase domain and the port/adapter pattern |

The same folder has one document per layer (`05` to `10`) and the sequence diagrams of the main business flows (`11-endpoint-flows.md`).

---

## 5. Request Flow Example: Placing an Order

```text
POST /api/buyer/orders   (Authorization: Bearer <token>)
 1. JwtAuthenticationFilter     verifies the token and identifies the buyer
 2. SecurityConfig              allows /api/buyer/** for ROLE_BUYER
 3. BuyerRestController         gets the user from CurrentUserProvider
 4. BuyerPort → BuyerUseCase    delegates to PlaceOrderService
 5. PlaceOrderService           validates the cart, finds a warehouse with stock,
                                reserves units, saves the order, issues the invoice, empties the cart
 6. Output ports                ShoppingCartJpaAdapter, OrderJpaAdapter, InventoryJpaAdapter,
                                InvoiceJpaAdapter (MySQL) and InventoryMovementMongoAdapter (MongoDB)
 7. OrderRestMapper             converts the order into OrderResponse
 ← 201 Created
```

If any business rule fails, the domain throws an exception and `GlobalExceptionHandler` turns it into the corresponding HTTP status.

---

## 6. Deployment

| Option | Command | Use |
| ------ | ------- | --- |
| Everything in Docker | `docker compose up -d --build` | Evaluation and demos: only Docker is needed |
| Databases in Docker, application local | `docker compose up -d mysql-db mongo-db` and `mvnw spring-boot:run` | Development |

Configuration is read from environment variables with local-development defaults (database URLs, credentials, JWT secret and lifetime). See `SETUP.md`.

---

## 7. Documentation Map

| Document | Content |
| -------- | ------- |
| `SDD/domain/Domain Model.md` | Entities, attributes, behavior and relationships |
| `SDD/domain/Domain Value Objects.md` | Catalogs |
| `SDD/domain/Domain Services.md` | General service rules, access matrix, assumptions, future improvements |
| `SDD/domain/services/*.md` | One document per service area (identity, warehouse, catalog, inventory, purchase, shipment, post-sale, report) |
| `SDD/domain/Input-ports.md` | Input ports per role |
| `SDD/domain/Output-ports.md` | Output ports |
| `SDD/Adapters/Rest-adapters.md` | Controllers, DTOs and mappers |
| `SDD/Adapters/Api-rest-endpoints.md` | All endpoints and status codes |
| `SDD/Adapters/Rest-validation.md` | Validation of request data |
| `SDD/Adapters/Global-exception-handler.md` | Error handling |
| `SDD/Adapters/Rest-security-cors.md` | JWT security and CORS |
| `SDD/Adapters/Use-cases-adapters.md` | Use case adapters |
| `SDD/Adapters/Persistence-adapters.md` | MySQL tables, MongoDB collection, mapping rules |
| `SDD/C4/README.md` | C4 model: levels, layers and flows |
| `SETUP.md` | How to run and test the system |

