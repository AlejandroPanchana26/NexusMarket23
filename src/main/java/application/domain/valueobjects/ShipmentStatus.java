package application.domain.valueobjects;

public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus PREPARING = new ShipmentStatus(
            "PREPARING", "Preparing", "Order is being packed at the warehouse.");
    public static final ShipmentStatus SHIPPED = new ShipmentStatus(
            "SHIPPED", "Shipped", "Order has left the warehouse.");
    public static final ShipmentStatus IN_TRANSIT = new ShipmentStatus(
            "IN_TRANSIT", "In Transit", "Order is on its way to the buyer.");
    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Delivered", "Order delivered to the buyer.");

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
