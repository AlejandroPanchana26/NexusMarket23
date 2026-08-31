# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and are used to encapsulate controlled business concepts, improve domain expressiveness, and prevent the use of primitive values or scattered string literals throughout the application.

The marketplace domain uses Value Objects for business catalogs such as roles, statuses, warehouse types, and inventory movement types.

All business catalogs inherit from `DomainCatalog`.

---

# Value Object Hierarchy

```text
DomainCatalog (Abstract)
├── SystemRole
├── UserStatus
├── BuyerStatus
├── SellerStatus
├── ProductStatus
├── WarehouseType
├── InventoryStatus
├── InventoryMovementType
├── OrderStatus
├── InvoiceStatus
├── ShipmentStatus
├── ReturnStatus
└── RefundStatus
```

---

# DomainCatalog (Abstract)

## Description

Represents a generic business catalog used throughout the marketplace domain.

`DomainCatalog` provides a consistent structure for controlled business values that require a code, human-readable name, and business description.

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type   | Description                                            |
| ----------- | ------ | ----------------------------------------------------- |
| code        | String | Unique business identifier of the catalog value.      |
| name        | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value.             |

## Characteristics

* Immutable.
* Equality is determined by value (the `code`) rather than object identity.
* Catalog values are controlled by the domain.
* Catalog values must not be represented by arbitrary strings throughout the application.
* Each catalog value must have a unique `code`.

---

# SystemRole

## Description

Represents the responsibilities and permissions assigned to a person within the marketplace.

The role is a characteristic of `Person` because it represents what the person means within the system and the responsibilities associated with that person.

The `role` attribute is therefore defined in `Person` and inherited by its specializations, `Buyer` and `Seller`.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code               | Name               | Description                                                          |
| ------------------ | ------------------ | ------------------------------------------------------------------- |
| BUYER              | Buyer              | Person who purchases products published in the marketplace.         |
| SELLER             | Seller             | Person responsible for registering and managing their own products. |
| LOGISTICS_OPERATOR | Logistics Operator | User in charge of the physical operation of warehouses and shipments.|
| ADMINISTRATOR      | Administrator      | User responsible for managing sellers and warehouses.               |
| SUPERVISOR         | Supervisor         | Consultation and operational monitoring profile.                    |

---

# UserStatus

## Description

Represents the current status of a user's access to the marketplace system.

`UserStatus` is independent from the commercial status of the associated person. A user may be blocked or inactive while the associated person remains commercially active.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                        |
| -------- | -------- | ------------------------------------------------- |
| ACTIVE   | Active   | User can access the system normally.              |
| INACTIVE | Inactive | User exists but cannot perform system operations. |
| BLOCKED  | Blocked  | User access has been suspended.                   |

---

# BuyerStatus

## Description

Represents the commercial status of a buyer within the marketplace.

It determines whether the buyer is enabled to place orders and is independent from the associated user's system access status.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                              |
| --------- | --------- | ------------------------------------------------------- |
| ACTIVE    | Active    | Buyer is enabled to place orders in the marketplace.    |
| SUSPENDED | Suspended | Buyer is temporarily disabled from placing orders.      |
| BLOCKED   | Blocked   | Buyer's commercial activity has been permanently disabled.|

---

# SellerStatus

## Description

Represents the operational status of a seller within the marketplace.

It determines whether the seller is enabled to publish and manage products.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                          |
| --------- | --------- | --------------------------------------------------- |
| ACTIVE    | Active    | Seller is enabled to publish and manage products.   |
| SUSPENDED | Suspended | Seller is temporarily disabled from selling.        |
| INACTIVE  | Inactive  | Seller exists but is not currently operating.       |

---

# ProductStatus

## Description

Represents the catalog state of a product.

The status describes the current visibility and availability of the product within the public catalog.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code         | Name         | Description                                              |
| ------------ | ------------ | ------------------------------------------------------- |
| PUBLISHED    | Published    | Product is visible in the public catalog and available. |
| SUSPENDED    | Suspended    | Product is temporarily not visible in the catalog.      |
| DISCONTINUED | Discontinued | Product has been permanently removed from the catalog.  |

## Lifecycle

```text
PUBLISHED
   │
   ├──────────────> SUSPENDED
   │                    │
   │ <──────────────────┘
   │
   ▼
DISCONTINUED
```

---

# WarehouseType

## Description

Represents the classification of a warehouse based on ownership.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name                  | Description                                         |
| ----------- | --------------------- | -------------------------------------------------- |
| MARKETPLACE | Marketplace Warehouse | Warehouse managed directly by the platform.        |
| SELLER      | Seller Warehouse      | Warehouse owned and managed by a seller.           |

---

# InventoryStatus

## Description

Represents the operational state of an inventory record.

A key business rule depends on this value: stock marked as `DAMAGED` cannot be reserved.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                       |
| --------- | --------- | ------------------------------------------------ |
| AVAILABLE | Available | Stock available to be reserved or sold.          |
| RESERVED  | Reserved  | Stock set aside for an order in progress.        |
| DAMAGED   | Damaged   | Stock not fit for sale; cannot be reserved.      |

---

# InventoryMovementType

## Description

Represents the type of movement recorded over an inventory record.

While an inventory status represents the current stock state, a movement type represents the kind of action that occurred.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                       |
| ----------- | ----------- | ------------------------------------------------ |
| ENTRY       | Entry       | Entry of new stock into the warehouse.           |
| RESERVATION | Reservation | Stock set aside for an order in progress.        |
| SALE_EXIT   | Sale Exit   | Stock leaving the warehouse due to a sale.       |
| ADJUSTMENT  | Adjustment  | Manual correction of recorded stock.             |
| RETURN      | Return      | Stock re-entering due to a return.               |

---

# OrderStatus

## Description

Represents the lifecycle state of an order.

The order lifecycle is the central process of the marketplace and reflects the progression from provisional selection to final delivery.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code            | Name             | Description                                        |
| --------------- | ---------------- | ------------------------------------------------- |
| CART            | Cart             | Provisional selection of products.                |
| PENDING_PAYMENT | Pending Payment  | Order awaits payment confirmation.                |
| PAID            | Paid             | Payment confirmed; fulfillment processes begin.   |
| SHIPPED         | Shipped          | Order has physically left the warehouse.          |
| DELIVERED       | Delivered        | Order delivered and considered finalized.         |

## Lifecycle

```text
CART
   │
   ▼
PENDING_PAYMENT
   │
   ▼
PAID
   │
   ▼
SHIPPED
   │
   ▼
DELIVERED
```

---

# InvoiceStatus

## Description

Represents the state of an invoice associated with an order.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                             |
| --------- | --------- | -------------------------------------- |
| ISSUED    | Issued    | Invoice generated for the order.       |
| PAID      | Paid      | Invoice paid by the buyer.             |
| CANCELLED | Cancelled | Invoice cancelled and no longer valid. |

## Lifecycle

```text
ISSUED
   │
   ├──────────────> CANCELLED
   │
   ▼
PAID
```

---

# ShipmentStatus

## Description

Represents the state of a shipment throughout the logistics process.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name        | Description                                  |
| ---------- | ----------- | ------------------------------------------- |
| PREPARING  | Preparing   | Order is being packed at the warehouse.     |
| SHIPPED    | Shipped     | Order has left the warehouse.               |
| IN_TRANSIT | In Transit  | Order is on its way to the buyer.           |
| DELIVERED  | Delivered   | Order delivered to the buyer.               |

## Lifecycle

```text
PREPARING
   │
   ▼
SHIPPED
   │
   ▼
IN_TRANSIT
   │
   ▼
DELIVERED
```

---

# ReturnStatus

## Description

Represents the state of a return requested by a buyer.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                                |
| --------- | --------- | ----------------------------------------- |
| REQUESTED | Requested | Buyer requested the return.               |
| APPROVED  | Approved  | Return approved and may proceed.          |
| REJECTED  | Rejected  | Return rejected.                          |
| COMPLETED | Completed | Return completed successfully.            |

## Lifecycle

```text
REQUESTED
   │
   ├──────────────> REJECTED
   │
   ▼
APPROVED
   │
   ▼
COMPLETED
```

---

# RefundStatus

## Description

Represents the state of a refund generated by an approved return.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code      | Name      | Description                              |
| --------- | --------- | --------------------------------------- |
| PENDING   | Pending   | Refund pending to be processed.         |
| PROCESSED | Processed | Refund delivered to the buyer.          |
| REJECTED  | Rejected  | Refund rejected.                        |

## Lifecycle

```text
PENDING
   │
   ├──────────────> REJECTED
   │
   ▼
PROCESSED
```

---

# Primitive Enumerations

The following concept is represented as a primitive enumeration because it contains fixed technical values and does not require business catalog metadata such as `code`, `name`, or `description`.

---

# ProductType

## Description

Represents the technical classification of a product, which determines whether it participates in inventory and shipping.

## Values

```text
PHYSICAL
DIGITAL
```

---

# Value Object Design Rules

## Immutability

All Value Objects must be immutable after creation. Their values cannot be modified once the object has been instantiated.

## Equality

Value Objects are compared according to their values (the `code`) rather than object identity. Two instances containing the same `code` represent the same Value Object.

## Controlled Values

Business catalogs must use controlled values defined by the domain. The application must avoid replacing these concepts with arbitrary strings such as:

```text
"ACTIVE"
"PUBLISHED"
"DELIVERED"
```

throughout the codebase. Instead, the corresponding Value Object must be used:

```text
BuyerStatus
ProductStatus
OrderStatus
```

## Business Versus Technical Enumerations

A business concept should be modeled as a `DomainCatalog` Value Object when it requires:

* a business code;
* a display name;
* a business description;
* controlled domain evolution.

A simple enumeration should be used when the concept represents a fixed technical value without additional business metadata, such as `ProductType`.

## Relationship With Entities

Entities reference Value Objects rather than primitive strings whenever the referenced value represents a controlled business concept.

Examples:

```text
Person.role : SystemRole

Buyer.commercialStatus : BuyerStatus

Seller.sellerStatus : SellerStatus

User.status : UserStatus

Product.status : ProductStatus

Warehouse.type : WarehouseType

Inventory.status : InventoryStatus

Order.status : OrderStatus
```

This approach improves type safety, domain expressiveness, maintainability, and consistency with Domain-Driven Design principles.
