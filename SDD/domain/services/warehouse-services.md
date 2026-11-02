# Warehouse Services

Registration and consultation of warehouses. Package: `application.domain.services.warehouse`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

## RegisterWarehouseService

**Role:** `ADMINISTRATOR`.

| Method                                  | Business Rule                                                                                                   |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| register(User, Warehouse, String)       | Without a seller identification, creates a marketplace warehouse. With a seller identification, creates an additional warehouse for that seller. |

**Ports:** `WarehouseRepositoryPort`.

## ConsultWarehousesService

| Method            | Roles                                                   | Business Rule                        |
| ----------------- | ------------------------------------------------------- | ----------------------------------- |
| listAll(User)     | `ADMINISTRATOR`, `LOGISTICS_OPERATOR`, `SUPERVISOR`    | Returns all warehouses.             |
| listOwn(User)     | `SELLER`                                                | Returns only the seller's warehouses.|

**Ports:** `WarehouseRepositoryPort`.
