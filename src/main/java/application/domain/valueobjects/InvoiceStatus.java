package application.domain.valueobjects;

public final class InvoiceStatus extends DomainCatalog {

    public static final InvoiceStatus ISSUED = new InvoiceStatus(
            "ISSUED", "Emitida", "La factura fue generada para el pedido.");
    public static final InvoiceStatus PAID = new InvoiceStatus(
            "PAID", "Pagada", "La factura fue cancelada por el comprador.");
    public static final InvoiceStatus CANCELLED = new InvoiceStatus(
            "CANCELLED", "Anulada", "La factura fue anulada y no tiene validez.");

    private InvoiceStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
