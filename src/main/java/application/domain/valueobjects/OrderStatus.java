package application.domain.valueobjects;

public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus CART = new OrderStatus(
            "CART", "Cart", "Provisional selection of products.");
    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pending Payment", "Order awaits payment confirmation.");
    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Paid", "Payment confirmed; fulfillment processes begin.");
    public static final OrderStatus SHIPPED = new OrderStatus(
            "SHIPPED", "Shipped", "Order has physically left the warehouse.");
    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Delivered", "Order delivered and considered finalized.");

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
