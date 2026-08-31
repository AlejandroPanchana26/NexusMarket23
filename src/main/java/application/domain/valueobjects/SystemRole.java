package application.domain.valueobjects;

public final class SystemRole extends DomainCatalog {

    public static final SystemRole BUYER = new SystemRole(
            "BUYER", "Comprador", "Persona que adquiere los productos publicados en el marketplace.");
    public static final SystemRole SELLER = new SystemRole(
            "SELLER", "Vendedor", "Persona responsable de registrar y administrar sus propios productos.");
    public static final SystemRole LOGISTICS_OPERATOR = new SystemRole(
            "LOGISTICS_OPERATOR", "Operador Logístico", "Usuario encargado de la operación física de bodegas y despachos.");
    public static final SystemRole ADMINISTRATOR = new SystemRole(
            "ADMINISTRATOR", "Administrador", "Usuario responsable de la administración de vendedores y bodegas.");
    public static final SystemRole SUPERVISOR = new SystemRole(
            "SUPERVISOR", "Supervisor", "Perfil de consulta y seguimiento operativo.");

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }
}
