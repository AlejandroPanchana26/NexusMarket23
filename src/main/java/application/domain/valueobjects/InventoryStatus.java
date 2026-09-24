package application.domain.valueobjects;

public final class InventoryStatus extends DomainCatalog {

    public static final InventoryStatus AVAILABLE = new InventoryStatus(
            "AVAILABLE", "Available", "Stock available to be reserved or sold.");
    public static final InventoryStatus RESERVED = new InventoryStatus(
            "RESERVED", "Reserved", "Stock set aside for an order in progress.");
    public static final InventoryStatus DAMAGED = new InventoryStatus(
            "DAMAGED", "Damaged", "Stock not fit for sale; cannot be reserved.");

    private InventoryStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
