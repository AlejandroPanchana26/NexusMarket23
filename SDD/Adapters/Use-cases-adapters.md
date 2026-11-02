# Use Case Adapters

## 1. Overview

Use case adapters implement the input ports of the domain. They are located in `application.adapters.useCases`, one class per input port.

```text
REST controller ──uses──> Input port (domain) <──implements── Use case ──calls──> Domain services
```

---

## 2. Responsibilities

* **Implement one input port.** `BuyerUseCase implements BuyerPort`, `SellerUseCase implements SellerPort`, and so on.
* **Delegate only.** Each method calls the domain service that performs the operation, in a single line. Use cases contain no business rules and no validations: the rules live in the models and the services.
* **Receive the services through the constructor.** Each use case receives only the services its port needs. Spring creates it because it is annotated with `@Component`.

Example:

```java
@Override
public Order placeOrder(User user) {
    return placeOrderService.placeOrder(user);
}
```

---

## 3. Why They Exist

Without use cases, each controller would depend on many services (the administrator controller would need fifteen). With them, each controller depends on **one** input port, which works as the menu of its role. This keeps the controllers simple and makes it clear, by reading a single interface, what each role can do.

---

## 4. Catalog

| Use Case | Implements | Services Used |
| -------- | ---------- | ------------- |
| `PublicAccessUseCase` | `PublicAccessPort` | 3 |
| `BuyerUseCase` | `BuyerPort` | 7 |
| `SellerUseCase` | `SellerPort` | 10 |
| `LogisticsOperatorUseCase` | `LogisticsOperatorPort` | 9 |
| `AdministratorUseCase` | `AdministratorPort` | 15 |
| `SupervisorUseCase` | `SupervisorPort` | 7 |

The complete list of operations and the service each one delegates to is in `SDD/domain/Input-ports.md`.
