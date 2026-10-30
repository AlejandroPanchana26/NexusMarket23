package application.adapters.rest.controllers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.requests.RefundRequest;
import application.adapters.rest.dtos.requests.RegisterEmployeeRequest;
import application.adapters.rest.dtos.requests.RegisterSellerRequest;
import application.adapters.rest.dtos.requests.RegisterWarehouseRequest;
import application.adapters.rest.dtos.responses.InventoryResponse;
import application.adapters.rest.dtos.responses.InvoiceResponse;
import application.adapters.rest.dtos.responses.OrderCountResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
import application.adapters.rest.dtos.responses.PersonStatusResponse;
import application.adapters.rest.dtos.responses.RefundResponse;
import application.adapters.rest.dtos.responses.ReturnResponse;
import application.adapters.rest.dtos.responses.ShipmentResponse;
import application.adapters.rest.dtos.responses.TotalSalesResponse;
import application.adapters.rest.dtos.responses.UserResponse;
import application.adapters.rest.dtos.responses.WarehouseResponse;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.PostSaleRestMapper;
import application.adapters.rest.mappers.ShipmentRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.adapters.security.CurrentUserProvider;
import application.domain.models.User;
import application.domain.ports.in.AdministratorPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Administración de vendedores, empleados, usuarios, bodegas, posventa y reportes.
@RestController
@RequestMapping("/api/admin")
public class AdministratorRestController {

    private final AdministratorPort adminPort;
    private final CurrentUserProvider currentUserProvider;

    public AdministratorRestController(AdministratorPort adminPort, CurrentUserProvider currentUserProvider) {
        this.adminPort = adminPort;
        this.currentUserProvider = currentUserProvider;
    }

    // Registros

    @PostMapping("/sellers")
    public ResponseEntity<UserResponse> registerSeller(@Valid @RequestBody RegisterSellerRequest request) {
        User user = adminPort.registerSeller(currentUserProvider.getCurrentUser(), UserRestMapper.toSeller(request),
                WarehouseRestMapper.toWarehouse(request.warehouseName(), request.warehouseAddress()),
                request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponse(user));
    }

    @PostMapping("/employees")
    public ResponseEntity<UserResponse> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
        User user = adminPort.registerEmployee(currentUserProvider.getCurrentUser(), UserRestMapper.toEmployee(request),
                CatalogMapper.systemRole(request.role()), request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRestMapper.toResponse(user));
    }

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseResponse> registerWarehouse(@Valid @RequestBody RegisterWarehouseRequest request) {
        WarehouseResponse warehouse = WarehouseRestMapper.toResponse(adminPort.registerWarehouse(
                currentUserProvider.getCurrentUser(), WarehouseRestMapper.toWarehouse(request.name(), request.address()),
                request.sellerIdentification()));
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouse);
    }

    // Usuarios (OBJ-01)

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return UserRestMapper.toResponseList(adminPort.listUsers(currentUserProvider.getCurrentUser()));
    }

    @PatchMapping("/users/{username}/block")
    public UserResponse blockUser(@PathVariable String username) {
        return UserRestMapper.toResponse(adminPort.blockUser(currentUserProvider.getCurrentUser(), username));
    }

    @PatchMapping("/users/{username}/deactivate")
    public UserResponse deactivateUser(@PathVariable String username) {
        return UserRestMapper.toResponse(adminPort.deactivateUser(currentUserProvider.getCurrentUser(), username));
    }

    @PatchMapping("/users/{username}/activate")
    public UserResponse activateUser(@PathVariable String username) {
        return UserRestMapper.toResponse(adminPort.activateUser(currentUserProvider.getCurrentUser(), username));
    }

    // Estado comercial de vendedores y compradores

    @PatchMapping("/sellers/{identification}/suspend")
    public PersonStatusResponse suspendSeller(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.suspendSeller(currentUserProvider.getCurrentUser(), identification));
    }

    @PatchMapping("/sellers/{identification}/activate")
    public PersonStatusResponse activateSeller(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.activateSeller(currentUserProvider.getCurrentUser(), identification));
    }

    @PatchMapping("/sellers/{identification}/deactivate")
    public PersonStatusResponse deactivateSeller(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.deactivateSeller(currentUserProvider.getCurrentUser(), identification));
    }

    @PatchMapping("/buyers/{identification}/suspend")
    public PersonStatusResponse suspendBuyer(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.suspendBuyer(currentUserProvider.getCurrentUser(), identification));
    }

    @PatchMapping("/buyers/{identification}/activate")
    public PersonStatusResponse activateBuyer(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.activateBuyer(currentUserProvider.getCurrentUser(), identification));
    }

    @PatchMapping("/buyers/{identification}/block")
    public PersonStatusResponse blockBuyer(@PathVariable String identification) {
        return UserRestMapper.toPersonStatusResponse(adminPort.blockBuyer(currentUserProvider.getCurrentUser(), identification));
    }

    // Consultas

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses() {
        return WarehouseRestMapper.toResponseList(adminPort.listWarehouses(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/inventory")
    public List<InventoryResponse> listInventory() {
        return InventoryRestMapper.toResponseList(adminPort.listInventory(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return OrderRestMapper.toResponseList(adminPort.listOrders(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/orders/{orderIdentifier}/invoice")
    public InvoiceResponse getInvoice(@PathVariable String orderIdentifier) {
        return OrderRestMapper.toInvoiceResponse(adminPort.getInvoice(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> listShipments() {
        return ShipmentRestMapper.toResponseList(adminPort.listShipments(currentUserProvider.getCurrentUser()));
    }

    // Devoluciones y reembolsos

    @GetMapping("/returns")
    public List<ReturnResponse> listReturnsByStatus(@RequestParam String status) {
        return PostSaleRestMapper.toReturnResponseList(adminPort.listReturnsByStatus(
                currentUserProvider.getCurrentUser(), PostSaleRestMapper.toReturnStatus(status)));
    }

    @PatchMapping("/returns/{returnIdentifier}/approve")
    public ReturnResponse approveReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(adminPort.approveReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @PatchMapping("/returns/{returnIdentifier}/reject")
    public ReturnResponse rejectReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(adminPort.rejectReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @PatchMapping("/returns/{returnIdentifier}/complete")
    public ReturnResponse completeReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(adminPort.completeReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @PostMapping("/returns/{returnIdentifier}/refund")
    public ResponseEntity<RefundResponse> createRefund(@PathVariable String returnIdentifier,
                                                       @Valid @RequestBody RefundRequest request) {
        RefundResponse refund = PostSaleRestMapper.toRefundResponse(adminPort.createRefund(
                currentUserProvider.getCurrentUser(), returnIdentifier, request.amount()));
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }

    @PatchMapping("/refunds/{refundIdentifier}/process")
    public RefundResponse processRefund(@PathVariable String refundIdentifier) {
        return PostSaleRestMapper.toRefundResponse(adminPort.processRefund(currentUserProvider.getCurrentUser(), refundIdentifier));
    }

    @PatchMapping("/refunds/{refundIdentifier}/reject")
    public RefundResponse rejectRefund(@PathVariable String refundIdentifier) {
        return PostSaleRestMapper.toRefundResponse(adminPort.rejectRefund(currentUserProvider.getCurrentUser(), refundIdentifier));
    }

    // Reportes (OBJ-12)

    // Ejemplo: /api/admin/reports/orders-count?status=PAID
    @GetMapping("/reports/orders-count")
    public OrderCountResponse countOrdersByStatus(@RequestParam String status) {
        int count = adminPort.countOrdersByStatus(currentUserProvider.getCurrentUser(), OrderRestMapper.toOrderStatus(status));
        return new OrderCountResponse(status, count);
    }

    @GetMapping("/reports/total-sales")
    public TotalSalesResponse calculateTotalSales() {
        return new TotalSalesResponse(adminPort.calculateTotalSales(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/reports/damaged-inventory")
    public List<InventoryResponse> listDamagedInventory() {
        return InventoryRestMapper.toResponseList(adminPort.listDamagedInventory(currentUserProvider.getCurrentUser()));
    }
}
