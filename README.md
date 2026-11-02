# NexusMarket23

Backend of **NexusMarket**, a marketplace where independent sellers publish physical and digital products, buyers purchase them, and the platform manages inventory, logistics, returns and refunds.

Academic project for **Software Construction 2**, built with a **hexagonal architecture** (ports and adapters) and **Domain-Driven Design**.

---

## Features

| Area | What It Does |
| ---- | ------------ |
| Users and roles | Public buyer registration, sellers registered by the administrator with their first warehouse, employees (logistics operator, administrator, supervisor), JWT login |
| Catalog | Physical and digital products, variants, publish / suspend / discontinue; the public catalog never exposes download links |
| Inventory | Stock per product and warehouse, reservations, damaged stock, full movement history stored in MongoDB |
| Purchase | Shopping cart, orders fulfilled from a single warehouse, frozen prices, invoices, payment; digital orders are delivered right after payment |
| Logistics | Shipments prepared, dispatched, in transit and delivered by the logistics operator |
| Post-sale | Returns of delivered orders, refunds, and returned units going back to stock |
| Reports | Total sales, orders by status and damaged inventory for supervisors and administrators |

---

## Technology

Java 17 · Spring Boot 4.1 · Spring Security with JWT · MySQL 8 (Spring Data JPA) · MongoDB 6 (Spring Data MongoDB) · Lombok · JUnit 5 · Maven · Docker Compose

---

## Quick Start

Only **Docker Desktop** is required.

```bash
docker compose up -d --build
docker compose logs -f nexusmarket-app     # wait for "Started NexusMarketApplication"
```

The API is available at `http://localhost:8080`. Health check: `GET http://localhost:8080/actuator/health`.

### Initial Users

| Username | Password | Role |
| -------- | -------- | ---- |
| `admin` | `admin123` | Administrator |
| `operator` | `operator123` | Logistics operator |
| `supervisor` | `supervisor123` | Supervisor |
| `tecnostore` | `seller123` | Seller |
| `ana` | `buyer123` | Buyer |

Log in with `POST /api/public/login` and send the returned token as `Authorization: Bearer <token>`.

### Try the API

Import `postman/NexusMarket.postman_collection.json` into Postman. It contains all **94 endpoints** grouped by role; tokens and identifiers are saved automatically.

### Run the Tests

```bash
docker compose up -d mysql-db mongo-db
./mvnw test          # Windows: .\mvnw.cmd test
```

---

## Project Structure

```text
src/main/java/application
├── domain            business rules: models, value objects, exceptions, services, ports
├── adapters
│   ├── rest          REST controllers, DTOs, mappers, error handling
│   ├── useCases      implementation of the input ports
│   ├── persistence   MySQL (JPA) and MongoDB adapters
│   └── security      JWT, Spring Security, CORS, BCrypt
└── infrastructure    initial data
```

---

## Documentation

| Document | Content |
| -------- | ------- |
| [`SETUP.md`](SETUP.md) | Step-by-step guide: run, test, configure and troubleshoot |
| [`SDD/Software Architecture/Software Architecture.md`](SDD/Software%20Architecture/Software%20Architecture.md) | Architecture overview and documentation map |
| [`SDD/C4/README.md`](SDD/C4/README.md) | C4 model diagrams |
| [`SDD/domain`](SDD/domain) | Domain model, value objects, services, input and output ports |
| [`SDD/Adapters`](SDD/Adapters) | REST API, validation, security, persistence and use cases |
