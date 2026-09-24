package application.domain.valueobjects;

public final class SellerStatus extends DomainCatalog {

    public static final SellerStatus ACTIVE = new SellerStatus(
            "ACTIVE", "Active", "Seller is enabled to publish and manage products.");
    public static final SellerStatus SUSPENDED = new SellerStatus(
            "SUSPENDED", "Suspended", "Seller is temporarily disabled from selling.");
    public static final SellerStatus INACTIVE = new SellerStatus(
            "INACTIVE", "Inactive", "Seller exists but is not currently operating.");

    private SellerStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
