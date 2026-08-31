package application.domain.valueobjects;

public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus PENDING = new RefundStatus(
            "PENDING", "Pendiente", "El reembolso está pendiente de ser procesado.");
    public static final RefundStatus PROCESSED = new RefundStatus(
            "PROCESSED", "Procesado", "El reembolso fue entregado al comprador.");
    public static final RefundStatus REJECTED = new RefundStatus(
            "REJECTED", "Rechazado", "El reembolso fue rechazado.");

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
