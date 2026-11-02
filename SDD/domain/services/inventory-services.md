# Inventory Services

Registration, changes and consultation of stock and its movement history. Package: `application.domain.services.inventory`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

Inventory is managed by sellers (only their own products) and logistics operators (any product).

## RegisterInventoryService

**Roles:** `SELLER`, `LOGISTICS_OPERATOR`.

| Method                                         | Business Rule                                                                                                                                                                  |
| ---------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| register(User, String, String, int)            | The product must be physical. A seller can only register its own products in its own or marketplace warehouses. A product can have only one inventory record per warehouse. An initial quantity greater than zero is recorded as an `ENTRY` movement. |

**Ports:** `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`, `ProductRepositoryPort`, `WarehouseRepositoryPort`.

## ManageStockService

**Roles:** `SELLER` (own products), `LOGISTICS_OPERATOR`.

| Method                          | Business Rule                                                                  |
| ------------------------------- | ----------------------------------------------------------------------------- |
| addStock(User, String, int)     | Applies `Inventory.addStock` and records an `ENTRY` movement.                 |
| adjust(User, String, int)       | Applies `Inventory.adjust` and records an `ADJUSTMENT` movement with the new quantity. |
| markAsDamaged(User, String)     | Applies `Inventory.markAsDamaged`. No movement is recorded because quantities do not change. |

**Ports:** `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`.

## ConsultInventoryService

| Method                          | Roles                                                                      | Business Rule                                       |
| ------------------------------- | -------------------------------------------------------------------------- | -------------------------------------------------- |
| listAll(User)                   | `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`                        | Returns all inventory records.                     |
| listByProduct(User, String)     | `SELLER` (own products), `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`| Returns the stock of a product in every warehouse. |
| listMovements(User, String)     | `SELLER` (own products), `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`| Returns the movement history of an inventory record. |

**Ports:** `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`, `ProductRepositoryPort`.
