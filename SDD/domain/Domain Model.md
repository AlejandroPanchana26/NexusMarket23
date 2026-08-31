# Domain Model

## Introduction

The Domain Model represents the core business entities of the NexusMarket marketplace platform. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the functional specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields.

The model distinguishes between:

* **Persons**, which represent identifiable people and their role within the marketplace.
* **Users**, which represent system identities used for authentication and authorization.
* **Products**, which represent the physical or digital goods offered for sale.
* **Warehouses**, which represent the physical locations where inventory is stored.
* **Inventory**, which represents the distributed stock of physical products.
* **Orders**, which represent the formal commercial commitment made by a buyer.
* **Post-sale entities** (Invoice, Shipment, Return, Refund), which represent the processes that occur after an order is placed.

---

# Domain Class Hierarchy

```text
Person (Abstract)
├── Buyer
└── Seller

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
   └── Seller
          │
          ├── owns ──────────> Warehouse
          └── publishes ─────> Product

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

This abstract class centralizes the common identity and contact information shared by buyers and sellers.

The role assigned to a person represents what that person means within the system and determines the responsibilities associated with that person.

This class cannot be instantiated directly.

## Attributes

| Attribute      | Type       | Description                                                        |
| -------------- | ---------- | ----------------------------------------------------------------- |
| identification | String     | Unique identifier of the person (identity or tax identification). |
| name           | String     | Full name of the person.                                          |
| email          | String     | Primary registered email address.                                 |
| phoneNumber    | String     | Primary contact phone number.                                     |
| role           | SystemRole | Business role that defines the person's responsibilities.         |

## Relationships

* A `Person` may be specialized as a `Buyer` or a `Seller`.
* A `Person` may be represented by a `User` for system access.

---

# Buyer

## Description

Represents a customer who purchases products published in the marketplace.

A buyer may own multiple delivery addresses and has a commercial status that determines whether it can place orders.

A buyer never manages information belonging to other buyers or to inventories.

## Inherits From

`Person`

## Attributes

| Attribute           | Type          | Description                                      |
| ------------------- | ------------- | ------------------------------------------------ |
| principalAddress    | String        | Primary delivery address. Required.              |
| additionalAddresses | List\<String> | Secondary delivery addresses. Empty by default.  |
| commercialStatus    | BuyerStatus   | Status of the buyer for placing orders.          |

---

# Seller

## Description

Represents a provider of products in the marketplace.

Sellers cannot self-register; they are incorporated by the Administrator.

A seller manages its own warehouses and products.

## Inherits From

`Person`

## Attributes

| Attribute    | Type              | Description                                        |
| ------------ | ----------------- | ------------------------------------------------- |
| sellerStatus | SellerStatus      | Operational status of the seller.                 |
| warehouses   | List\<Warehouse>  | Warehouses owned by the seller. Empty by default. |
| products     | List\<Product>    | Products published by the seller. Empty by default.|

---

# User

## Description

Represents a system identity used for authentication and authorization.

A user references the person (buyer or seller) it represents within the system. User status is independent from the commercial status of the associated person.

## Attributes

| Attribute | Type       | Description                                        |
| --------- | ---------- | ------------------------------------------------- |
| userId    | Integer    | Internal unique identifier of the system user.    |
| username  | String     | Login name used during authentication.            |
| password  | String     | Secure password stored by the system.             |
| status    | UserStatus | Current status of the user's system access.       |
| person    | Person     | Person represented by this user.                  |

## Relationships

* A `User` references one `Person`.

---

# Product (Abstract)

## Description

Represents any product offered in the marketplace catalog.

The catalog distinguishes between physical products (which require inventory and shipping) and digital products (delivered immediately after payment).

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type          | Description                                       |
| ----------- | ------------- | ------------------------------------------------ |
| identifier  | String        | Unique identifier of the product.                |
| name        | String        | Name of the product.                             |
| description | String        | Description of the product.                      |
| price       | BigDecimal    | Sale price of the product.                       |
| seller      | Seller        | Seller who owns the product.                     |
| variants    | List\<String> | Variants (color, size, model). Empty by default. |
| status      | ProductStatus | Catalog status of the product.                   |
| type        | ProductType   | Technical classification: physical or digital.   |

## Relationships

* A `Product` belongs to one `Seller`.

---

# PhysicalProduct

## Description

Represents a physical product that requires inventory management and shipping.

## Inherits From

`Product`

## Attributes

| Attribute | Type       | Description               |
| --------- | ---------- | ------------------------- |
| weight    | BigDecimal | Weight of the product.    |

---

# DigitalProduct

## Description

Represents a digital product that is delivered immediately after payment and does not require inventory.

## Inherits From

`Product`

## Attributes

| Attribute   | Type   | Description                     |
| ----------- | ------ | ------------------------------- |
| downloadUrl | String | Download link for the product.  |

---

# Warehouse

## Description

Represents a physical storage location.

Warehouses are classified as marketplace warehouses or seller warehouses.

## Attributes

| Attribute  | Type          | Description                                              |
| ---------- | ------------- | ------------------------------------------------------- |
| identifier | String        | Unique identifier of the warehouse.                     |
| name       | String        | Name of the warehouse.                                  |
| address    | String        | Physical address of the warehouse.                      |
| type       | WarehouseType | Type of warehouse (marketplace or seller).              |
| owner      | Seller        | Owning seller. Null when the warehouse is a marketplace warehouse. |

---

# Inventory

## Description

Represents the distributed stock of a physical product in a specific warehouse.

Inventory must be linked to a product and a warehouse. Negative stock is not allowed under any circumstance.

## Attributes

| Attribute         | Type            | Description                                  |
| ----------------- | --------------- | ------------------------------------------- |
| identifier        | String          | Unique identifier of the inventory record.  |
| product           | PhysicalProduct | Physical product being stocked.             |
| warehouse         | Warehouse       | Warehouse where the stock is located.       |
| availableQuantity | Integer         | Quantity available for sale.                |
| reservedQuantity  | Integer         | Quantity reserved for orders in progress.   |
| status            | InventoryStatus | Current status of the inventory.            |

## Relationships

* An `Inventory` references one `PhysicalProduct` and one `Warehouse`.
* An `Inventory` may generate multiple `InventoryMovement` instances.

---

# InventoryMovement

## Description

Represents a significant event that changes inventory quantities.

While inventory represents the current stock, a movement represents an action that occurred (entry, reservation, sale exit, adjustment, return).

## Attributes

| Attribute    | Type                  | Description                              |
| ------------ | --------------------- | --------------------------------------- |
| identifier   | String                | Unique identifier of the movement.      |
| inventory    | Inventory             | Inventory affected by the movement.     |
| movementType | InventoryMovementType | Type of movement.                       |
| quantity     | Integer               | Quantity involved in the movement.      |
| date         | LocalDateTime         | Date and time when the movement occurred.|
| performedBy  | User                  | User who performed the movement.        |

---

# ShoppingCart

## Description

Represents the provisional selection of products made by a buyer before confirming an order.

## Attributes

| Attribute  | Type             | Description                              |
| ---------- | ---------------- | --------------------------------------- |
| identifier | String           | Unique identifier of the shopping cart. |
| buyer      | Buyer            | Buyer who owns the cart.                |
| items      | List\<CartItem>  | Selected items. Empty by default.       |

---

# CartItem

## Description

Represents a single line within a shopping cart: a product with its quantity and price.

## Attributes

| Attribute | Type       | Description                                 |
| --------- | ---------- | ------------------------------------------- |
| product   | Product    | Product added to the cart.                  |
| quantity  | Integer    | Quantity of the product.                    |
| unitPrice | BigDecimal | Price of the product when added.            |

---

# Order

## Description

Represents the formal commercial commitment made by a buyer. Its lifecycle is the central process of the system.

## Lifecycle

```text
CART → PENDING_PAYMENT → PAID → SHIPPED → DELIVERED
```

## Attributes

| Attribute    | Type                | Description                             |
| ------------ | ------------------- | -------------------------------------- |
| identifier   | String              | Unique identifier of the order.        |
| buyer        | Buyer               | Buyer who places the order.            |
| details      | List\<OrderDetail>  | Order lines. Empty by default.         |
| status       | OrderStatus         | Current lifecycle status of the order. |
| creationDate | LocalDateTime       | Date and time when the order was created.|
| total        | BigDecimal          | Total value of the order.              |

## Relationships

* An `Order` is placed by one `Buyer`.
* An `Order` contains multiple `OrderDetail` instances.
* An `Order` may be billed by an `Invoice`, shipped by a `Shipment`, and may generate a `Return`.

---

# OrderDetail

## Description

Represents a single line within an order: a product with its quantity, unit price, and subtotal.

## Attributes

| Attribute | Type       | Description                              |
| --------- | ---------- | --------------------------------------- |
| product   | Product    | Product included in the order line.     |
| quantity  | Integer    | Quantity of the product.                |
| unitPrice | BigDecimal | Agreed unit price.                      |
| subtotal  | BigDecimal | Line subtotal (quantity × unit price).  |

---

# Invoice

## Description

Represents the commercial billing information associated with an order.

## Attributes

| Attribute   | Type          | Description                          |
| ----------- | ------------- | ----------------------------------- |
| identifier  | String        | Unique identifier of the invoice.   |
| order       | Order         | Order being billed.                 |
| issueDate   | LocalDateTime | Date and time the invoice was issued.|
| totalAmount | BigDecimal    | Total billed amount.                |
| status      | InvoiceStatus | Current status of the invoice.      |

---

# Shipment

## Description

Represents the logistics process for delivering a physical order.

## Attributes

| Attribute          | Type           | Description                                |
| ------------------ | -------------- | ----------------------------------------- |
| identifier         | String         | Unique identifier of the shipment.        |
| order              | Order          | Order being shipped.                      |
| originWarehouse    | Warehouse      | Warehouse the order ships from.           |
| destinationAddress | String         | Delivery address.                         |
| status             | ShipmentStatus | Current status of the shipment.           |
| logisticsOperator  | User           | Logistics operator responsible.           |
| shipmentDate       | LocalDateTime  | Date and time of dispatch.                |
| deliveryDate       | LocalDateTime  | Date and time of delivery.                |

---

# Return

## Description

Represents a return requested by a buyer for a previously placed order.

## Attributes

| Attribute   | Type          | Description                            |
| ----------- | ------------- | ------------------------------------- |
| identifier  | String        | Unique identifier of the return.      |
| order       | Order         | Order the return applies to.          |
| reason      | String        | Reason for the return.                |
| status      | ReturnStatus  | Current status of the return.         |
| requestDate | LocalDateTime | Date and time the return was requested.|

---

# Refund

## Description

Represents the reimbursement generated by an approved return.

## Attributes

| Attribute     | Type          | Description                          |
| ------------- | ------------- | ----------------------------------- |
| identifier    | String        | Unique identifier of the refund.    |
| returnRequest | Return        | Return that originates the refund.  |
| amount        | BigDecimal    | Amount to be refunded.              |
| status        | RefundStatus  | Current status of the refund.       |
| date          | LocalDateTime | Date and time of the refund.        |

---
# Related Documentation

The value objects, enumerations, and their design rules referenced throughout this model are documented separately in **Domain Value Objects.md**, following the same structure used across the SDD.
