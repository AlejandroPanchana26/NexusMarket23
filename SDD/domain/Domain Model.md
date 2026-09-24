# Domain Model

## Introduction

The Domain Model represents the core business entities of the NexusMarket marketplace platform. These entities encapsulate the data, relationships, lifecycle, and business rules described in the functional specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles:

* Inheritance is used only for genuine domain specialization.
* Relationships are explicit object references instead of generic identifier fields.
* Entities are not anemic: each entity protects its own business rules through behavior methods. When a rule is violated, the entity throws a domain exception and its state does not change.

The model distinguishes between:

* **Persons**, which represent identifiable people and their role within the marketplace (buyers, sellers, and internal employees).
* **Users**, which represent system identities used for authentication and authorization.
* **Products**, which represent the physical or digital goods offered for sale.
* **Warehouses**, which represent the physical locations where inventory is stored.
* **Inventory**, which represents the distributed stock of physical products.
* **Shopping carts and orders**, which represent the provisional selection and the formal commercial commitment of a buyer.
* **Post-sale entities** (Invoice, Shipment, Return, Refund), which represent the processes that occur after an order is placed.

Coordination between several entities (for example, reserving inventory when an order is placed) is the responsibility of the domain services, not of the entities.

---

# Domain Class Hierarchy

```text
Person (Abstract)
├── Buyer
├── Seller
└── Employee

User

Product (Abstract)
├── PhysicalProduct
└── DigitalProduct

Warehouse
Inventory
InventoryMovement
ShoppingCart
CartItem
Order
OrderDetail
Invoice
Shipment
Return
Refund
```

---

# Domain Relationships

```text
Person
   │
   ├── Buyer
   ├── Seller
   │      │
   │      ├── owns ──────────> Warehouse
   │      └── publishes ─────> Product
   └── Employee

User
   └── represents ──────────> Person

Product
   └── belongs to ─────────> Seller

Inventory
   ├── references ─────────> PhysicalProduct
   └── located in ────────> Warehouse
          │
          └── generates ───> InventoryMovement

Buyer
   ├── has ────────────────> ShoppingCart ──> CartItem
   └── places ─────────────> Order ─────────> OrderDetail

Order
   ├── billed by ─────────> Invoice
   ├── shipped by ────────> Shipment
   └── may generate ──────> Return ─────────> Refund
```

---

# Entities

---

# Person (Abstract)

## Description

Represents any identifiable person within the marketplace.

This abstract class centralizes the common identity and contact information shared by buyers, sellers, and internal employees. The role assigned to a person defines what that person means within the system. Each person has exactly one role.

This class cannot be instantiated directly.

## Attributes

| Attribute      | Type       | Description                                               |
| -------------- | ---------- | -------------------------------------------------------- |
| identification | String     | Unique identifier of the person.                          |
| name           | String     | Full name of the person.                                  |
| email          | String     | Primary registered email address.                         |
| phoneNumber    | String     | Primary contact phone number.                             |
| role           | SystemRole | Business role that defines the person's responsibilities. |

## Behavior

| Method                       | Business Rule                                                                 |
| ---------------------------- | ---------------------------------------------------------------------------- |
| hasRole(SystemRole)          | Returns whether the person has the given role.                               |
| validateIdentity() (protected) | Identification, name, and email are required; the email must contain `@`. |

## Relationships

* A `Person` may be specialized as a `Buyer`, a `Seller`, or an `Employee`.
* A `Person` may be represented by a `User` for system access.

---

# Buyer

## Description

Represents a customer who purchases products published in the marketplace.

A buyer may own multiple delivery addresses and has a commercial status that determines whether it can purchase. A buyer never manages information belonging to other buyers or to inventories.

## Inherits From

`Person`

## Attributes

| Attribute           | Type          | Description                                     |
| ------------------- | ------------- | ----------------------------------------------- |
| principalAddress    | String        | Primary delivery address. Required.             |
| additionalAddresses | List\<String> | Secondary delivery addresses. Empty by default. |
| commercialStatus    | BuyerStatus   | Status of the buyer for placing orders.         |

## Behavior

| Method                        | Business Rule                                                                  |
| ----------------------------- | ----------------------------------------------------------------------------- |
| register()                    | Validates identity and principal address; assigns role `BUYER` and status `ACTIVE`. |
| canPlaceOrders()              | Returns true only when the commercial status is `ACTIVE`.                     |
| addAdditionalAddress(String)  | The address is required and cannot duplicate an existing address.            |
| suspend()                     | Allowed only from `ACTIVE`.                                                   |
| activate()                    | Allowed only from `SUSPENDED`.                                                |
| block()                       | Allowed from `ACTIVE` or `SUSPENDED`. Blocking is permanent.                  |

---

# Seller

## Description

Represents a provider of products in the marketplace.

Sellers cannot self-register; they are incorporated by the Administrator. A seller manages its own warehouses and products.

## Inherits From

`Person`

## Attributes

| Attribute    | Type             | Description                                          |
| ------------ | ---------------- | --------------------------------------------------- |
| sellerStatus | SellerStatus     | Operational status of the seller.                   |
| warehouses   | List\<Warehouse> | Warehouses owned by the seller. Empty by default.   |
| products     | List\<Product>   | Products published by the seller. Empty by default. |

## Behavior

| Method                   | Business Rule                                                         |
| ------------------------ | -------------------------------------------------------------------- |
| register()               | Validates identity; assigns role `SELLER` and status `ACTIVE`.       |
| canSell()                | Returns true only when the seller status is `ACTIVE`.                |
| addWarehouse(Warehouse)  | Links the warehouse to the seller as its owner.                      |
| addProduct(Product)      | Links the product to the seller.                                     |
| suspend()                | Allowed only from `ACTIVE`.                                          |
| activate()               | Allowed from `SUSPENDED` or `INACTIVE`.                              |
| deactivate()             | Allowed from `ACTIVE` or `SUSPENDED`.                                |

---

# Employee

## Description

Represents an internal member of the platform staff: logistics operator, administrator, or supervisor.

These three roles share the same data, so a single class is used and the `role` attribute identifies which one the employee is.

## Inherits From

`Person`

## Attributes

No additional attributes. All data is inherited from `Person`.

## Behavior

| Method                | Business Rule                                                                                   |
| --------------------- | ---------------------------------------------------------------------------------------------- |
| register(SystemRole)  | Validates identity. The role must be `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, or `SUPERVISOR`. |

---

# User

## Description

Represents a system identity used for authentication and authorization.

A user references the person (buyer, seller, or employee) it represents. The role of the user is the role of that person, so each user has exactly one role. User status is independent from the commercial status of the associated person.

## Attributes

| Attribute | Type       | Description                                     |
| --------- | ---------- | ---------------------------------------------- |
| userId    | Integer    | Internal unique identifier of the system user. |
| username  | String     | Login name used during authentication.         |
| password  | String     | Password stored by the system.                 |
| status    | UserStatus | Current status of the user's system access.    |
| person    | Person     | Person represented by this user.               |

## Behavior

| Method               | Business Rule                                                                  |
| -------------------- | ----------------------------------------------------------------------------- |
| register()           | Username, password, and a person with a role are required; status becomes `ACTIVE`. |
| isActive()           | Returns true only when the status is `ACTIVE`.                                |
| hasRole(SystemRole)  | Returns whether the represented person has the given role.                    |
| getRole()            | Returns the role of the represented person.                                   |
| block()              | Allowed only from `ACTIVE`.                                                   |
| deactivate()         | Allowed only from `ACTIVE`.                                                   |
| activate()           | Allowed from `BLOCKED` or `INACTIVE`.                                         |

## Relationships

* A `User` references one `Person`.

---

# Product (Abstract)

## Description

Represents any product offered in the marketplace catalog.

The catalog distinguishes between physical products (which require inventory and shipping) and digital products (delivered immediately after payment).

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type          | Description                                          |
| ----------- | ------------- | --------------------------------------------------- |
| identifier  | String        | Unique identifier of the product.                   |
| name        | String        | Name of the product.                                |
| description | String        | Description of the product.                         |
| price       | BigDecimal    | Sale price of the product.                          |
| seller      | Seller        | Seller who owns the product.                        |
| variants    | List\<String> | Variants (color, size, model). Empty by default.    |
| status      | ProductStatus | Catalog status of the product.                      |
| type        | ProductType   | Technical classification: physical or digital.      |

## Behavior

| Method                   | Business Rule                                                                             |
| ------------------------ | ---------------------------------------------------------------------------------------- |
| requiresInventory() (abstract) | Each specialization states whether it uses warehouse stock.                        |
| publish()                | Allowed from a new or `SUSPENDED` product. Requires name, seller, and a price greater than zero. |
| suspend()                | Allowed only from `PUBLISHED`.                                                           |
| discontinue()            | Allowed from `PUBLISHED` or `SUSPENDED`. `DISCONTINUED` is final.                        |
| isAvailableForSale()     | Returns true only when the status is `PUBLISHED`.                                        |
| changePrice(BigDecimal)  | The price must be greater than zero. A discontinued product cannot change its price.    |
| addVariant(String)       | The variant is required and cannot be duplicated.                                        |

## Relationships

* A `Product` belongs to one `Seller`.

---

# PhysicalProduct

## Description

Represents a physical product that requires inventory management and shipping. It is created with type `PHYSICAL`.

## Inherits From

`Product`

## Attributes

| Attribute | Type       | Description            |
| --------- | ---------- | ---------------------- |
| weight    | BigDecimal | Weight of the product. |

## Behavior

| Method               | Business Rule                                                        |
| -------------------- | ------------------------------------------------------------------- |
| requiresInventory()  | Returns true.                                                       |
| publish()            | The weight must be greater than zero before applying the common rules. |

---

# DigitalProduct

## Description

Represents a digital product that is delivered immediately after payment and does not require inventory. It is created with type `DIGITAL`.

## Inherits From

`Product`

## Attributes

| Attribute   | Type   | Description                    |
| ----------- | ------ | ------------------------------ |
| downloadUrl | String | Download link for the product. |

## Behavior

| Method               | Business Rule                                                            |
| -------------------- | ----------------------------------------------------------------------- |
| requiresInventory()  | Returns false.                                                          |
| publish()            | The download URL is required before applying the common rules.          |

---

# Warehouse

## Description

Represents a physical storage location. Warehouses are classified as marketplace warehouses or seller warehouses.

## Attributes

| Attribute  | Type          | Description                                                         |
| ---------- | ------------- | ------------------------------------------------------------------ |
| identifier | String        | Unique identifier of the warehouse.                                |
| name       | String        | Name of the warehouse.                                             |
| address    | String        | Physical address of the warehouse.                                 |
| type       | WarehouseType | Type of warehouse (marketplace or seller).                         |
| owner      | Seller        | Owning seller. Null when the warehouse is a marketplace warehouse. |

## Behavior

| Method                   | Business Rule                                                                                  |
| ------------------------ | --------------------------------------------------------------------------------------------- |
| register()               | Name, address, and type are required. A marketplace warehouse cannot have an owner; a seller warehouse must have one. |
| assignTo(Seller)         | Sets the type to `SELLER` and the given seller as owner.                                      |
| isMarketplaceWarehouse() | Returns true when the type is `MARKETPLACE`.                                                  |
| belongsTo(Seller)        | Returns whether the given seller is the owner.                                                |

---

# Inventory

## Description

Represents the distributed stock of a physical product in a specific warehouse.

Inventory must be linked to a product and a warehouse. Negative stock is not allowed under any circumstance, and damaged stock cannot be reserved.

## Attributes

| Attribute         | Type            | Description                                 |
| ----------------- | --------------- | ------------------------------------------ |
| identifier        | String          | Unique identifier of the inventory record. |
| product           | PhysicalProduct | Physical product being stocked.            |
| warehouse         | Warehouse       | Warehouse where the stock is located.      |
| availableQuantity | Integer         | Quantity available for sale.               |
| reservedQuantity  | Integer         | Quantity reserved for orders in progress.  |
| status            | InventoryStatus | Current status of the inventory.           |

## Behavior

All quantities received by these methods must be greater than zero, except in `adjust`, which accepts zero.

| Method               | Business Rule                                                                            |
| -------------------- | --------------------------------------------------------------------------------------- |
| register()           | Requires product and warehouse; starts with zero available and zero reserved units.    |
| addStock(int)        | Increases available units. Not allowed on damaged inventory.                            |
| reserve(int)         | Moves units from available to reserved. Not allowed on damaged inventory or beyond the available units. |
| confirmSale(int)     | Removes units from the reserved quantity when the order is shipped. Cannot exceed the reserved units. |
| receiveReturn(int)   | Increases available units with returned products.                                       |
| adjust(int)          | Sets the available quantity after a physical count. It cannot be negative.              |
| markAsDamaged()      | Sets the status to `DAMAGED`.                                                           |
| hasAvailable(int)    | Returns whether the inventory is not damaged and has enough available units.            |
| isDamaged()          | Returns true when the status is `DAMAGED`.                                              |

The status switches automatically between `AVAILABLE` and `RESERVED` according to the quantities.

## Relationships

* An `Inventory` references one `PhysicalProduct` and one `Warehouse`.
* An `Inventory` may generate multiple `InventoryMovement` instances.

---

# InventoryMovement

## Description

Represents a significant event that changed inventory quantities. While inventory represents the current stock, a movement is the record of an action that occurred.

## Attributes

| Attribute    | Type                  | Description                               |
| ------------ | --------------------- | ---------------------------------------- |
| identifier   | String                | Unique identifier of the movement.       |
| inventory    | Inventory             | Inventory affected by the movement.      |
| movementType | InventoryMovementType | Type of movement.                        |
| quantity     | Integer               | Quantity involved in the movement.       |
| date         | LocalDateTime         | Date and time when the movement occurred.|
| performedBy  | User                  | User who performed the movement.         |

## Behavior

| Method                                                   | Business Rule                                                                         |
| -------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| InventoryMovement(Inventory, InventoryMovementType, int, User) (constructor) | Inventory, type, and user are required; the quantity cannot be negative; the date is set automatically. |

---

# ShoppingCart

## Description

Represents the provisional selection of products made by a buyer before confirming an order.

## Attributes

| Attribute  | Type            | Description                             |
| ---------- | --------------- | -------------------------------------- |
| identifier | String          | Unique identifier of the shopping cart.|
| buyer      | Buyer           | Buyer who owns the cart.               |
| items      | List\<CartItem> | Selected items. Empty by default.      |

## Behavior

| Method                  | Business Rule                                                                                     |
| ----------------------- | ------------------------------------------------------------------------------------------------ |
| addItem(Product, int)   | The buyer must be able to place orders and the product must be available for sale. If the product is already in the cart, its quantity increases. |
| removeItem(Product)     | The product must be in the cart.                                                                 |
| clear()                 | Removes all items.                                                                               |
| isEmpty()               | Returns true when the cart has no items.                                                         |
| calculateTotal()        | Sums the subtotals of all items.                                                                 |

---

# CartItem

## Description

Represents a single line within a shopping cart: a product with its quantity and price.

## Attributes

| Attribute | Type       | Description                      |
| --------- | ---------- | -------------------------------- |
| product   | Product    | Product added to the cart.       |
| quantity  | Integer    | Quantity of the product.         |
| unitPrice | BigDecimal | Price of the product when added. |

## Behavior

| Method                          | Business Rule                                                                 |
| ------------------------------- | ---------------------------------------------------------------------------- |
| CartItem(Product, int) (constructor) | The product is required and the quantity must be greater than zero. The current product price is copied. |
| increaseQuantity(int)           | The extra quantity must be greater than zero.                                |
| calculateSubtotal()             | Returns unit price multiplied by quantity.                                   |
| isForProduct(Product)           | Returns whether the line belongs to the given product (same identifier).     |

---

# Order

## Description

Represents the formal commercial commitment made by a buyer. Its lifecycle is the central process of the system. A finalized order cannot be modified under any circumstance. An order that contains physical products is shipped from a single warehouse.

## Lifecycle

```text
CART → PENDING_PAYMENT → PAID → SHIPPED → DELIVERED
                           └──────────────────┘ (digital-only orders)
```

## Attributes

| Attribute    | Type               | Description                               |
| ------------ | ------------------ | ---------------------------------------- |
| identifier   | String             | Unique identifier of the order.          |
| buyer        | Buyer              | Buyer who places the order.              |
| details      | List\<OrderDetail> | Order lines. Empty by default.           |
| status       | OrderStatus        | Current lifecycle status of the order.   |
| creationDate | LocalDateTime      | Date and time when the order was created.|
| total        | BigDecimal         | Total value of the order.                |
| fulfillmentWarehouse | Warehouse    | Warehouse where the units were reserved and from which the order ships. Empty for digital-only orders. |

## Behavior

| Method                                  | Business Rule                                                                                         |
| --------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| fromCart(ShoppingCart) (static)         | The cart cannot be empty. Creates the order, copies each item with its cart price, and confirms it.  |
| startFor(Buyer)                         | The buyer must be able to place orders. The order starts in `CART` with total zero.                  |
| addDetail(Product, int, BigDecimal)     | Allowed only in `CART`. A finalized order cannot be modified. Updates the total.                     |
| confirm()                               | From `CART` to `PENDING_PAYMENT`. The order must have at least one line.                             |
| markAsPaid()                            | From `PENDING_PAYMENT` to `PAID`.                                                                    |
| markAsShipped()                         | From `PAID` to `SHIPPED`. The order must contain physical products.                                  |
| markAsDelivered()                       | From `SHIPPED` to `DELIVERED`, or from `PAID` when the order has only digital products.              |
| isFinalized()                           | Returns true when the status is `DELIVERED`.                                                         |
| containsPhysicalProducts()              | Returns whether any line has a product that requires inventory.                                      |

## Relationships

* An `Order` is placed by one `Buyer`.
* An `Order` with physical products is fulfilled from a single `Warehouse` (`fulfillmentWarehouse`).
* An `Order` contains multiple `OrderDetail` instances.
* An `Order` may be billed by an `Invoice`, shipped by a `Shipment`, and may generate a `Return`.

---

# OrderDetail

## Description

Represents a single line within an order: a product with its quantity, unit price, and subtotal. The values are fixed when the line is created.

## Attributes

| Attribute | Type       | Description                             |
| --------- | ---------- | -------------------------------------- |
| product   | Product    | Product included in the order line.    |
| quantity  | Integer    | Quantity of the product.               |
| unitPrice | BigDecimal | Agreed unit price.                     |
| subtotal  | BigDecimal | Line subtotal (quantity × unit price). |

## Behavior

| Method                                            | Business Rule                                                                  |
| ------------------------------------------------- | ----------------------------------------------------------------------------- |
| OrderDetail(Product, int, BigDecimal) (constructor) | Product and price are required, the quantity must be greater than zero, and the subtotal is calculated. |

---

# Invoice

## Description

Represents the commercial billing information associated with an order. The invoice is issued when the order is confirmed and waiting for payment.

## Attributes

| Attribute   | Type          | Description                           |
| ----------- | ------------- | ------------------------------------ |
| identifier  | String        | Unique identifier of the invoice.    |
| order       | Order         | Order being billed.                  |
| issueDate   | LocalDateTime | Date and time the invoice was issued.|
| totalAmount | BigDecimal    | Total billed amount.                 |
| status      | InvoiceStatus | Current status of the invoice.       |

## Behavior

| Method           | Business Rule                                                                           |
| ---------------- | -------------------------------------------------------------------------------------- |
| issueFor(Order)  | The order must be `PENDING_PAYMENT`. Copies the order total; status becomes `ISSUED`. |
| markAsPaid()     | Allowed only from `ISSUED`.                                                            |
| cancel()         | Allowed only from `ISSUED`. A paid invoice cannot be cancelled.                        |

---

# Shipment

## Description

Represents the logistics process for delivering an order that contains physical products.

## Attributes

| Attribute          | Type           | Description                        |
| ------------------ | -------------- | --------------------------------- |
| identifier         | String         | Unique identifier of the shipment.|
| order              | Order          | Order being shipped.              |
| originWarehouse    | Warehouse      | Warehouse the order ships from.   |
| destinationAddress | String         | Delivery address.                 |
| status             | ShipmentStatus | Current status of the shipment.   |
| logisticsOperator  | User           | Logistics operator responsible.   |
| shipmentDate       | LocalDateTime  | Date and time of dispatch.        |
| deliveryDate       | LocalDateTime  | Date and time of delivery.        |

## Behavior

| Method                                         | Business Rule                                                                                                 |
| ---------------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| prepareFor(Order, Warehouse, String, User)     | The order must be `PAID` and contain physical products. Origin and address are required. The user must have the `LOGISTICS_OPERATOR` role. Status becomes `PREPARING`. |
| dispatch()                                     | From `PREPARING` to `SHIPPED`; records the shipment date.                                                   |
| markInTransit()                                | From `SHIPPED` to `IN_TRANSIT`.                                                                              |
| markAsDelivered()                              | From `IN_TRANSIT` to `DELIVERED`; records the delivery date.                                                |

---

# Return

## Description

Represents a return requested by a buyer for an order that was already delivered. The return references the order but never modifies it.

## Attributes

| Attribute   | Type          | Description                             |
| ----------- | ------------- | -------------------------------------- |
| identifier  | String        | Unique identifier of the return.       |
| order       | Order         | Order the return applies to.           |
| reason      | String        | Reason for the return.                 |
| status      | ReturnStatus  | Current status of the return.          |
| requestDate | LocalDateTime | Date and time the return was requested.|

## Behavior

| Method                   | Business Rule                                                           |
| ------------------------ | ---------------------------------------------------------------------- |
| requestFor(Order, String)| The order must be finalized and the reason is required. Status becomes `REQUESTED`. |
| approve()                | Allowed only from `REQUESTED`.                                         |
| reject()                 | Allowed only from `REQUESTED`.                                         |
| complete()               | Allowed only from `APPROVED`.                                          |
| canBeRefunded()          | Returns true when the status is `APPROVED` or `COMPLETED`.             |

---

# Refund

## Description

Represents the reimbursement generated by an approved return.

## Attributes

| Attribute     | Type          | Description                         |
| ------------- | ------------- | ---------------------------------- |
| identifier    | String        | Unique identifier of the refund.   |
| returnRequest | Return        | Return that originates the refund. |
| amount        | BigDecimal    | Amount to be refunded.             |
| status        | RefundStatus  | Current status of the refund.      |
| date          | LocalDateTime | Date and time of the refund.       |

## Behavior

| Method                        | Business Rule                                                                                         |
| ----------------------------- | ---------------------------------------------------------------------------------------------------- |
| createFor(Return, BigDecimal) | The return must allow refunds. The amount must be greater than zero and cannot exceed the order total. Status becomes `PENDING`. |
| process()                     | Allowed only from `PENDING`; records the processing date.                                            |
| reject()                      | Allowed only from `PENDING`.                                                                         |

---

# Domain Exceptions

When a business rule is violated, the entity throws a domain exception and its state remains unchanged. All domain exceptions inherit from `DomainException`, which extends `RuntimeException`.

```text
DomainException
├── BusinessRuleViolationException
├── InvalidStatusTransitionException
├── EntityNotFoundException
├── DuplicateEntityException
└── UnauthorizedOperationException
```

| Exception                        | When It Is Used                                                              |
| -------------------------------- | --------------------------------------------------------------------------- |
| DomainException                  | Base class for every domain rule violation.                                 |
| BusinessRuleViolationException   | A business rule or required data is not satisfied.                          |
| InvalidStatusTransitionException | A status change is not allowed from the current status.                     |
| EntityNotFoundException          | A requested entity does not exist. Used by domain services.                 |
| DuplicateEntityException         | A unique value (such as identification or email) already exists. Used by domain services. |
| UnauthorizedOperationException   | The user's role does not allow the operation. Used by domain services.      |

---

# Domain Design Rules

## Person and User

* `Buyer`, `Seller`, and `Employee` inherit from `Person`.
* `role` is defined in `Person`; each person, and therefore each user, has exactly one role.
* `User` references the `Person` it represents through an explicit relationship, not a generic identifier.

## Products

* `PhysicalProduct` and `DigitalProduct` are specializations of `Product` and set their own `ProductType` when created.
* Only physical products participate in inventory management and shipping.

## Inventory

* Inventory must be linked to a physical product and a warehouse.
* Negative stock is not allowed.
* Damaged stock cannot be reserved.

## Orders

* An order is created from a non-empty cart and follows a defined lifecycle.
* Order lines preserve the unit price at the time of purchase.
* A finalized order cannot be modified under any circumstance.

## Behavior and Coordination

* Each entity validates its own rules and throws a domain exception when a rule is violated.
* Operations that coordinate several entities belong to the domain services.

---

# Related Documentation

The value objects, enumerations, and their lifecycles referenced throughout this model are documented in **Domain Value Objects.md**.
