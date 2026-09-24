package application.domain.valueobjects;

public final class SystemRole extends DomainCatalog {

    public static final SystemRole BUYER = new SystemRole(
            "BUYER", "Buyer", "Person who purchases products published in the marketplace.");
    public static final SystemRole SELLER = new SystemRole(
            "SELLER", "Seller", "Person responsible for registering and managing their own products.");
    public static final SystemRole LOGISTICS_OPERATOR = new SystemRole(
            "LOGISTICS_OPERATOR", "Logistics Operator", "User in charge of the physical operation of warehouses and shipments.");
    public static final SystemRole ADMINISTRATOR = new SystemRole(
            "ADMINISTRATOR", "Administrator", "User responsible for managing sellers and warehouses.");
    public static final SystemRole SUPERVISOR = new SystemRole(
            "SUPERVISOR", "Supervisor", "Consultation and operational monitoring profile.");

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }
}
