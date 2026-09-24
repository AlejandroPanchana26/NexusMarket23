package application.domain.valueobjects;

public final class WarehouseType extends DomainCatalog {

    public static final WarehouseType MARKETPLACE = new WarehouseType(
            "MARKETPLACE", "Marketplace Warehouse", "Warehouse managed directly by the platform.");
    public static final WarehouseType SELLER = new WarehouseType(
            "SELLER", "Seller Warehouse", "Warehouse owned and managed by a seller.");

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }
}
