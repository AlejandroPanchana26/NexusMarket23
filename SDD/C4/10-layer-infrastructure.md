# Layer — Infrastructure

## Initial Data

`DatabaseSeeder` (`infrastructure.seed`) implements `CommandLineRunner`, so it runs when the application starts. It does nothing if the user `admin` already exists, so data is never duplicated.

It creates the first administrator directly (registering employees requires an administrator) and then uses the domain services for everything else, so the initial data follows the same business rules:

| Data | Detail |
| ---- | ------ |
| Users | `admin`, `operator`, `supervisor`, `tecnostore` (seller), `ana` (buyer) |
| Warehouses | "North Warehouse" (seller) and "Central Warehouse" (marketplace) |
| Products | A published physical product with 20 units in stock, and a published digital product |

## Configuration

`application.properties` reads every value from an environment variable with a local-development default (`${VARIABLE:default}`):

| Property | Variable |
| -------- | -------- |
| `spring.datasource.url`, `username`, `password` | `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` |
| `spring.mongodb.uri` | `MONGODB_URI` |
| `jwt.secret`, `jwt.expiration-minutes` | `JWT_SECRET`, `JWT_EXPIRATION_MINUTES` |
| `cors.allowed-origins` | `CORS_ALLOWED_ORIGINS` |
| `server.port` | `SERVER_PORT` |

`spring.jpa.hibernate.ddl-auto=update` lets Hibernate create and update the MySQL tables from the entities.

## Deployment

```mermaid
flowchart TB
    subgraph compose["docker-compose.yml"]
        app["nexusmarket-app<br>maven:3.9-eclipse-temurin-17<br>port 8080"]
        mysql[("mysql-db<br>mysql:8.0<br>3307 → 3306")]
        mongo[("mongo-db<br>mongo:6.0<br>27017")]
        app -- "depends on (healthy)" --> mysql
        app -- "depends on (healthy)" --> mongo
    end
    v1[["volume mysql_data"]] --- mysql
    v2[["volume mongo_data"]] --- mongo
    v3[["volume maven_cache"]] --- app
```

The volumes keep the data and the downloaded dependencies when the containers stop. Step-by-step instructions are in `SETUP.md`.
