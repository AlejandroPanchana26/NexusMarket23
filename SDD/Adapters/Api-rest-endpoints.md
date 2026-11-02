# REST API Endpoints

## 1. Overview

The REST API is the input adapter that exposes NexusMarket over HTTP. It has one controller per input port, and each controller groups the endpoints of one role.

Controllers contain no business rules. Every method does three things: convert the request DTO, call the input port, and convert the result into a response DTO.

The API has **94 endpoints**: one for each operation of the six input ports.

Base URL (local): `http://localhost:8080`

---

## 2. Conventions

| Topic | Convention |
| ----- | ---------- |
| Format | JSON in requests and responses (`Content-Type: application/json`). |
| Authentication | Every endpoint except `/api/public/**` and `/actuator/health` requires the header `Authorization: Bearer <token>`. The token is obtained from `POST /api/public/login`. |
| Path variables | `{name}` in a path identifies one resource (for example `{orderIdentifier}`). |
| Query parameters | `?status=...` filters a list. Valid values are the catalog codes (for example `APPROVED`). |
| DTOs | Requests and responses use DTOs (Java records). Domain models never leave the API: for example, `UserResponse` has no password field. |
| Download links | The public catalog never shows `downloadUrl`. Buyers see it in their order lines once the order is paid; sellers see it in their own product list. |

### Status Codes

| Code | Meaning |
| ---- | ------- |
| `200 OK` | Successful query or change. |
| `201 Created` | A resource was created (registrations, products, inventory, orders, shipments, returns, refunds). |
| `400 Bad Request` | Invalid request data, broken business rule, or status change not allowed. |
| `401 Unauthorized` | Missing, invalid or expired token. |
| `403 Forbidden` | The role cannot use the endpoint, or the user cannot act on that resource (for example, another seller's product). |
| `404 Not Found` | The resource or the address does not exist. |
| `405 Method Not Allowed` | Wrong HTTP method for the address. |
| `409 Conflict` | Duplicated data (identification, email, username, shipment, refund, inventory record). |
| `415 Unsupported Media Type` | The body was not sent as JSON. |
| `500 Internal Server Error` | Unexpected error. Details are written to the server log, not to the response. |

Error responses always have the same shape (`ErrorResponse`):

```json
{
  "status": 409,
  "error": "CONFLICT",
  "message": "Identification is already registered.",
  "timestamp": "2026-10-29T21:30:00.17"
}
```

---

## 3. Public Endpoints (`PublicAccessPort`)

Base path: `/api/public`. No token required.

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `POST` | `/api/public/register` | `RegisterBuyerRequest` | `UserResponse` | 201 |
| `POST` | `/api/public/login` | `LoginRequest` | `LoginResponse` | 200 |
| `GET` | `/api/public/products` | — | `List<ProductResponse>` | 200 |

---

## 4. Buyer Endpoints (`BuyerPort`)

Base path: `/api/buyer`. Role `BUYER`.

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `GET` | `/api/buyer/cart` | — | `CartResponse` | 200 |
| `POST` | `/api/buyer/cart/items` | `AddCartItemRequest` | `CartResponse` | 200 |
| `DELETE` | `/api/buyer/cart/items/{productIdentifier}` | — | `CartResponse` | 200 |
| `DELETE` | `/api/buyer/cart` | — | `CartResponse` | 200 |
| `POST` | `/api/buyer/orders` | — | `OrderResponse` | 201 |
| `PATCH` | `/api/buyer/orders/{orderIdentifier}/pay` | — | `OrderResponse` | 200 |
| `GET` | `/api/buyer/orders` | — | `List<OrderResponse>` | 200 |
| `GET` | `/api/buyer/orders/{orderIdentifier}/invoice` | — | `InvoiceResponse` | 200 |
| `GET` | `/api/buyer/orders/{orderIdentifier}/shipment` | — | `ShipmentResponse` | 200 |
| `POST` | `/api/buyer/orders/{orderIdentifier}/returns` | `ReturnReasonRequest` | `ReturnResponse` | 201 |
| `GET` | `/api/buyer/orders/{orderIdentifier}/returns` | — | `List<ReturnResponse>` | 200 |
| `GET` | `/api/buyer/returns/{returnIdentifier}/refund` | — | `RefundResponse` | 200 |

---

## 5. Seller Endpoints (`SellerPort`)

Base path: `/api/seller`. Role `SELLER`.

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `POST` | `/api/seller/products` | `CreateProductRequest` | `ProductResponse` | 201 |
| `PATCH` | `/api/seller/products/{productIdentifier}/publish` | — | `ProductResponse` | 200 |
| `PATCH` | `/api/seller/products/{productIdentifier}/suspend` | — | `ProductResponse` | 200 |
| `PATCH` | `/api/seller/products/{productIdentifier}/discontinue` | — | `ProductResponse` | 200 |
| `PATCH` | `/api/seller/products/{productIdentifier}/price` | `ChangePriceRequest` | `ProductResponse` | 200 |
| `POST` | `/api/seller/products/{productIdentifier}/variants` | `VariantRequest` | `ProductResponse` | 200 |
| `GET` | `/api/seller/products` | — | `List<ProductResponse>` | 200 |
| `GET` | `/api/seller/warehouses` | — | `List<WarehouseResponse>` | 200 |
| `POST` | `/api/seller/inventory` | `RegisterInventoryRequest` | `InventoryResponse` | 201 |
| `PATCH` | `/api/seller/inventory/{inventoryIdentifier}/add-stock` | `QuantityRequest` | `InventoryResponse` | 200 |
| `PATCH` | `/api/seller/inventory/{inventoryIdentifier}/adjust` | `QuantityRequest` | `InventoryResponse` | 200 |
| `PATCH` | `/api/seller/inventory/{inventoryIdentifier}/damaged` | — | `InventoryResponse` | 200 |
| `GET` | `/api/seller/products/{productIdentifier}/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/seller/inventory/{inventoryIdentifier}/movements` | — | `List<InventoryMovementResponse>` | 200 |
| `GET` | `/api/seller/orders` | — | `List<OrderResponse>` | 200 |
| `PATCH` | `/api/seller/returns/{returnIdentifier}/approve` | — | `ReturnResponse` | 200 |
| `PATCH` | `/api/seller/returns/{returnIdentifier}/reject` | — | `ReturnResponse` | 200 |
| `POST` | `/api/seller/returns/{returnIdentifier}/refund` | `RefundRequest` | `RefundResponse` | 201 |

---

## 6. Logistics Operator Endpoints (`LogisticsOperatorPort`)

Base path: `/api/operator`. Role `LOGISTICS_OPERATOR`.

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `GET` | `/api/operator/warehouses` | — | `List<WarehouseResponse>` | 200 |
| `POST` | `/api/operator/inventory` | `RegisterInventoryRequest` | `InventoryResponse` | 201 |
| `PATCH` | `/api/operator/inventory/{inventoryIdentifier}/add-stock` | `QuantityRequest` | `InventoryResponse` | 200 |
| `PATCH` | `/api/operator/inventory/{inventoryIdentifier}/adjust` | `QuantityRequest` | `InventoryResponse` | 200 |
| `PATCH` | `/api/operator/inventory/{inventoryIdentifier}/damaged` | — | `InventoryResponse` | 200 |
| `GET` | `/api/operator/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/operator/products/{productIdentifier}/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/operator/inventory/{inventoryIdentifier}/movements` | — | `List<InventoryMovementResponse>` | 200 |
| `GET` | `/api/operator/orders` | — | `List<OrderResponse>` | 200 |
| `POST` | `/api/operator/orders/{orderIdentifier}/shipment` | `PrepareShipmentRequest` (optional) | `ShipmentResponse` | 201 |
| `PATCH` | `/api/operator/shipments/{shipmentIdentifier}/dispatch` | — | `ShipmentResponse` | 200 |
| `PATCH` | `/api/operator/shipments/{shipmentIdentifier}/in-transit` | — | `ShipmentResponse` | 200 |
| `PATCH` | `/api/operator/shipments/{shipmentIdentifier}/deliver` | — | `ShipmentResponse` | 200 |
| `GET` | `/api/operator/shipments` | — | `List<ShipmentResponse>` | 200 |
| `GET` | `/api/operator/orders/{orderIdentifier}/shipment` | — | `ShipmentResponse` | 200 |
| `GET` | `/api/operator/returns?status=...` | — | `List<ReturnResponse>` | 200 |
| `PATCH` | `/api/operator/returns/{returnIdentifier}/complete` | — | `ReturnResponse` | 200 |

---

## 7. Administrator Endpoints (`AdministratorPort`)

Base path: `/api/admin`. Role `ADMINISTRATOR`.

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `POST` | `/api/admin/sellers` | `RegisterSellerRequest` | `UserResponse` | 201 |
| `POST` | `/api/admin/employees` | `RegisterEmployeeRequest` | `UserResponse` | 201 |
| `POST` | `/api/admin/warehouses` | `RegisterWarehouseRequest` | `WarehouseResponse` | 201 |
| `GET` | `/api/admin/users` | — | `List<UserResponse>` | 200 |
| `PATCH` | `/api/admin/users/{username}/block` | — | `UserResponse` | 200 |
| `PATCH` | `/api/admin/users/{username}/deactivate` | — | `UserResponse` | 200 |
| `PATCH` | `/api/admin/users/{username}/activate` | — | `UserResponse` | 200 |
| `PATCH` | `/api/admin/sellers/{identification}/suspend` | — | `PersonStatusResponse` | 200 |
| `PATCH` | `/api/admin/sellers/{identification}/activate` | — | `PersonStatusResponse` | 200 |
| `PATCH` | `/api/admin/sellers/{identification}/deactivate` | — | `PersonStatusResponse` | 200 |
| `PATCH` | `/api/admin/buyers/{identification}/suspend` | — | `PersonStatusResponse` | 200 |
| `PATCH` | `/api/admin/buyers/{identification}/activate` | — | `PersonStatusResponse` | 200 |
| `PATCH` | `/api/admin/buyers/{identification}/block` | — | `PersonStatusResponse` | 200 |
| `GET` | `/api/admin/warehouses` | — | `List<WarehouseResponse>` | 200 |
| `GET` | `/api/admin/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/admin/orders` | — | `List<OrderResponse>` | 200 |
| `GET` | `/api/admin/orders/{orderIdentifier}/invoice` | — | `InvoiceResponse` | 200 |
| `GET` | `/api/admin/shipments` | — | `List<ShipmentResponse>` | 200 |
| `GET` | `/api/admin/returns?status=...` | — | `List<ReturnResponse>` | 200 |
| `PATCH` | `/api/admin/returns/{returnIdentifier}/approve` | — | `ReturnResponse` | 200 |
| `PATCH` | `/api/admin/returns/{returnIdentifier}/reject` | — | `ReturnResponse` | 200 |
| `PATCH` | `/api/admin/returns/{returnIdentifier}/complete` | — | `ReturnResponse` | 200 |
| `POST` | `/api/admin/returns/{returnIdentifier}/refund` | `RefundRequest` | `RefundResponse` | 201 |
| `PATCH` | `/api/admin/refunds/{refundIdentifier}/process` | — | `RefundResponse` | 200 |
| `PATCH` | `/api/admin/refunds/{refundIdentifier}/reject` | — | `RefundResponse` | 200 |
| `GET` | `/api/admin/reports/orders-count?status=...` | — | `OrderCountResponse` | 200 |
| `GET` | `/api/admin/reports/total-sales` | — | `TotalSalesResponse` | 200 |
| `GET` | `/api/admin/reports/damaged-inventory` | — | `List<InventoryResponse>` | 200 |

---

## 8. Supervisor Endpoints (`SupervisorPort`)

Base path: `/api/supervisor`. Role `SUPERVISOR`. All endpoints are read-only (`GET`).

| Method | Path | Request Body | Response | Success |
| ------ | ---- | ------------ | -------- | ------- |
| `GET` | `/api/supervisor/users` | — | `List<UserResponse>` | 200 |
| `GET` | `/api/supervisor/warehouses` | — | `List<WarehouseResponse>` | 200 |
| `GET` | `/api/supervisor/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/supervisor/products/{productIdentifier}/inventory` | — | `List<InventoryResponse>` | 200 |
| `GET` | `/api/supervisor/inventory/{inventoryIdentifier}/movements` | — | `List<InventoryMovementResponse>` | 200 |
| `GET` | `/api/supervisor/orders` | — | `List<OrderResponse>` | 200 |
| `GET` | `/api/supervisor/orders/by-status?status=...` | — | `List<OrderResponse>` | 200 |
| `GET` | `/api/supervisor/orders/{orderIdentifier}/invoice` | — | `InvoiceResponse` | 200 |
| `GET` | `/api/supervisor/shipments` | — | `List<ShipmentResponse>` | 200 |
| `GET` | `/api/supervisor/orders/{orderIdentifier}/shipment` | — | `ShipmentResponse` | 200 |
| `GET` | `/api/supervisor/returns?status=...` | — | `List<ReturnResponse>` | 200 |
| `GET` | `/api/supervisor/orders/{orderIdentifier}/returns` | — | `List<ReturnResponse>` | 200 |
| `GET` | `/api/supervisor/returns/{returnIdentifier}/refund` | — | `RefundResponse` | 200 |
| `GET` | `/api/supervisor/reports/orders-count?status=...` | — | `OrderCountResponse` | 200 |
| `GET` | `/api/supervisor/reports/total-sales` | — | `TotalSalesResponse` | 200 |
| `GET` | `/api/supervisor/reports/damaged-inventory` | — | `List<InventoryResponse>` | 200 |

---

## 9. DTOs

The fields of every request and response DTO are described in `Rest-adapters.md`, and the validation rules of the request DTOs in `Rest-validation.md`.
