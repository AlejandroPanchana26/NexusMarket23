package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Order {

    private String identifier;

    // Comprador que realiza el pedido.
    private Buyer buyer;

    // Líneas del pedido, esta permanecera vacia por defecto
    private List<OrderDetail> details = new ArrayList<>();

    // Estado actual dentro del ciclo del pedido.
    private OrderStatus status;

    private LocalDateTime creationDate;

    // Valor total del pedido.
    private BigDecimal total;

    // Crea el pedido a partir del carrito, conservando los precios con los que se agregaron los productos.
    public static Order fromCart(ShoppingCart cart) {
        if (cart == null || cart.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot create an order from an empty cart.");
        }
        Order order = new Order();
        order.startFor(cart.getBuyer());
        for (CartItem item : cart.getItems()) {
            order.addDetail(item.getProduct(), item.getQuantity(), item.getUnitPrice());
        }
        order.confirm();
        return order;
    }

    public void startFor(Buyer orderBuyer) {
        if (orderBuyer == null || !orderBuyer.canPlaceOrders()) {
            throw new BusinessRuleViolationException("Buyer is not enabled to place orders.");
        }
        this.buyer = orderBuyer;
        this.status = OrderStatus.CART;
        this.creationDate = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    // Solo mientras el pedido está en CART se pueden agregar líneas.
    public void addDetail(Product product, int quantity, BigDecimal unitPrice) {
        requireEditable();
        details.add(new OrderDetail(product, quantity, unitPrice));
        this.total = total.add(details.get(details.size() - 1).getSubtotal());
    }

    public void confirm() {
        requireStatus(OrderStatus.CART, OrderStatus.PENDING_PAYMENT);
        if (details.isEmpty()) {
            throw new BusinessRuleViolationException("An order must have at least one product.");
        }
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void markAsPaid() {
        requireStatus(OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        this.status = OrderStatus.PAID;
    }

    public void markAsShipped() {
        requireStatus(OrderStatus.PAID, OrderStatus.SHIPPED);
        if (!containsPhysicalProducts()) {
            throw new BusinessRuleViolationException("An order with only digital products is not shipped.");
        }
        this.status = OrderStatus.SHIPPED;
    }

    // Un pedido solo digital se entrega apenas se paga; si tiene productos físicos debe despacharse primero.
    public void markAsDelivered() {
        boolean shipped = OrderStatus.SHIPPED.equals(status);
        boolean paidDigitalOnly = OrderStatus.PAID.equals(status) && !containsPhysicalProducts();
        if (!shipped && !paidDigitalOnly) {
            throw new InvalidStatusTransitionException("Order", status, OrderStatus.DELIVERED);
        }
        this.status = OrderStatus.DELIVERED;
    }

    public boolean isFinalized() {
        return OrderStatus.DELIVERED.equals(status);
    }

    public boolean containsPhysicalProducts() {
        for (OrderDetail detail : details) {
            if (detail.getProduct().requiresInventory()) {
                return true;
            }
        }
        return false;
    }

    // Regla del PDF: un pedido finalizado no puede modificarse bajo ninguna circunstancia.
    private void requireEditable() {
        if (isFinalized()) {
            throw new BusinessRuleViolationException("A finalized order cannot be modified.");
        }
        if (!OrderStatus.CART.equals(status)) {
            throw new BusinessRuleViolationException("Only orders in CART status can be edited.");
        }
    }

    private void requireStatus(OrderStatus expected, OrderStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Order", status, target);
        }
    }
}
