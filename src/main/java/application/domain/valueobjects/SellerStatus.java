package application.domain.valueobjects;

public final class SellerStatus extends DomainCatalog {

    public static final SellerStatus ACTIVE = new SellerStatus(
            "ACTIVE", "Activo", "El vendedor está habilitado para publicar y administrar productos.");
    public static final SellerStatus SUSPENDED = new SellerStatus(
            "SUSPENDED", "Suspendido", "El vendedor está deshabilitado temporalmente para vender.");
    public static final SellerStatus INACTIVE = new SellerStatus(
            "INACTIVE", "Inactivo", "El vendedor existe pero no se encuentra operando actualmente en el marketplace.");

    private SellerStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
