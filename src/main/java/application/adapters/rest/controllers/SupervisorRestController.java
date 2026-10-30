package application.adapters.rest.controllers;

import application.adapters.rest.dtos.responses.InventoryMovementResponse;
import application.adapters.rest.dtos.responses.InventoryResponse;
import application.adapters.rest.dtos.responses.InvoiceResponse;
import application.adapters.rest.dtos.responses.OrderCountResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
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
import application.domain.ports.in.SupervisorPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// El supervisor solo consulta: todas sus direcciones son GET.
@RestController
@RequestMapping("/api/supervisor")
public class SupervisorRestController {

    private final SupervisorPort supervisorPort;
    private final CurrentUserProvider currentUserProvider;

    public SupervisorRestController(SupervisorPort supervisorPort, CurrentUserProvider currentUserProvider) {
        this.supervisorPort = supervisorPort;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return UserRestMapper.toResponseList(supervisorPort.listUsers(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses() {
        return WarehouseRestMapper.toResponseList(supervisorPort.listWarehouses(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/inventory")
    public List<InventoryResponse> listInventory() {
        return InventoryRestMapper.toResponseList(supervisorPort.listInventory(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/products/{productIdentifier}/inventory")
    public List<InventoryResponse> listProductInventory(@PathVariable String productIdentifier) {
        return InventoryRestMapper.toResponseList(supervisorPort.listProductInventory(currentUserProvider.getCurrentUser(),
                productIdentifier));
    }

    @GetMapping("/inventory/{inventoryIdentifier}/movements")
    public List<InventoryMovementResponse> listMovements(@PathVariable String inventoryIdentifier) {
        return InventoryRestMapper.toMovementResponseList(supervisorPort.listInventoryMovements(
                currentUserProvider.getCurrentUser(), inventoryIdentifier));
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return OrderRestMapper.toResponseList(supervisorPort.listOrders(currentUserProvider.getCurrentUser()));
    }

    // Ejemplo: /api/supervisor/orders/by-status?status=DELIVERED
    @GetMapping("/orders/by-status")
    public List<OrderResponse> listOrdersByStatus(@RequestParam String status) {
        return OrderRestMapper.toResponseList(supervisorPort.listOrdersByStatus(currentUserProvider.getCurrentUser(),
                OrderRestMapper.toOrderStatus(status)));
    }

    @GetMapping("/orders/{orderIdentifier}/invoice")
    public InvoiceResponse getInvoice(@PathVariable String orderIdentifier) {
        return OrderRestMapper.toInvoiceResponse(supervisorPort.getInvoice(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> listShipments() {
        return ShipmentRestMapper.toResponseList(supervisorPort.listShipments(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/orders/{orderIdentifier}/shipment")
    public ShipmentResponse findShipmentByOrder(@PathVariable String orderIdentifier) {
        return ShipmentRestMapper.toResponse(supervisorPort.findShipmentByOrder(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/returns")
    public List<ReturnResponse> listReturnsByStatus(@RequestParam String status) {
        return PostSaleRestMapper.toReturnResponseList(supervisorPort.listReturnsByStatus(
                currentUserProvider.getCurrentUser(), PostSaleRestMapper.toReturnStatus(status)));
    }

    @GetMapping("/orders/{orderIdentifier}/returns")
    public List<ReturnResponse> listReturnsForOrder(@PathVariable String orderIdentifier) {
        return PostSaleRestMapper.toReturnResponseList(supervisorPort.listReturnsForOrder(
                currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/returns/{returnIdentifier}/refund")
    public RefundResponse getRefund(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toRefundResponse(supervisorPort.getRefund(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @GetMapping("/reports/orders-count")
    public OrderCountResponse countOrdersByStatus(@RequestParam String status) {
        int count = supervisorPort.countOrdersByStatus(currentUserProvider.getCurrentUser(), OrderRestMapper.toOrderStatus(status));
        return new OrderCountResponse(status, count);
    }

    @GetMapping("/reports/total-sales")
    public TotalSalesResponse calculateTotalSales() {
        return new TotalSalesResponse(supervisorPort.calculateTotalSales(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/reports/damaged-inventory")
    public List<InventoryResponse> listDamagedInventory() {
        return InventoryRestMapper.toResponseList(supervisorPort.listDamagedInventory(currentUserProvider.getCurrentUser()));
    }
}
