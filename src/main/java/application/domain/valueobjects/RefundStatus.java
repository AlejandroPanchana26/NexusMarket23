package application.domain.valueobjects;

public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus PENDING = new RefundStatus(
            "PENDING", "Pending", "Refund pending to be processed.");
    public static final RefundStatus PROCESSED = new RefundStatus(
            "PROCESSED", "Processed", "Refund delivered to the buyer.");
    public static final RefundStatus REJECTED = new RefundStatus(
            "REJECTED", "Rejected", "Refund rejected.");

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
