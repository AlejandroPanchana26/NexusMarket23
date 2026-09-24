package application.domain.valueobjects;

public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Requested", "Buyer requested the return.");
    public static final ReturnStatus APPROVED = new ReturnStatus(
            "APPROVED", "Approved", "Return approved and may proceed.");
    public static final ReturnStatus REJECTED = new ReturnStatus(
            "REJECTED", "Rejected", "Return rejected.");
    public static final ReturnStatus COMPLETED = new ReturnStatus(
            "COMPLETED", "Completed", "Return completed successfully.");

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
