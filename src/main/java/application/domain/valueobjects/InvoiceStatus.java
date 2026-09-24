package application.domain.valueobjects;

public final class InvoiceStatus extends DomainCatalog {

    public static final InvoiceStatus ISSUED = new InvoiceStatus(
            "ISSUED", "Issued", "Invoice generated for the order.");
    public static final InvoiceStatus PAID = new InvoiceStatus(
            "PAID", "Paid", "Invoice paid by the buyer.");
    public static final InvoiceStatus CANCELLED = new InvoiceStatus(
            "CANCELLED", "Cancelled", "Invoice cancelled and no longer valid.");

    private InvoiceStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
