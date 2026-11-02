# Post-Sale Services

Returns and refunds. Package: `application.domain.services.postsale`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

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
