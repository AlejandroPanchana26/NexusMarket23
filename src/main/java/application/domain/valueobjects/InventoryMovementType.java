package application.domain.valueobjects;

public final class InventoryMovementType extends DomainCatalog {

    public static final InventoryMovementType ENTRY = new InventoryMovementType(
            "ENTRY", "Entry", "Entry of new stock into the warehouse.");
    public static final InventoryMovementType RESERVATION = new InventoryMovementType(
            "RESERVATION", "Reservation", "Stock set aside for an order in progress.");
    public static final InventoryMovementType SALE_EXIT = new InventoryMovementType(
            "SALE_EXIT", "Sale Exit", "Stock leaving the warehouse due to a sale.");
    public static final InventoryMovementType ADJUSTMENT = new InventoryMovementType(
            "ADJUSTMENT", "Adjustment", "Manual correction of recorded stock.");
    public static final InventoryMovementType RETURN = new InventoryMovementType(
            "RETURN", "Return", "Stock re-entering due to a return.");

    private InventoryMovementType(String code, String name, String description) {
        super(code, name, description);
    }
}
