package application.domain.valueobjects;

public final class InventoryStatus extends DomainCatalog {

    public static final InventoryStatus AVAILABLE = new InventoryStatus(
            "AVAILABLE", "Disponible", "Existencia disponible para ser reservada o vendida.");
    public static final InventoryStatus RESERVED = new InventoryStatus(
            "RESERVED", "Reservado", "Existencia apartada para un pedido en proceso.");
    public static final InventoryStatus DAMAGED = new InventoryStatus(
            "DAMAGED", "Dañado", "Existencia no apta para la venta; no puede reservarse.");

    private InventoryStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
