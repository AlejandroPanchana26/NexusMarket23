package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.AddCartItemRequest;
import application.adapters.rest.dtos.requests.ReturnReasonRequest;
import application.adapters.rest.dtos.responses.CartResponse;
import application.adapters.rest.dtos.responses.InvoiceResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
import application.adapters.rest.dtos.responses.RefundResponse;
import application.adapters.rest.dtos.responses.ReturnResponse;
import application.adapters.rest.dtos.responses.ShipmentResponse;
import application.adapters.rest.mappers.CartRestMapper;
import application.adapters.rest.mappers.OrderRestMapper;
import application.adapters.rest.mappers.PostSaleRestMapper;
import application.adapters.rest.mappers.ShipmentRestMapper;
import application.adapters.security.CurrentUserProvider;
import application.domain.ports.in.BuyerPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Operaciones del comprador. El usuario se obtiene de CurrentUserProvider en cada petición.
@RestController
@RequestMapping("/api/buyer")
public class BuyerRestController {

    private final BuyerPort buyerPort;
    private final CurrentUserProvider currentUserProvider;

    public BuyerRestController(BuyerPort buyerPort, CurrentUserProvider currentUserProvider) {
        this.buyerPort = buyerPort;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/cart")
    public CartResponse viewCart() {
        return CartRestMapper.toResponse(buyerPort.viewCart(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/cart/items")
    public CartResponse addToCart(@Valid @RequestBody AddCartItemRequest request) {
        return CartRestMapper.toResponse(buyerPort.addToCart(currentUserProvider.getCurrentUser(),
                request.productIdentifier(), request.quantity()));
    }

    @DeleteMapping("/cart/items/{productIdentifier}")
    public CartResponse removeFromCart(@PathVariable String productIdentifier) {
        return CartRestMapper.toResponse(buyerPort.removeFromCart(currentUserProvider.getCurrentUser(), productIdentifier));
    }

    @DeleteMapping("/cart")
    public CartResponse clearCart() {
        return CartRestMapper.toResponse(buyerPort.clearCart(currentUserProvider.getCurrentUser()));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder() {
        OrderResponse order = OrderRestMapper.toBuyerResponse(buyerPort.placeOrder(currentUserProvider.getCurrentUser()));
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PatchMapping("/orders/{orderIdentifier}/pay")
    public OrderResponse payOrder(@PathVariable String orderIdentifier) {
        return OrderRestMapper.toBuyerResponse(buyerPort.payOrder(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/orders")
    public List<OrderResponse> listMyOrders() {
        return OrderRestMapper.toBuyerResponseList(buyerPort.listMyOrders(currentUserProvider.getCurrentUser()));
    }

    @GetMapping("/orders/{orderIdentifier}/invoice")
    public InvoiceResponse getInvoice(@PathVariable String orderIdentifier) {
        return OrderRestMapper.toInvoiceResponse(buyerPort.getInvoice(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/orders/{orderIdentifier}/shipment")
    public ShipmentResponse trackShipment(@PathVariable String orderIdentifier) {
        return ShipmentRestMapper.toResponse(buyerPort.trackShipment(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @PostMapping("/orders/{orderIdentifier}/returns")
    public ResponseEntity<ReturnResponse> requestReturn(@PathVariable String orderIdentifier,
                                                        @Valid @RequestBody ReturnReasonRequest request) {
        ReturnResponse response = PostSaleRestMapper.toReturnResponse(
                buyerPort.requestReturn(currentUserProvider.getCurrentUser(), orderIdentifier, request.reason()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders/{orderIdentifier}/returns")
    public List<ReturnResponse> listReturns(@PathVariable String orderIdentifier) {
        return PostSaleRestMapper.toReturnResponseList(buyerPort.listReturns(currentUserProvider.getCurrentUser(), orderIdentifier));
    }

    @GetMapping("/returns/{returnIdentifier}/refund")
    public RefundResponse getRefund(@PathVariable String returnIdentifier) {
        return PostSaleRestMapper.toRefundResponse(buyerPort.getRefund(currentUserProvider.getCurrentUser(), returnIdentifier));
    }
}
