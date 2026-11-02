# Input Ports

## Introduction

Input ports are the Java interfaces through which the outside world (the REST controllers) asks the domain to do something. They are the opposite of the output ports: the domain **uses** the output ports, while the input ports are **used by** the adapters.

Each input port is the **menu of one role**: reading `BuyerPort` shows everything a buyer can do in NexusMarket, and nothing else.

All input ports are located in `application.domain.ports.in`.

---

# Design Rules

* **One port per role.** There are six ports: one for public access and one for each role of the functional specification.
* **Implemented by use cases.** Each port is implemented by a class in `application.adapters.useCases` (for example, `BuyerUseCase implements BuyerPort`).
* **Delegation only.** A use case method only calls the corresponding domain service. Use cases contain no business rules: the rules live in the models and the services.
* **The authenticated user is a parameter.** Every operation that requires authentication receives the `User` as its first parameter, and the service validates the role (RG-01, RG-03). The same operation can appear in several ports (for example, registering inventory is available to sellers and logistics operators) because the service, not the port, decides what each role may do.
* **One REST controller per port.** Each port is used by exactly one controller, so the API has one group of endpoints per role.

---

# Port Catalog

| Input Port | Role | Use Case | REST Base Path | Operations |
| ---------- | ---- | -------- | -------------- | ---------- |
| `PublicAccessPort` | Public (no authentication) | `PublicAccessUseCase` | `/api/public` | 3 |
| `BuyerPort` | `BUYER` | `BuyerUseCase` | `/api/buyer` | 12 |
| `SellerPort` | `SELLER` | `SellerUseCase` | `/api/seller` | 18 |
| `LogisticsOperatorPort` | `LOGISTICS_OPERATOR` | `LogisticsOperatorUseCase` | `/api/operator` | 17 |
| `AdministratorPort` | `ADMINISTRATOR` | `AdministratorUseCase` | `/api/admin` | 28 |
| `SupervisorPort` | `SUPERVISOR` | `SupervisorUseCase` | `/api/supervisor` | 16 |
| **Total** | | | | **94** |

---

# PublicAccessPort

**Role:** Public (no authentication). **Implemented by:** `PublicAccessUseCase`. **Used by:** the REST controller mapped to `/api/public`.

| Operation | Delegates to |
| --------- | ------------ |
| User registerBuyer(Buyer, String, String) | `RegisterBuyerService.register` |
| User login(String, String) | `LoginService.login` |
| List<Product> listPublishedProducts() | `ConsultCatalogService.listPublished` |

---

# BuyerPort

**Role:** `BUYER`. **Implemented by:** `BuyerUseCase`. **Used by:** the REST controller mapped to `/api/buyer`.

| Operation | Delegates to |
| --------- | ------------ |
| ShoppingCart viewCart(User) | `ManageCartService.view` |
| ShoppingCart addToCart(User, String, int) | `ManageCartService.addItem` |
| ShoppingCart removeFromCart(User, String) | `ManageCartService.removeItem` |
| ShoppingCart clearCart(User) | `ManageCartService.clear` |
| Order placeOrder(User) | `PlaceOrderService.placeOrder` |
| Order payOrder(User, String) | `PayOrderService.pay` |
| List<Order> listMyOrders(User) | `ConsultOrdersService.listOwn` |
| Invoice getInvoice(User, String) | `ConsultOrdersService.getInvoice` |
| Shipment trackShipment(User, String) | `ConsultShipmentsService.findByOrder` |
| Return requestReturn(User, String, String) | `ManageReturnService.request` |
| List<Return> listReturns(User, String) | `ConsultReturnsService.listForOrder` |
| Refund getRefund(User, String) | `ConsultReturnsService.findRefund` |

---

# SellerPort

**Role:** `SELLER`. **Implemented by:** `SellerUseCase`. **Used by:** the REST controller mapped to `/api/seller`.

| Operation | Delegates to |
| --------- | ------------ |
| Product createProduct(User, Product) | `CreateProductService.create` |
| Product publishProduct(User, String) | `ManageProductService.publish` |
| Product suspendProduct(User, String) | `ManageProductService.suspend` |
| Product discontinueProduct(User, String) | `ManageProductService.discontinue` |
| Product changeProductPrice(User, String, BigDecimal) | `ManageProductService.changePrice` |
| Product addProductVariant(User, String, String) | `ManageProductService.addVariant` |
| List<Product> listMyProducts(User) | `ConsultCatalogService.listOwn` |
| List<Warehouse> listMyWarehouses(User) | `ConsultWarehousesService.listOwn` |
| Inventory registerInventory(User, String, String, int) | `RegisterInventoryService.register` |
| Inventory addStock(User, String, int) | `ManageStockService.addStock` |
| Inventory adjustStock(User, String, int) | `ManageStockService.adjust` |
| Inventory markInventoryAsDamaged(User, String) | `ManageStockService.markAsDamaged` |
| List<Inventory> listProductInventory(User, String) | `ConsultInventoryService.listByProduct` |
| List<InventoryMovement> listInventoryMovements(User, String) | `ConsultInventoryService.listMovements` |
| List<Order> listMyOrders(User) | `ConsultOrdersService.listForSeller` |
| Return approveReturn(User, String) | `ManageReturnService.approve` |
| Return rejectReturn(User, String) | `ManageReturnService.reject` |
| Refund createRefund(User, String, BigDecimal) | `ManageRefundService.create` |

---

# LogisticsOperatorPort

**Role:** `LOGISTICS_OPERATOR`. **Implemented by:** `LogisticsOperatorUseCase`. **Used by:** the REST controller mapped to `/api/operator`.

| Operation | Delegates to |
| --------- | ------------ |
| List<Warehouse> listWarehouses(User) | `ConsultWarehousesService.listAll` |
| Inventory registerInventory(User, String, String, int) | `RegisterInventoryService.register` |
| Inventory addStock(User, String, int) | `ManageStockService.addStock` |
| Inventory adjustStock(User, String, int) | `ManageStockService.adjust` |
| Inventory markInventoryAsDamaged(User, String) | `ManageStockService.markAsDamaged` |
| List<Inventory> listInventory(User) | `ConsultInventoryService.listAll` |
| List<Inventory> listProductInventory(User, String) | `ConsultInventoryService.listByProduct` |
| List<InventoryMovement> listInventoryMovements(User, String) | `ConsultInventoryService.listMovements` |
| List<Order> listOrders(User) | `ConsultOrdersService.listAll` |
| Shipment prepareShipment(User, String, String) | `ShipOrderService.prepare` |
| Shipment dispatchShipment(User, String) | `ShipOrderService.dispatch` |
| Shipment markShipmentInTransit(User, String) | `ShipOrderService.markInTransit` |
| Shipment confirmDelivery(User, String) | `ShipOrderService.confirmDelivery` |
| List<Shipment> listShipments(User) | `ConsultShipmentsService.listAll` |
| Shipment findShipmentByOrder(User, String) | `ConsultShipmentsService.findByOrder` |
| List<Return> listReturnsByStatus(User, ReturnStatus) | `ConsultReturnsService.listByStatus` |
| Return completeReturn(User, String) | `ManageReturnService.complete` |

---

# AdministratorPort

**Role:** `ADMINISTRATOR`. **Implemented by:** `AdministratorUseCase`. **Used by:** the REST controller mapped to `/api/admin`.

| Operation | Delegates to |
| --------- | ------------ |
| User registerSeller(User, Seller, Warehouse, String, String) | `RegisterSellerService.register` |
| User registerEmployee(User, Employee, SystemRole, String, String) | `RegisterEmployeeService.register` |
| List<User> listUsers(User) | `ConsultUsersService.listAll` |
| User blockUser(User, String) | `ManageUserStatusService.block` |
| User deactivateUser(User, String) | `ManageUserStatusService.deactivate` |
| User activateUser(User, String) | `ManageUserStatusService.activate` |
| Seller suspendSeller(User, String) | `ManageSellerStatusService.suspend` |
| Seller activateSeller(User, String) | `ManageSellerStatusService.activate` |
| Seller deactivateSeller(User, String) | `ManageSellerStatusService.deactivate` |
| Buyer suspendBuyer(User, String) | `ManageBuyerStatusService.suspend` |
| Buyer activateBuyer(User, String) | `ManageBuyerStatusService.activate` |
| Buyer blockBuyer(User, String) | `ManageBuyerStatusService.block` |
| Warehouse registerWarehouse(User, Warehouse, String) | `RegisterWarehouseService.register` |
| List<Warehouse> listWarehouses(User) | `ConsultWarehousesService.listAll` |
| List<Inventory> listInventory(User) | `ConsultInventoryService.listAll` |
| List<Order> listOrders(User) | `ConsultOrdersService.listAll` |
| Invoice getInvoice(User, String) | `ConsultOrdersService.getInvoice` |
| List<Shipment> listShipments(User) | `ConsultShipmentsService.listAll` |
| List<Return> listReturnsByStatus(User, ReturnStatus) | `ConsultReturnsService.listByStatus` |
| Return approveReturn(User, String) | `ManageReturnService.approve` |
| Return rejectReturn(User, String) | `ManageReturnService.reject` |
| Return completeReturn(User, String) | `ManageReturnService.complete` |
| Refund createRefund(User, String, BigDecimal) | `ManageRefundService.create` |
| Refund processRefund(User, String) | `ManageRefundService.process` |
| Refund rejectRefund(User, String) | `ManageRefundService.reject` |
| int countOrdersByStatus(User, OrderStatus) | `SupervisorReportService.countOrdersByStatus` |
| BigDecimal calculateTotalSales(User) | `SupervisorReportService.calculateTotalSales` |
| List<Inventory> listDamagedInventory(User) | `SupervisorReportService.listDamagedInventory` |

---

# SupervisorPort

**Role:** `SUPERVISOR`. **Implemented by:** `SupervisorUseCase`. **Used by:** the REST controller mapped to `/api/supervisor`.

| Operation | Delegates to |
| --------- | ------------ |
| List<User> listUsers(User) | `ConsultUsersService.listAll` |
| List<Warehouse> listWarehouses(User) | `ConsultWarehousesService.listAll` |
| List<Inventory> listInventory(User) | `ConsultInventoryService.listAll` |
| List<Inventory> listProductInventory(User, String) | `ConsultInventoryService.listByProduct` |
| List<InventoryMovement> listInventoryMovements(User, String) | `ConsultInventoryService.listMovements` |
| List<Order> listOrders(User) | `ConsultOrdersService.listAll` |
| List<Order> listOrdersByStatus(User, OrderStatus) | `SupervisorReportService.listOrdersByStatus` |
| Invoice getInvoice(User, String) | `ConsultOrdersService.getInvoice` |
| List<Shipment> listShipments(User) | `ConsultShipmentsService.listAll` |
| Shipment findShipmentByOrder(User, String) | `ConsultShipmentsService.findByOrder` |
| List<Return> listReturnsByStatus(User, ReturnStatus) | `ConsultReturnsService.listByStatus` |
| List<Return> listReturnsForOrder(User, String) | `ConsultReturnsService.listForOrder` |
| Refund getRefund(User, String) | `ConsultReturnsService.findRefund` |
| int countOrdersByStatus(User, OrderStatus) | `SupervisorReportService.countOrdersByStatus` |
| BigDecimal calculateTotalSales(User) | `SupervisorReportService.calculateTotalSales` |
| List<Inventory> listDamagedInventory(User) | `SupervisorReportService.listDamagedInventory` |
