package application.domain.valueobjects;

public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus CART = new OrderStatus(
            "CART", "Carrito", "Selección provisional de productos antes de confirmar el pedido.");
    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pendiente de Pago", "El pedido espera la confirmación del pago.");
    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Pagado", "El pago fue confirmado y se inician los procesos de alistamiento.");
    public static final OrderStatus SHIPPED = new OrderStatus(
            "SHIPPED", "Despachado", "El pedido salió físicamente de la bodega.");
    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Entregado", "El pedido fue entregado y se considera finalizado.");

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
