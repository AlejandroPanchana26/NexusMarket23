package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.PrepareShipmentRequest;
import application.adapters.rest.dtos.requests.QuantityRequest;
import application.adapters.rest.dtos.requests.RegisterInventoryRequest;
import application.adapters.rest.dtos.responses.InventoryMovementResponse;
import application.adapters.rest.dtos.responses.InventoryResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
import application.adapters.rest.dtos.responses.ReturnResponse;
import application.adapters.rest.dtos.responses.ShipmentResponse;
import application.adapters.rest.dtos.responses.WarehouseResponse;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.PostSaleRestMapper;
import application.adapters.rest.mappers.ShipmentRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.adapters.security.CurrentUserProvider;
import application.domain.ports.in.LogisticsOperatorPort;
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

// Operación física: bodegas, inventario, envíos y recepción de devoluciones.
@RestController
@RequestMapping("/api/operator")
public class LogisticsOperatorRestController {

    private final LogisticsOperatorPort operatorPort;
    private final CurrentUserProvider currentUserProvider;

    public LogisticsOperatorRestController(LogisticsOperatorPort operatorPort, CurrentUserProvider currentUserProvider) {
        this.operatorPort = operatorPort;
        this.currentUserProvider = currentUserProvider;
    }

    // Bodegas e inventario

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listWarehouses() {
        return WarehouseRestMapper.toResponseList(operatorPort.listWarehouses(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponse> registerInventory(@Valid @RequestBody RegisterInventoryRequest request) {
        InventoryResponse inventory = InventoryRestMapper.toResponse(operatorPort.registerInventory(
                currentUserProvider.getCurrentUser(), request.productIdentifier(), request.warehouseIdentifier(),
                request.initialQuantity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/add-stock")
    public InventoryResponse addStock(@PathVariable String inventoryIdentifier, @Valid @RequestBody QuantityRequest request) {
        return InventoryRestMapper.toResponse(operatorPort.addStock(currentUserProvider.getCurrentUser(),
                inventoryIdentifier, request.quantity()));
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/adjust")
    public InventoryResponse adjustStock(@PathVariable String inventoryIdentifier, @Valid @RequestBody QuantityRequest request) {
        return InventoryRestMapper.toResponse(operatorPort.adjustStock(currentUserProvider.getCurrentUser(),
                inventoryIdentifier, request.quantity()));
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/damaged")
    public InventoryResponse markAsDamaged(@PathVariable String inventoryIdentifier) {
        return InventoryRestMapper.toResponse(operatorPort.markInventoryAsDamaged(currentUserProvider.getCurrentUser(),
                inventoryIdentifier));
    }

    @GetMapping("/inventory")
    public List<InventoryResponse> listInventory() {
        return InventoryRestMapper.toResponseList(operatorPort.listInventory(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/products/{productIdentifier}/inventory")
    public List<InventoryResponse> listProductInventory(@PathVariable String productIdentifier) {
        return InventoryRestMapper.toResponseList(operatorPort.listProductInventory(currentUserProvider.getCurrentUser(),
                productIdentifier));
    }

    @GetMapping("/inventory/{inventoryIdentifier}/movements")
    public List<InventoryMovementResponse> listMovements(@PathVariable String inventoryIdentifier) {
        return InventoryRestMapper.toMovementResponseList(operatorPort.listInventoryMovements(
                currentUserProvider.getCurrentUser(), inventoryIdentifier));
    }

    // Pedidos y envíos

    @GetMapping("/orders")
    public List<OrderResponse> listOrders() {
        return OrderRestMapper.toResponseList(operatorPort.listOrders(currentUserProvider.getCurrentUser()));
    }

    // El cuerpo es opcional: sin dirección se usa la dirección principal del comprador.
    @PostMapping("/orders/{orderIdentifier}/shipment")
    public ResponseEntity<ShipmentResponse> prepareShipment(@PathVariable String orderIdentifier,
                                                            @RequestBody(required = false) PrepareShipmentRequest request) {
        String address = request == null ? null : request.destinationAddress();
        ShipmentResponse shipment = ShipmentRestMapper.toResponse(operatorPort.prepareShipment(
                currentUserProvider.getCurrentUser(), orderIdentifier, address));
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @PatchMapping("/shipments/{shipmentIdentifier}/dispatch")
    public ShipmentResponse dispatchShipment(@PathVariable String shipmentIdentifier) {
        return ShipmentRestMapper.toResponse(operatorPort.dispatchShipment(currentUserProvider.getCurrentUser(), shipmentIdentifier));
    }

    @PatchMapping("/shipments/{shipmentIdentifier}/in-transit")
    public ShipmentResponse markInTransit(@PathVariable String shipmentIdentifier) {
        return ShipmentRestMapper.toResponse(operatorPort.markShipmentInTransit(currentUserProvider.getCurrentUser(), shipmentIdentifier));
    }

    @PatchMapping("/shipments/{shipmentIdentifier}/deliver")
    public ShipmentResponse confirmDelivery(@PathVariable String shipmentIdentifier) {
        return ShipmentRestMapper.toResponse(operatorPort.confirmDelivery(currentUserProvider.getCurrentUser(), shipmentIdentifier));
    }

    @GetMapping("/shipments")
    public List<ShipmentResponse> listShipments() {
        return ShipmentRestMapper.toResponseList(operatorPort.listShipments(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/orders/{orderIdentifier}/shipment")
    public ShipmentResponse findShipmentByOrder(@PathVariable String orderIdentifier) {
        return ShipmentRestMapper.toResponse(operatorPort.findShipmentByOrder(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    // Devoluciones

    // Ejemplo: /api/operator/returns?status=APPROVED
    @GetMapping("/returns")
    public List<ReturnResponse> listReturnsByStatus(@RequestParam String status) {
        return PostSaleRestMapper.toReturnResponseList(operatorPort.listReturnsByStatus(
                currentUserProvider.getCurrentUser(), PostSaleRestMapper.toReturnStatus(status)));
    }

    @PatchMapping("/returns/{returnIdentifier}/complete")
    public ReturnResponse completeReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(operatorPort.completeReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }
}
