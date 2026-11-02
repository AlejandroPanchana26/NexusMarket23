# Purchase Services

Shopping cart, order placement, payment and order consultation. Package: `application.domain.services.purchase`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

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
