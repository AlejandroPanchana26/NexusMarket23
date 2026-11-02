# REST Adapters

## 1. Overview

The REST adapters are the input side of the hexagonal architecture: they receive HTTP requests, convert them, and call the input ports of the domain. They are located in `application.adapters.rest`.

---

## 2. Structure

```text
adapters/rest
├── controllers     ← one controller per input port (6)
├── dtos
│   ├── requests    ← data that enters the API
│   └── responses   ← data that leaves the API
├── mappers         ← conversion between DTOs and domain models
└── exception       ← GlobalExceptionHandler and ErrorResponse
```

| Controller | Input Port | Base Path |
| ---------- | ---------- | --------- |
| `PublicRestController` | `PublicAccessPort` | `/api/public` |
| `BuyerRestController` | `BuyerPort` | `/api/buyer` |
| `SellerRestController` | `SellerPort` | `/api/seller` |
| `LogisticsOperatorRestController` | `LogisticsOperatorPort` | `/api/operator` |
| `AdministratorRestController` | `AdministratorPort` | `/api/admin` |
| `SupervisorRestController` | `SupervisorPort` | `/api/supervisor` |

---

## 3. Controller Pattern

Controllers contain no business rules. Every method follows the same three steps: take the data of the request, call the input port, and convert the result.

```java
@PatchMapping("/orders/{orderIdentifier}/pay")
public OrderResponse payOrder(@PathVariable String orderIdentifier) {
    return OrderRestMapper.toBuyerResponse(
            buyerPort.payOrder(currentUserProvider.getCurrentUser(), orderIdentifier));
}
```

| Annotation | Meaning |
| ---------- | ------- |
| `@RestController`, `@RequestMapping` | The class handles the addresses that start with the base path. |
| `@GetMapping`, `@PostMapping`, `@PatchMapping`, `@DeleteMapping` | HTTP method and address of each endpoint. |
| `@PathVariable` | Value taken from the address (identifies one resource). |
| `@RequestParam` | Value taken from the query string (filters a list, for example `?status=APPROVED`). |
| `@RequestBody` | The JSON body converted into a request DTO. |
| `@Valid` | Checks the validation annotations of the DTO before entering the method. |

Endpoints that create something return `201 Created` through `ResponseEntity.status(HttpStatus.CREATED)`; the others return `200 OK`.

---

## 4. Authenticated User

Controllers obtain the user that makes the request with `CurrentUserProvider.getCurrentUser()`. The user was identified from the JWT token by `JwtAuthenticationFilter` (see `Rest-security-cors.md`). The controllers never read the token themselves, so the authentication mechanism can change without modifying them.

---

## 5. DTOs

DTOs are Java records: classes that only carry data. They protect the domain in both directions:

* **Domain models never leave the API.** For example, `User` has a password and `Seller` has a list of products that point back to the seller; returning them directly would expose the password and create infinite JSON cycles.
* **DTOs never enter the domain.** Mappers convert them into domain objects before calling the input ports.

### Request DTOs

The validation rules of each request DTO are explained in `Rest-validation.md`.

| DTO | Fields |
| --- | ------ |
| `AddCartItemRequest` | `productIdentifier` (String) @NotBlank; `quantity` (Integer) @NotNull @Positive |
| `ChangePriceRequest` | `price` (BigDecimal) @NotNull @Positive |
| `CreateProductRequest` | `type` (String) @NotBlank; `name` (String) @NotBlank; `description` (String); `price` (BigDecimal) @NotNull @Positive; `weight` (BigDecimal); `downloadUrl` (String); `variants` (List<String>) |
| `LoginRequest` | `username` (String) @NotBlank; `password` (String) @NotBlank |
| `PrepareShipmentRequest` | `destinationAddress` (String) |
| `QuantityRequest` | `quantity` (Integer) @NotNull @PositiveOrZero |
| `RefundRequest` | `amount` (BigDecimal) @NotNull @Positive |
| `RegisterBuyerRequest` | `identification` (String) @NotBlank; `name` (String) @NotBlank; `email` (String) @NotBlank @Email; `phoneNumber` (String); `principalAddress` (String) @NotBlank; `additionalAddresses` (List<String>); `username` (String) @NotBlank; `password` (String) @NotBlank |
| `RegisterEmployeeRequest` | `identification` (String) @NotBlank; `name` (String) @NotBlank; `email` (String) @NotBlank @Email; `phoneNumber` (String); `role` (String) @NotBlank; `username` (String) @NotBlank; `password` (String) @NotBlank |
| `RegisterInventoryRequest` | `productIdentifier` (String) @NotBlank; `warehouseIdentifier` (String) @NotBlank; `initialQuantity` (Integer) @NotNull @PositiveOrZero |
| `RegisterSellerRequest` | `identification` (String) @NotBlank; `name` (String) @NotBlank; `email` (String) @NotBlank @Email; `phoneNumber` (String); `username` (String) @NotBlank; `password` (String) @NotBlank; `warehouseName` (String) @NotBlank; `warehouseAddress` (String) @NotBlank |
| `RegisterWarehouseRequest` | `name` (String) @NotBlank; `address` (String) @NotBlank; `sellerIdentification` (String) |
| `ReturnReasonRequest` | `reason` (String) @NotBlank |
| `VariantRequest` | `variant` (String) @NotBlank |

### Response DTOs

| DTO | Fields |
| --- | ------ |
| `CartItemResponse` | `productIdentifier` (String); `productName` (String); `quantity` (Integer); `unitPrice` (BigDecimal); `subtotal` (BigDecimal) |
| `CartResponse` | `cartIdentifier` (String); `items` (List<CartItemResponse>); `total` (BigDecimal) |
| `InventoryMovementResponse` | `identifier` (String); `movementType` (String); `quantity` (Integer); `date` (LocalDateTime); `performedBy` (String) |
| `InventoryResponse` | `identifier` (String); `productIdentifier` (String); `productName` (String); `warehouseIdentifier` (String); `warehouseName` (String); `availableQuantity` (Integer); `reservedQuantity` (Integer); `status` (String) |
| `InvoiceResponse` | `identifier` (String); `orderIdentifier` (String); `issueDate` (LocalDateTime); `totalAmount` (BigDecimal); `status` (String) |
| `LoginResponse` | `token` (String); `tokenType` (String); `expiresInMinutes` (long); `user` (UserResponse) |
| `OrderCountResponse` | `status` (String); `count` (int) |
| `OrderLineResponse` | `productIdentifier` (String); `productName` (String); `productType` (String); `quantity` (Integer); `unitPrice` (BigDecimal); `subtotal` (BigDecimal); `downloadUrl` (String) |
| `OrderResponse` | `identifier` (String); `status` (String); `creationDate` (LocalDateTime); `total` (BigDecimal); `buyerIdentification` (String); `fulfillmentWarehouseName` (String); `lines` (List<OrderLineResponse>) |
| `PersonStatusResponse` | `identification` (String); `name` (String); `role` (String); `status` (String) |
| `ProductResponse` | `identifier` (String); `type` (String); `name` (String); `description` (String); `price` (BigDecimal); `status` (String); `variants` (List<String>); `sellerIdentification` (String); `sellerName` (String); `weight` (BigDecimal); `downloadUrl` (String) |
| `RefundResponse` | `identifier` (String); `returnIdentifier` (String); `amount` (BigDecimal); `status` (String); `date` (LocalDateTime) |
| `ReturnResponse` | `identifier` (String); `orderIdentifier` (String); `reason` (String); `status` (String); `requestDate` (LocalDateTime) |
| `ShipmentResponse` | `identifier` (String); `orderIdentifier` (String); `originWarehouseName` (String); `destinationAddress` (String); `status` (String); `logisticsOperatorUsername` (String); `shipmentDate` (LocalDateTime); `deliveryDate` (LocalDateTime) |
| `TotalSalesResponse` | `totalSales` (BigDecimal) |
| `UserResponse` | `userId` (Integer); `username` (String); `status` (String); `role` (String); `identification` (String); `name` (String); `email` (String) |
| `WarehouseResponse` | `identifier` (String); `name` (String); `address` (String); `type` (String); `ownerIdentification` (String); `ownerName` (String) |

---

## 6. Mappers

| Mapper | Converts |
| ------ | -------- |
| `UserRestMapper` | Users and persons; registration requests into `Buyer`, `Seller` and `Employee`; commercial statuses |
| `ProductRestMapper` | Products; creation requests into `PhysicalProduct` or `DigitalProduct`. `toPublicResponse` never includes the download link |
| `WarehouseRestMapper` | Warehouses |
| `InventoryRestMapper` | Inventory records and their movement history |
| `CartRestMapper` | Shopping carts |
| `OrderRestMapper` | Orders and invoices. `toBuyerResponse` includes download links only for paid orders; text statuses into `OrderStatus` |
| `ShipmentRestMapper` | Shipments |
| `PostSaleRestMapper` | Returns and refunds; text statuses into `ReturnStatus` |

Catalog codes (for example `"PAID"`) are converted with `CatalogMapper`, the same helper used by the persistence adapters.
