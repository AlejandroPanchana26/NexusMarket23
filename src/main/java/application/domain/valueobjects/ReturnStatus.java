package application.domain.valueobjects;

public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Solicitada", "El comprador solicitó la devolución del pedido.");
    public static final ReturnStatus APPROVED = new ReturnStatus(
            "APPROVED", "Aprobada", "La devolución fue aprobada y puede continuar.");
    public static final ReturnStatus REJECTED = new ReturnStatus(
            "REJECTED", "Rechazada", "La devolución fue rechazada.");
    public static final ReturnStatus COMPLETED = new ReturnStatus(
            "COMPLETED", "Completada", "La devolución finalizó satisfactoriamente.");

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
