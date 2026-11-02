# Domain Services

## Introduction

Domain services coordinate the operations of NexusMarket that involve several entities or require access to stored information.

Entities protect their own rules (for example, `Inventory.reserve` never allows negative stock). Services execute the steps of a complete business operation in the correct order and use the output ports to read and store data. Services never duplicate the rules that already belong to the entities; they call the entity methods instead.

All services are located in `application.domain.services` and are grouped in one folder per business area.

---

# General Design Rules

* **Validate first, store last.** Every service validates all the rules before calling any `save` method, so a failed operation never leaves partial data.
* **Ports through the constructor.** Each service receives the output ports it needs through its constructor and is marked with `@Service` so that Spring can create it.
* **Authenticated user and role (RG-01, RG-03).** Every operation, except public registration, login, and the public catalog, receives the authenticated `User` and validates the allowed roles with `AccessValidator`.
* **Own information only (RG-03).** Buyers and sellers can only act on their own information. Administrators, logistics operators, and supervisors act according to their role.
* **Generated identifiers.** Services assign the identifier of new entities with `UUID.randomUUID()`. Person identification is provided by the person.

---

# Service Structure

```text
services
├── AccessValidator
├── identity
│   ├── UserAccountService
│   ├── PersonLookupService
│   ├── RegisterBuyerService
│   ├── RegisterSellerService
│   ├── RegisterEmployeeService
│   ├── LoginService
│   ├── ManageUserStatusService
│   ├── ManageBuyerStatusService
│   ├── ManageSellerStatusService
│   └── ConsultUsersService
├── warehouse
│   ├── RegisterWarehouseService
│   └── ConsultWarehousesService
├── catalog
│   ├── CreateProductService
│   ├── ManageProductService
│   └── ConsultCatalogService
├── inventory
│   ├── RegisterInventoryService
│   ├── ManageStockService
│   └── ConsultInventoryService
├── purchase
│   ├── ManageCartService
│   ├── PlaceOrderService
│   ├── PayOrderService
│   └── ConsultOrdersService
├── shipment
│   ├── ShipOrderService
│   └── ConsultShipmentsService
├── postsale
│   ├── ManageReturnService
│   ├── ManageRefundService
│   └── ConsultReturnsService
└── report
    └── SupervisorReportService
```

---

# AccessValidator

Helper class with static methods. It is not instantiated.

| Method                                         | Business Rule                                                                              |
| ---------------------------------------------- | ----------------------------------------------------------------------------------------- |
| requireRole(User, SystemRole...)               | The user must exist, be active, and have one of the allowed roles (RG-01, RG-03).        |
| requireOwnerIfSeller(User, Seller)             | If the user is a seller, the given seller must be that same user. Other roles are not restricted by this check. |

---

# Services by Area

Each area has its own document in `SDD/domain/services/`.

| Area | Package | Services | Document |
| ---- | ------- | -------- | -------- |
| Identity | `identity` | `UserAccountService`, `PersonLookupService`, `RegisterBuyerService`, `RegisterSellerService`, `RegisterEmployeeService`, `LoginService`, `ManageUserStatusService`, `ManageBuyerStatusService`, `ManageSellerStatusService`, `ConsultUsersService` | `services/identity-services.md` |
| Warehouse | `warehouse` | `RegisterWarehouseService`, `ConsultWarehousesService` | `services/warehouse-services.md` |
| Catalog | `catalog` | `CreateProductService`, `ManageProductService`, `ConsultCatalogService` | `services/catalog-services.md` |
| Inventory | `inventory` | `RegisterInventoryService`, `ManageStockService`, `ConsultInventoryService` | `services/inventory-services.md` |
| Purchase | `purchase` | `ManageCartService`, `PlaceOrderService`, `PayOrderService`, `ConsultOrdersService` | `services/purchase-services.md` |
| Shipment | `shipment` | `ShipOrderService`, `ConsultShipmentsService` | `services/shipment-services.md` |
| Post-Sale | `postsale` | `ManageReturnService`, `ManageRefundService`, `ConsultReturnsService` | `services/post-sale-services.md` |
| Report | `report` | `SupervisorReportService` | `services/report-services.md` |

---

# Access Matrix

| Area                         | Buyer | Seller | Logistics Operator | Administrator | Supervisor |
| ---------------------------- | :---: | :----: | :----------------: | :-----------: | :--------: |
| Buyer registration           | ✔ (public) |   |                    |               |            |
| Seller and employee registration |   |        |                    | ✔             |            |
| User, buyer, seller status   |       |        |                    | ✔             |            |
| User list (OBJ-01)           |       |        |                    | ✔             | consult    |
| Warehouses                   |       | consult own |  consult      | ✔             | consult    |
| Product catalog              | consult (public) | ✔ own |           |               |            |
| Inventory                    |       | ✔ own  | ✔                  | consult       | consult    |
| Shopping cart and orders     | ✔ own | consult own |  consult      | consult       | consult    |
| Shipments                    | consult own |  | ✔                  | consult       | consult    |
| Returns                      | request own | approve / reject own | complete | ✔       | consult    |
| Refunds                      | consult own | create own |        | ✔             |            |
| Reports                      |       |        |                    | ✔             | ✔          |

---

# Assumptions

* **Returns and refunds.** The responsibility matrix of the functional specification assigns refund management to two participants, but the source does not make clear which ones. It is assumed that returns and refunds are approved by the administrator or by the seller whose products are in the order, that the logistics operator receives returned products, and that the administrator processes the money of refunds.
* **Payment.** The specification does not define a payment gateway, so paying an order registers the payment as confirmed.
* **Initial administrator.** The system needs one administrator to register sellers and employees. `DatabaseSeeder` (infrastructure layer) creates it on the first start, together with sample data.

---

# Future Improvements

* **Cancellation of unpaid orders.** The order lifecycle of the specification has no cancellation state, so stock reserved by an unpaid order stays reserved.
* **Partial returns.** Returns apply to the complete order; returning individual products would require return lines.
