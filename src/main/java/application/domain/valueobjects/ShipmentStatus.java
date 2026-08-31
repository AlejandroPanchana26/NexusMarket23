package application.domain.valueobjects;

public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus PREPARING = new ShipmentStatus(
            "PREPARING", "En preparación", "El pedido está siendo empacado en la bodega.");
    public static final ShipmentStatus SHIPPED = new ShipmentStatus(
            "SHIPPED", "Despachado", "El pedido salió de la bodega hacia su destino.");
    public static final ShipmentStatus IN_TRANSIT = new ShipmentStatus(
            "IN_TRANSIT", "En tránsito", "El pedido se encuentra en camino al comprador.");
    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Entregado", "El pedido fue entregado al comprador.");

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
