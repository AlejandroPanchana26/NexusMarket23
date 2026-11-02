# REST Validation

## 1. Purpose

Request data is validated in two places:

| Where | What | Result When It Fails |
| ----- | ---- | -------------------- |
| Request DTOs (Bean Validation) | Format of the data: required fields, valid email, positive numbers | `400 Bad Request` listing the invalid fields |
| Domain models and services | Business rules: unique data, stock, statuses, ownership | `400`, `403`, `404` or `409`, depending on the rule |

The DTO validation stops malformed data at the door, before it reaches the domain. Business rules are never moved to the DTOs: they stay in the domain.

---

## 2. Processing Order

1. Spring converts the JSON body into the request DTO. If the body is missing or is not valid JSON, the response is `400`.
2. `@Valid` checks the annotations of the DTO. If a field is invalid, the response is `400` with the field names (`MethodArgumentNotValidException`).
3. The controller converts the DTO into domain objects and calls the input port.
4. The domain validates the business rules and throws a domain exception when one is broken (see `Global-exception-handler.md`).

---

## 3. Annotations

| Annotation | Rule |
| ---------- | ---- |
| `@NotBlank` | Required text, not empty. |
| `@NotNull` | Required value. |
| `@Email` | Valid email format. |
| `@Positive` | Greater than zero. |
| `@PositiveOrZero` | Zero or greater. |

---

## 4. Request DTO Rules

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

---

## 5. Examples

| Request | Problem | Who Rejects It | Response |
| ------- | ------- | -------------- | -------- |
| Register buyer with `"email": ""` | Empty required field | `@NotBlank` in `RegisterBuyerRequest` | `400` |
| Add to cart with `"quantity": 0` | Not positive | `@Positive` in `AddCartItemRequest` | `400` |
| Create product with `"type": "SERVICE"` | Unknown product type | `ProductRestMapper.toProduct` | `400` |
| Add stock with `"quantity": 0` | Stock increments must be positive | `Inventory.addStock` (domain) | `400` |
| Register buyer with an email already used | Duplicated data | `UserAccountService` (domain) | `409` |
