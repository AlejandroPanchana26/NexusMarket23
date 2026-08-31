package application.domain.valueobjects;

public final class BuyerStatus extends DomainCatalog {

    public static final BuyerStatus ACTIVE = new BuyerStatus(
            "ACTIVE", "Activo", "El comprador está habilitado para realizar pedidos en el marketplace.");
    public static final BuyerStatus SUSPENDED = new BuyerStatus(
            "SUSPENDED", "Suspendido", "El comprador está deshabilitado temporalmente para realizar pedidos.");
    public static final BuyerStatus BLOCKED = new BuyerStatus(
            "BLOCKED", "Bloqueado", "La actividad comercial del comprador ha sido deshabilitada de forma permanente.");

    private BuyerStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
