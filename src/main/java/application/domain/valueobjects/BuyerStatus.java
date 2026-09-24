package application.domain.valueobjects;

public final class BuyerStatus extends DomainCatalog {

    public static final BuyerStatus ACTIVE = new BuyerStatus(
            "ACTIVE", "Active", "Buyer is enabled to place orders in the marketplace.");
    public static final BuyerStatus SUSPENDED = new BuyerStatus(
            "SUSPENDED", "Suspended", "Buyer is temporarily disabled from placing orders.");
    public static final BuyerStatus BLOCKED = new BuyerStatus(
            "BLOCKED", "Blocked", "Buyer's commercial activity has been permanently disabled.");

    private BuyerStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
