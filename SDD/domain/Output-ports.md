# Output Ports

## Introduction

Output ports are Java interfaces that the domain uses to store and retrieve information, or to request technical services, without depending on any specific technology.

The domain services only know these interfaces. The concrete implementations (adapters for MySQL, MongoDB, password encryption, etc.) belong to the infrastructure layer and can be replaced without changing the domain.

All output ports are located in `application.domain.ports.out`.

---

# Conventions

| Method Pattern      | Meaning                                              | Return Type                          |
| ------------------- | ---------------------------------------------------- | ------------------------------------ |
| `save(entity)`      | Stores a new entity or updates an existing one.      | The stored entity.                   |
| `findBy...` (one)   | Looks for a single element that may not exist.      | `Optional<T>` (empty when not found) |
| `findBy...` (many)  | Looks for several elements.                          | `List<T>` (empty when none are found)|
| `existsBy...`       | Checks whether a value is already registered.        | `boolean`                            |

## Parameter Rule

Search methods receive only the value being searched (for example, an identifier as a `String`), not the complete domain object. This keeps each method clear about what it needs.

---

# Port Catalog

```text
PersonRepositoryPort
UserRepositoryPort
WarehouseRepositoryPort
ProductRepositoryPort
InventoryRepositoryPort
InventoryMovementRepositoryPort
ShoppingCartRepositoryPort
OrderRepositoryPort
InvoiceRepositoryPort
ShipmentRepositoryPort
ReturnRepositoryPort
RefundRepositoryPort
PasswordEncoderPort
```

---

# PersonRepositoryPort

Stores buyers, sellers, and employees. A single port is used for every kind of person because the identification and the email must be unique across the whole platform.

| Method                                | Description                                            |
| ------------------------------------- | ----------------------------------------------------- |
| save(Person)                          | Stores a person.                                      |
| findByIdentification(String)          | Returns the person with the given identification.    |
| existsByIdentification(String)        | Checks whether the identification is registered.     |
| existsByEmail(String)                 | Checks whether the email is registered.              |

---

# UserRepositoryPort

| Method                     | Description                                  |
| -------------------------- | ------------------------------------------- |
| save(User)                 | Stores a user.                              |
| findByUsername(String)     | Returns the user with the given username.   |
| existsByUsername(String)   | Checks whether the username is taken.       |
| findAll()                  | Returns all users.                          |

---

# WarehouseRepositoryPort

| Method                                  | Description                                |
| --------------------------------------- | ----------------------------------------- |
| save(Warehouse)                         | Stores a warehouse.                       |
| findByIdentifier(String)                | Returns the warehouse with the identifier.|
| findBySellerIdentification(String)      | Returns the warehouses of a seller.       |
| findAll()                               | Returns all warehouses.                   |

---

# ProductRepositoryPort

| Method                                  | Description                                                        |
| --------------------------------------- | ----------------------------------------------------------------- |
| save(Product)                           | Stores a product.                                                 |
| findByIdentifier(String)                | Returns the product with the identifier.                         |
| findByStatus(ProductStatus)             | Returns the products in a status. With `PUBLISHED` it returns the public catalog. |
| findBySellerIdentification(String)      | Returns the products of a seller.                                |

---

# InventoryRepositoryPort

A product can have stock in several warehouses, but only one inventory record per warehouse.

| Method                                             | Description                                           |
| -------------------------------------------------- | ---------------------------------------------------- |
| save(Inventory)                                    | Stores an inventory record.                          |
| findByIdentifier(String)                           | Returns the inventory record with the identifier.    |
| findByProductIdentifier(String)                    | Returns the inventory of a product in every warehouse.|
| findByProductAndWarehouse(String, String)          | Returns the inventory of a product in one warehouse. |
| findAll()                                          | Returns all inventory records.                       |

---

# InventoryMovementRepositoryPort

| Method                                   | Description                                        |
| ---------------------------------------- | ------------------------------------------------- |
| save(InventoryMovement)                  | Stores a movement.                                |
| findByInventoryIdentifier(String)        | Returns the movement history of an inventory.     |

---

# ShoppingCartRepositoryPort

| Method                                  | Description                         |
| --------------------------------------- | ---------------------------------- |
| save(ShoppingCart)                      | Stores a shopping cart.            |
| findByBuyerIdentification(String)       | Returns the cart of a buyer.       |

---

# OrderRepositoryPort

| Method                                  | Description                          |
| --------------------------------------- | ----------------------------------- |
| save(Order)                             | Stores an order.                    |
| findByIdentifier(String)                | Returns the order with the identifier.|
| findByBuyerIdentification(String)       | Returns the orders of a buyer.      |
| findByStatus(OrderStatus)               | Returns the orders in a status.     |
| findAll()                               | Returns all orders.                 |

---

# InvoiceRepositoryPort

| Method                             | Description                         |
| ---------------------------------- | ---------------------------------- |
| save(Invoice)                      | Stores an invoice.                 |
| findByOrderIdentifier(String)      | Returns the invoice of an order.   |
| findAll()                          | Returns all invoices.              |

---

# ShipmentRepositoryPort

| Method                             | Description                           |
| ---------------------------------- | ------------------------------------ |
| save(Shipment)                     | Stores a shipment.                   |
| findByIdentifier(String)           | Returns the shipment with the identifier.|
| findByOrderIdentifier(String)      | Returns the shipment of an order.    |
| findAll()                          | Returns all shipments.               |

---

# ReturnRepositoryPort

| Method                             | Description                            |
| ---------------------------------- | ------------------------------------- |
| save(Return)                       | Stores a return.                      |
| findByIdentifier(String)           | Returns the return with the identifier.|
| findByOrderIdentifier(String)      | Returns the returns of an order.      |
| findByStatus(ReturnStatus)         | Returns the returns in a status.      |

---

# RefundRepositoryPort

| Method                             | Description                             |
| ---------------------------------- | -------------------------------------- |
| save(Refund)                       | Stores a refund.                       |
| findByIdentifier(String)           | Returns the refund with the identifier.|
| findByReturnIdentifier(String)     | Returns the refund of a return.        |

---

# PasswordEncoderPort

Passwords are never stored as plain text. The domain only asks to encode a password and to verify it; the encryption algorithm is chosen by the adapter.

| Method                    | Description                                                  |
| ------------------------- | ----------------------------------------------------------- |
| encode(String)            | Returns the encrypted version of a raw password.           |
| matches(String, String)   | Checks whether a raw password matches an encrypted password.|
