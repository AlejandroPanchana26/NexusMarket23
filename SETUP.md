# NexusMarket — Setup Guide

Step-by-step guide to run NexusMarket from scratch, run its tests, and try the whole marketplace flow.

NexusMarket is a Spring Boot application built with a hexagonal architecture. It stores business data in **MySQL** and the inventory movement history in **MongoDB**, and it protects its REST API with **JWT** tokens.

---

## 0. Prerequisites

| Tool | Required | Purpose |
| ---- | -------- | ------- |
| Docker Desktop (with Docker Compose v2) | Yes | Runs MySQL, MongoDB and, optionally, the application |
| Postman | Recommended | Tries the API with the included collection |
| Java 17 or later | Only for option B | Runs the application outside Docker |

Check Docker:

```bash
docker --version
docker compose version
```

Docker Desktop must be open and show **Engine running**.

---

## 1. Start the application and the databases

All commands are run from the project root folder (where `pom.xml` and `docker-compose.yml` are).

### Option A — Everything in Docker (recommended)

Only Docker is needed: the application container already includes Java 17 and Maven.

```bash
docker compose up -d --build
```

The first start takes several minutes because Maven downloads the dependencies inside the container. Follow the progress with:

```bash
docker compose logs -f nexusmarket-app
```

The application is ready when the log shows `Started NexusMarketApplication`. Press `Ctrl + C` to stop following the log (the application keeps running).

### Option B — Databases in Docker, application on your computer

```bash
docker compose up -d mysql-db mongo-db
```

Wait until both containers are `(healthy)`:

```bash
docker compose ps
```

Then start the application:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### Initial data

On the first start, `DatabaseSeeder` creates these users (it does nothing on later starts):

| Username | Password | Role |
| -------- | -------- | ---- |
| `admin` | `admin123` | Administrator |
| `operador` | `operador123` | Logistics operator |
| `supervisor` | `supervisor123` | Supervisor |
| `tecnostore` | `vendedor123` | Seller (owner of "Bodega Norte") |
| `ana` | `comprador123` | Buyer |

It also creates the marketplace warehouse "Bodega Central", a published physical product with 20 units in stock, and a published digital product.

---

## 2. Compile and run the tests

```bash
# Windows
.\mvnw.cmd clean compile
.\mvnw.cmd test

# Linux / macOS
./mvnw clean compile
./mvnw test
```

Expected result: `Tests run: 22, Failures: 0, Errors: 0` and `BUILD SUCCESS`.

| Test class | What it checks |
| ---------- | -------------- |
| `InventoryTest` | Stock never goes negative; damaged stock cannot be reserved; reservation, sale and return cycle |
| `OrderTest` | Frozen prices; digital orders are delivered after payment; physical orders must be shipped first; finalized orders cannot change |
| `PurchaseServicesTest` | Placing an order reserves stock, issues the invoice and empties the cart; nothing is created without stock; buyers cannot pay other buyers' orders |
| `AccessRulesTest` | Sellers cannot change other sellers' products; supervisors cannot register sellers; duplicated emails, wrong passwords and blocked users are rejected |
| `NexusMarketApplicationTests` | The whole application starts and connects to the databases |

The domain tests use in-memory implementations of the output ports and do not need a database. `NexusMarketApplicationTests` starts the complete application, so **MySQL and MongoDB must be running** (step 1, option B, first command).

---

## 3. Verify the running application

Health check (no token needed):

```
GET http://localhost:8080/actuator/health
```

Expected response: a JSON document that contains `"status": "UP"`.

Public catalog (no token needed):

```
GET http://localhost:8080/api/public/products
```

### Ports

| Service | Port on your computer | Port inside Docker |
| ------- | --------------------- | ------------------ |
| Application | `8080` | `8080` |
| MySQL | `3307` | `3306` |
| MongoDB | `27017` | `27017` |

MySQL is published on port `3307` because port `3306` is often already used by a MySQL installed directly on the computer. Inside Docker, the application connects to `mysql-db:3306`.

### Configuration

The values are read from environment variables. The defaults are meant for local development only.

| Variable | Default value | Purpose |
| -------- | ------------- | ------- |
| `DB_URL` | `jdbc:mysql://localhost:3307/nexusmarket_db?...` | MySQL connection |
| `DB_USERNAME` / `DB_PASSWORD` | `nexus_app` / `nexus_pass` | MySQL credentials |
| `MONGODB_URI` | `mongodb://localhost:27017/nexusmarket_history` | MongoDB connection |
| `JWT_SECRET` | Development key (39 characters) | Signs the tokens; must have at least 32 characters |
| `JWT_EXPIRATION_MINUTES` | `60` | Token lifetime |
| `SERVER_PORT` | `8080` | Application port |

---

## 4. End-to-end test

### Authentication

1. Log in with `POST http://localhost:8080/api/public/login` and the body `{ "username": "ana", "password": "comprador123" }`.
2. The response contains a `token`.
3. Send it in every other request with the header `Authorization: Bearer <token>`.

| Response | Meaning |
| -------- | ------- |
| `401 Unauthorized` | No token, or the token is invalid or expired |
| `403 Forbidden` | Valid token, but the role is not allowed to use that address |

### Postman collection

The file `postman/NexusMarket.postman_collection.json` contains all the endpoints grouped by role.

1. In Postman, select **Import** and choose the file.
2. Run any login request in **0. Public**. The token is saved automatically in the collection variable `{{token}}`, and every other request uses it.
3. The requests that create something save its identifier automatically (`productId`, `warehouseId`, `inventoryId`, `orderId`, `shipmentId`, `returnId`, `refundId`).

Complete purchase and post-sale flow:

| Step | Folder | Request |
| ---- | ------ | ------- |
| 1 | 0. Public | Login as seller (tecnostore) |
| 2 | 2. Seller | Create product → Publish product → List my warehouses → Register inventory |
| 3 | 0. Public | Login as buyer (ana) |
| 4 | 1. Buyer | Add to cart → Place order → Pay order |
| 5 | 0. Public | Login as logistics operator (operador) |
| 6 | 3. Logistics Operator | Prepare shipment → Dispatch shipment → Mark in transit → Confirm delivery |
| 7 | 0. Public, then 1. Buyer | Login as buyer (ana) → Track shipment → Request return |
| 8 | 0. Public, then 2. Seller | Login as seller → Approve return → Create refund |
| 9 | 0. Public, then 4. Administrator | Login as administrator → Process refund |
| 10 | 0. Public, then 3. Logistics Operator | Login as logistics operator → Complete return |
| 11 | 0. Public, then 5. Supervisor | Login as supervisor → Calculate total sales → List movements |

In the last step, the movement history shows `ENTRY`, `RESERVATION`, `SALE_EXIT` and `RETURN`, read from MongoDB.

### Inspect the databases

```bash
# MySQL tables
docker exec nexusmarket-mysql mysql -unexus_app -pnexus_pass nexusmarket_db -e "SHOW TABLES;"

# MongoDB inventory movements
docker exec nexusmarket-mongo mongosh nexusmarket_history --quiet --eval "db.inventory_movements.find().toArray()"
```

---

## 5. Shut down

Stop the containers and keep the data:

```bash
docker compose down
```

Stop the containers and **delete all the data** (the initial data is created again on the next start):

```bash
docker compose down -v
```

If the application runs on your computer (option B), stop it with `Ctrl + C`.

---

## 6. Troubleshooting

| Problem | Solution |
| ------- | -------- |
| `port is already allocated` for `3307`, `27017` or `8080` | Another program uses that port. Stop it, or change the port on the left side of the `ports` mapping in `docker-compose.yml`. |
| `Cannot connect to the Docker daemon` | Open Docker Desktop and wait for **Engine running**. |
| The application cannot connect to MySQL (option B) | Check `docker compose ps`: both databases must be `(healthy)`. |
| `NexusMarketApplicationTests` fails | Start the databases before running `mvnw test`. |
| `401` in every request | Run a login request again; tokens expire after 60 minutes. |
| `403` in a request | The logged-in role cannot use that address. Log in with the correct role. |
| Option A and option B at the same time | Both use port `8080`. Run only one of them. |
