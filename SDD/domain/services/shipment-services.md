# Shipment Services

Preparation, dispatch, transport and delivery of orders. Package: `application.domain.services.shipment`.

General rules, the `AccessValidator` helper and the access matrix are described in `SDD/domain/Domain Services.md`.

---

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
