package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.ChangePriceRequest;
import application.adapters.rest.dtos.requests.CreateProductRequest;
import application.adapters.rest.dtos.requests.QuantityRequest;
import application.adapters.rest.dtos.requests.RefundRequest;
import application.adapters.rest.dtos.requests.RegisterInventoryRequest;
import application.adapters.rest.dtos.requests.VariantRequest;
import application.adapters.rest.dtos.responses.InventoryMovementResponse;
import application.adapters.rest.dtos.responses.InventoryResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
import application.adapters.rest.dtos.responses.ProductResponse;
import application.adapters.rest.dtos.responses.RefundResponse;
import application.adapters.rest.dtos.responses.ReturnResponse;
import application.adapters.rest.dtos.responses.WarehouseResponse;
import application.adapters.rest.mappers.InventoryRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.PostSaleRestMapper;
import application.adapters.rest.mappers.ProductRestMapper;
import application.adapters.rest.mappers.WarehouseRestMapper;
import application.adapters.security.CurrentUserProvider;
import application.domain.ports.in.SellerPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Operaciones del vendedor sobre sus productos, su inventario, sus pedidos y las devoluciones.
@RestController
@RequestMapping("/api/seller")
public class SellerRestController {

    private final SellerPort sellerPort;
    private final CurrentUserProvider currentUserProvider;

    public SellerRestController(SellerPort sellerPort, CurrentUserProvider currentUserProvider) {
        this.sellerPort = sellerPort;
        this.currentUserProvider = currentUserProvider;
    }

    // Productos

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = ProductRestMapper.toResponse(
                sellerPort.createProduct(currentUserProvider.getCurrentUser(), ProductRestMapper.toProduct(request)));
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PatchMapping("/products/{productIdentifier}/publish")
    public ProductResponse publishProduct(@PathVariable String productIdentifier) {
        return ProductRestMapper.toResponse(sellerPort.publishProduct(currentUserProvider.getCurrentUser(), productIdentifier));
    }

    @PatchMapping("/products/{productIdentifier}/suspend")
    public ProductResponse suspendProduct(@PathVariable String productIdentifier) {
        return ProductRestMapper.toResponse(sellerPort.suspendProduct(currentUserProvider.getCurrentUser(), productIdentifier));
    }

    @PatchMapping("/products/{productIdentifier}/discontinue")
    public ProductResponse discontinueProduct(@PathVariable String productIdentifier) {
        return ProductRestMapper.toResponse(sellerPort.discontinueProduct(currentUserProvider.getCurrentUser(), productIdentifier));
    }

    @PatchMapping("/products/{productIdentifier}/price")
    public ProductResponse changePrice(@PathVariable String productIdentifier, @Valid @RequestBody ChangePriceRequest request) {
        return ProductRestMapper.toResponse(sellerPort.changeProductPrice(currentUserProvider.getCurrentUser(),
                productIdentifier, request.price()));
    }

    @PostMapping("/products/{productIdentifier}/variants")
    public ProductResponse addVariant(@PathVariable String productIdentifier, @Valid @RequestBody VariantRequest request) {
        return ProductRestMapper.toResponse(sellerPort.addProductVariant(currentUserProvider.getCurrentUser(),
                productIdentifier, request.variant()));
    }

    @GetMapping("/products")
    public List<ProductResponse> listMyProducts() {
        return ProductRestMapper.toResponseList(sellerPort.listMyProducts(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/warehouses")
    public List<WarehouseResponse> listMyWarehouses() {
        return WarehouseRestMapper.toResponseList(sellerPort.listMyWarehouses(currentUserProvider.getCurrentUser()));
    }

    // Inventario

    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponse> registerInventory(@Valid @RequestBody RegisterInventoryRequest request) {
        InventoryResponse inventory = InventoryRestMapper.toResponse(sellerPort.registerInventory(
                currentUserProvider.getCurrentUser(), request.productIdentifier(), request.warehouseIdentifier(),
                request.initialQuantity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/add-stock")
    public InventoryResponse addStock(@PathVariable String inventoryIdentifier, @Valid @RequestBody QuantityRequest request) {
        return InventoryRestMapper.toResponse(sellerPort.addStock(currentUserProvider.getCurrentUser(),
                inventoryIdentifier, request.quantity()));
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/adjust")
    public InventoryResponse adjustStock(@PathVariable String inventoryIdentifier, @Valid @RequestBody QuantityRequest request) {
        return InventoryRestMapper.toResponse(sellerPort.adjustStock(currentUserProvider.getCurrentUser(),
                inventoryIdentifier, request.quantity()));
    }

    @PatchMapping("/inventory/{inventoryIdentifier}/damaged")
    public InventoryResponse markAsDamaged(@PathVariable String inventoryIdentifier) {
        return InventoryRestMapper.toResponse(sellerPort.markInventoryAsDamaged(currentUserProvider.getCurrentUser(),
                inventoryIdentifier));
    }

    @GetMapping("/products/{productIdentifier}/inventory")
    public List<InventoryResponse> listProductInventory(@PathVariable String productIdentifier) {
        return InventoryRestMapper.toResponseList(sellerPort.listProductInventory(currentUserProvider.getCurrentUser(),
                productIdentifier));
    }

    @GetMapping("/inventory/{inventoryIdentifier}/movements")
    public List<InventoryMovementResponse> listMovements(@PathVariable String inventoryIdentifier) {
        return InventoryRestMapper.toMovementResponseList(sellerPort.listInventoryMovements(
                currentUserProvider.getCurrentUser(), inventoryIdentifier));
    }

    // Pedidos y devoluciones

    @GetMapping("/orders")
    public List<OrderResponse> listMyOrders() {
        return OrderRestMapper.toResponseList(sellerPort.listMyOrders(currentUserProvider.getCurrentUser()));
    }

    @PatchMapping("/returns/{returnIdentifier}/approve")
    public ReturnResponse approveReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(sellerPort.approveReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @PatchMapping("/returns/{returnIdentifier}/reject")
    public ReturnResponse rejectReturn(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toReturnResponse(sellerPort.rejectReturn(currentUserProvider.getCurrentUser(), returnIdentifier));
    }

    @PostMapping("/returns/{returnIdentifier}/refund")
    public ResponseEntity<RefundResponse> createRefund(@PathVariable String returnIdentifier,
                                                       @Valid @RequestBody RefundRequest request) {
        RefundResponse refund = PostSaleRestMapper.toRefundResponse(sellerPort.createRefund(
                currentUserProvider.getCurrentUser(), returnIdentifier, request.amount()));
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }
}
