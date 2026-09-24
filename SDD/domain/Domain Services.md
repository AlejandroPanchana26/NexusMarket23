# Domain Services

## Introduction

Domain services coordinate the operations of NexusMarket that involve several entities or require access to stored information.

Entities protect their own rules (for example, `Inventory.reserve` never allows negative stock). Services execute the steps of a complete business operation in the correct order and use the output ports to read and store data. Services never duplicate the rules that already belong to the entities; they call the entity methods instead.

All services are located in `application.domain.services` and are grouped in one folder per business area.

---

# General Design Rules

* **Validate first, store last.** Every service validates all the rules before calling any `save` method, so a failed operation never leaves partial data.
* **Ports through the constructor.** Each service receives the output ports it needs through its constructor and is marked with `@Service` so that Spring can create it.
* **Authenticated user and role (RG-01, RG-03).** Every operation, except public registration, login, and the public catalog, receives the authenticated `User` and validates the allowed roles with `AccessValidator`.
* **Own information only (RG-03).** Buyers and sellers can only act on their own information. Administrators, logistics operators, and supervisors act according to their role.
* **Generated identifiers.** Services assign the identifier of new entities with `UUID.randomUUID()`. Person identification is provided by the person.

---

# Service Structure

```text
services
├── AccessValidator
├── identity
│   ├── UserAccountService
│   ├── PersonLookupService
│   ├── RegisterBuyerService
│   ├── RegisterSellerService
│   ├── RegisterEmployeeService
│   ├── LoginService
│   ├── ManageUserStatusService
│   ├── ManageBuyerStatusService
│   └── ManageSellerStatusService
├── warehouse
│   ├── RegisterWarehouseService
│   └── ConsultWarehousesService
├── catalog
│   ├── CreateProductService
│   ├── ManageProductService
│   └── ConsultCatalogService
├── inventory
│   ├── RegisterInventoryService
│   ├── ManageStockService
│   └── ConsultInventoryService
├── purchase
│   ├── ManageCartService
│   ├── PlaceOrderService
│   ├── PayOrderService
│   └── ConsultOrdersService
├── shipment
│   ├── ShipOrderService
│   └── ConsultShipmentsService
├── postsale
│   ├── ManageReturnService
│   ├── ManageRefundService
│   └── ConsultReturnsService
└── report
    └── SupervisorReportService
```

---

# AccessValidator

Helper class with static methods. It is not instantiated.

| Method                                         | Business Rule                                                                              |
| ---------------------------------------------- | ----------------------------------------------------------------------------------------- |
| requireRole(User, SystemRole...)               | The user must exist, be active, and have one of the allowed roles (RG-01, RG-03).        |
| requireOwnerIfSeller(User, Seller)             | If the user is a seller, the given seller must be that same user. Other roles are not restricted by this check. |

---

# Identity Services

## UserAccountService

Shared steps used by every registration service.

| Method                                         | Business Rule                                                                                     |
| ---------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| validateNewAccount(Person, String, String)     | Username and password are required. Identification, email, and username must not be registered. |
| createUser(Person, String, String)             | Encodes the password with `PasswordEncoderPort`, registers the user, and stores it.            |

**Ports:** `PersonRepositoryPort`, `UserRepositoryPort`, `PasswordEncoderPort`.

## PersonLookupService

| Method               | Business Rule                                                         |
| -------------------- | -------------------------------------------------------------------- |
| findSeller(String)   | Returns the person only if it is a `Seller`; otherwise, not found.   |
| findBuyer(String)    | Returns the person only if it is a `Buyer`; otherwise, not found.    |

**Ports:** `PersonRepositoryPort`.

## RegisterBuyerService

**Role:** public (no authenticated user required).

| Method                               | Steps                                                                                          |
| ------------------------------------ | --------------------------------------------------------------------------------------------- |
| register(Buyer, String, String)      | 1. `buyer.register()`. 2. Validate the new account. 3. Store the buyer. 4. Create the user.   |

**Ports:** `PersonRepositoryPort`.

## RegisterSellerService

**Role:** `ADMINISTRATOR`. Sellers cannot self-register.

| Method                                                   | Steps                                                                                                                         |
| -------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| register(User, Seller, Warehouse, String, String)        | 1. Validate role. 2. `seller.register()`. 3. Validate the new account. 4. Link and register the first warehouse. 5. Store seller and warehouse. 6. Create the user. |

**Ports:** `PersonRepositoryPort`, `WarehouseRepositoryPort`.

## RegisterEmployeeService

**Role:** `ADMINISTRATOR`.

| Method                                                     | Steps                                                                                        |
| ---------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| register(User, Employee, SystemRole, String, String)       | 1. Validate role. 2. `employee.register(role)`. 3. Validate the new account. 4. Store the employee. 5. Create the user. |

**Ports:** `PersonRepositoryPort`.

## LoginService

**Role:** public.

| Method                    | Business Rule                                                                                                   |
| ------------------------- | -------------------------------------------------------------------------------------------------------------- |
| login(String, String)     | Returns the user when the username exists, the password matches, and the user is active. A wrong username and a wrong password produce the same message so that existing usernames are not revealed. |

**Ports:** `UserRepositoryPort`, `PasswordEncoderPort`.

## ManageUserStatusService

**Role:** `ADMINISTRATOR`.

| Method                     | Business Rule                                 |
| -------------------------- | -------------------------------------------- |
| block(User, String)        | Blocks the user's access (`User.block`).     |
| deactivate(User, String)   | Deactivates the user (`User.deactivate`).    |
| activate(User, String)     | Reactivates the user (`User.activate`).      |

**Ports:** `UserRepositoryPort`.

## ManageBuyerStatusService

**Role:** `ADMINISTRATOR`.

| Method                   | Business Rule                              |
| ------------------------ | ----------------------------------------- |
| suspend(User, String)    | Suspends the buyer (`Buyer.suspend`).     |
| activate(User, String)   | Reactivates the buyer (`Buyer.activate`). |
| block(User, String)      | Blocks the buyer permanently (`Buyer.block`). |

**Ports:** `PersonRepositoryPort`.

## ManageSellerStatusService

**Role:** `ADMINISTRATOR`.

| Method                     | Business Rule                                 |
| -------------------------- | -------------------------------------------- |
| suspend(User, String)      | Suspends the seller (`Seller.suspend`).      |
| activate(User, String)     | Reactivates the seller (`Seller.activate`).  |
| deactivate(User, String)   | Deactivates the seller (`Seller.deactivate`).|

**Ports:** `PersonRepositoryPort`.

---

# Warehouse Services

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

---

# Catalog Services

## CreateProductService

**Role:** `SELLER` (active).

| Method                  | Business Rule                                                                                                  |
| ----------------------- | ------------------------------------------------------------------------------------------------------------- |
| create(User, Product)   | The product name is required. The product is linked to the seller and stored without status; it is not visible until it is published. |

**Ports:** `ProductRepositoryPort`.

## ManageProductService

**Role:** `SELLER`. A seller can only manage its own products (RG-03).

| Method                                        | Business Rule                                                      |
| --------------------------------------------- | ----------------------------------------------------------------- |
| publish(User, String)                         | The seller must be active. Applies `Product.publish`.             |
| suspend(User, String)                         | Applies `Product.suspend`.                                        |
| discontinue(User, String)                     | Applies `Product.discontinue`.                                    |
| changePrice(User, String, BigDecimal)         | Applies `Product.changePrice`.                                    |
| addVariant(User, String, String)              | Applies `Product.addVariant`.                                     |

**Ports:** `ProductRepositoryPort`.

## ConsultCatalogService

| Method             | Roles     | Business Rule                                              |
| ------------------ | --------- | --------------------------------------------------------- |
| listPublished()    | public    | Returns the public catalog: only `PUBLISHED` products.    |
| listOwn(User)      | `SELLER`  | Returns all the seller's products, published or not.      |

**Ports:** `ProductRepositoryPort`.

---

# Inventory Services

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

---

# Purchase Services

## ManageCartService

**Role:** `BUYER`. Each buyer has one cart, created empty the first time it is used.

| Method                          | Business Rule                                        |
| ------------------------------- | --------------------------------------------------- |
| addItem(User, String, int)      | Applies `ShoppingCart.addItem`.                     |
| removeItem(User, String)        | Applies `ShoppingCart.removeItem`.                  |
| clear(User)                     | Applies `ShoppingCart.clear`.                       |
| view(User)                      | Returns the buyer's cart.                           |

**Ports:** `ShoppingCartRepositoryPort`, `ProductRepositoryPort`.

## PlaceOrderService

**Role:** `BUYER`.

| Method               | Steps |
| -------------------- | ----- |
| placeOrder(User)     | 1. Find the buyer's cart. 2. Check that every product is still published. 3. Create the order with `Order.fromCart`. 4. If the order has physical products, find the first warehouse with enough available stock for **all** of them. 5. Reserve the stock there and record a `RESERVATION` movement per product. 6. Store the order with its `fulfillmentWarehouse`. 7. Issue the invoice. 8. Empty the cart. |

**Business rule:** an order with physical products is fulfilled from a single warehouse. If no single warehouse has enough stock, the order is not created and nothing is reserved.

**Ports:** `ShoppingCartRepositoryPort`, `OrderRepositoryPort`, `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`, `InvoiceRepositoryPort`.

## PayOrderService

**Role:** `BUYER` (own orders only).

| Method              | Steps |
| ------------------- | ----- |
| pay(User, String)   | 1. Mark the order and its invoice as paid. 2. If the order has only digital products, mark it as delivered immediately. |

The payment is registered as confirmed because the functional specification does not define a payment gateway.

**Ports:** `OrderRepositoryPort`, `InvoiceRepositoryPort`.

## ConsultOrdersService

| Method                        | Roles                                                     | Business Rule                                                |
| ----------------------------- | --------------------------------------------------------- | ----------------------------------------------------------- |
| listOwn(User)                 | `BUYER`                                                   | Returns the buyer's orders.                                 |
| listForSeller(User)           | `SELLER`                                                  | Returns the orders that include at least one of the seller's products. |
| listAll(User)                 | `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`       | Returns all orders.                                         |
| getInvoice(User, String)      | `BUYER` (own orders), `ADMINISTRATOR`, `SUPERVISOR`       | Returns the invoice of an order.                            |

**Ports:** `OrderRepositoryPort`, `InvoiceRepositoryPort`.

---

# Shipment Services

## ShipOrderService

**Role:** `LOGISTICS_OPERATOR`.

| Method                                  | Steps / Business Rule |
| --------------------------------------- | --------------------- |
| prepare(User, String, String)           | The order can have only one shipment. The origin is the order's `fulfillmentWarehouse`. Without an address, the buyer's principal address is used; a given address must be one of the buyer's registered addresses. Applies `Shipment.prepareFor`. |
| dispatch(User, String)                  | Applies `Shipment.dispatch` and `Order.markAsShipped`. For each physical product, applies `Inventory.confirmSale` in the origin warehouse and records a `SALE_EXIT` movement. |
| markInTransit(User, String)             | Applies `Shipment.markInTransit`. |
| confirmDelivery(User, String)           | Applies `Shipment.markAsDelivered` and `Order.markAsDelivered`. The order becomes finalized after the confirmed delivery. |

**Ports:** `ShipmentRepositoryPort`, `OrderRepositoryPort`, `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`.

## ConsultShipmentsService

| Method                     | Roles                                                                    | Business Rule                     |
| -------------------------- | ------------------------------------------------------------------------ | -------------------------------- |
| listAll(User)              | `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`                      | Returns all shipments.           |
| findByOrder(User, String)  | `BUYER` (own orders), `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`| Returns the shipment of an order.|

**Ports:** `ShipmentRepositoryPort`.

---

# Post-Sale Services

A return applies to the complete order.

## ManageReturnService

| Method                              | Roles                                                  | Steps / Business Rule |
| ----------------------------------- | ------------------------------------------------------ | --------------------- |
| request(User, String, String)       | `BUYER` (own orders)                                   | A new return is allowed only if every previous return of the order was rejected. Applies `Return.requestFor` (the order must be delivered). |
| approve(User, String)               | `ADMINISTRATOR`, or a `SELLER` with products in the order | Applies `Return.approve`. |
| reject(User, String)                | `ADMINISTRATOR`, or a `SELLER` with products in the order | Applies `Return.reject`. |
| complete(User, String)              | `LOGISTICS_OPERATOR`, `ADMINISTRATOR`                  | Applies `Return.complete`. For each physical product, applies `Inventory.receiveReturn` in the order's `fulfillmentWarehouse` and records a `RETURN` movement. |

The helper `requireApprover(User, Order)` is shared with `ManageRefundService`.

**Ports:** `ReturnRepositoryPort`, `OrderRepositoryPort`, `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`.

## ManageRefundService

| Method                                | Roles                                                  | Business Rule |
| ------------------------------------- | ------------------------------------------------------ | ------------- |
| create(User, String, BigDecimal)      | `ADMINISTRATOR`, or a `SELLER` with products in the order | A return can have only one refund. Applies `Refund.createFor`. |
| process(User, String)                 | `ADMINISTRATOR`                                        | Applies `Refund.process`. |
| reject(User, String)                  | `ADMINISTRATOR`                                        | Applies `Refund.reject`. |

**Ports:** `RefundRepositoryPort`, `ReturnRepositoryPort`.

## ConsultReturnsService

| Method                             | Roles                                                                      | Business Rule                          |
| ---------------------------------- | -------------------------------------------------------------------------- | ------------------------------------- |
| listByStatus(User, ReturnStatus)   | `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`                        | Returns the returns in a status.      |
| listForOrder(User, String)         | `BUYER` (own orders), `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`  | Returns the returns of an order.      |
| findRefund(User, String)           | `BUYER` (own orders), `ADMINISTRATOR`, `SUPERVISOR`, `LOGISTICS_OPERATOR`  | Returns the refund of a return.       |

**Ports:** `ReturnRepositoryPort`, `RefundRepositoryPort`, `OrderRepositoryPort`.

---

# Report Services

## SupervisorReportService

Administrative queries of objective OBJ-12. **Roles:** `SUPERVISOR`, `ADMINISTRATOR`.

| Method                                  | Business Rule                                                                 |
| --------------------------------------- | ---------------------------------------------------------------------------- |
| countOrdersByStatus(User, OrderStatus)  | Returns the number of orders in a status.                                    |
| listOrdersByStatus(User, OrderStatus)   | Returns the orders in a status.                                              |
| calculateTotalSales(User)               | Adds the totals of the orders in `PAID`, `SHIPPED`, or `DELIVERED`.          |
| listDamagedInventory(User)              | Returns the inventory records marked as `DAMAGED`.                           |

**Ports:** `OrderRepositoryPort`, `InventoryRepositoryPort`.

---

# Access Matrix

| Area                         | Buyer | Seller | Logistics Operator | Administrator | Supervisor |
| ---------------------------- | :---: | :----: | :----------------: | :-----------: | :--------: |
| Buyer registration           | ✔ (public) |   |                    |               |            |
| Seller and employee registration |   |        |                    | ✔             |            |
| User, buyer, seller status   |       |        |                    | ✔             |            |
| Warehouses                   |       | consult own |  consult      | ✔             | consult    |
| Product catalog              | consult (public) | ✔ own |           |               |            |
| Inventory                    |       | ✔ own  | ✔                  | consult       | consult    |
| Shopping cart and orders     | ✔ own | consult own |  consult      | consult       | consult    |
| Shipments                    | consult own |  | ✔                  | consult       | consult    |
| Returns                      | request own | approve / reject own | complete | ✔       | consult    |
| Refunds                      | consult own | create own |        | ✔             |            |
| Reports                      |       |        |                    | ✔             | ✔          |

---

# Assumptions

* **Returns and refunds.** The responsibility matrix of the functional specification assigns refund management to two participants, but the source does not make clear which ones. It is assumed that returns and refunds are approved by the administrator or by the seller whose products are in the order, that the logistics operator receives returned products, and that the administrator processes the money of refunds.
* **Payment.** The specification does not define a payment gateway, so paying an order registers the payment as confirmed.
* **Initial administrator.** The system needs one administrator created at startup to register sellers and employees. It will be created in the infrastructure layer.

---

# Future Improvements

* **Cancellation of unpaid orders.** The order lifecycle of the specification has no cancellation state, so stock reserved by an unpaid order stays reserved.
* **Partial returns.** Returns apply to the complete order; returning individual products would require return lines.
