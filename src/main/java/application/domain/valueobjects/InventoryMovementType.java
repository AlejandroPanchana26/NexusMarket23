package application.domain.valueobjects;

public final class InventoryMovementType extends DomainCatalog {

    public static final InventoryMovementType ENTRY = new InventoryMovementType(
            "ENTRY", "Ingreso", "Entrada de nuevas existencias a la bodega.");
    public static final InventoryMovementType RESERVATION = new InventoryMovementType(
            "RESERVATION", "Reserva", "Existencias apartadas para un pedido en proceso.");
    public static final InventoryMovementType SALE_EXIT = new InventoryMovementType(
            "SALE_EXIT", "Salida por venta", "Existencias que salen de la bodega por una venta confirmada.");
    public static final InventoryMovementType ADJUSTMENT = new InventoryMovementType(
            "ADJUSTMENT", "Ajuste", "Corrección manual de las existencias registradas.");
    public static final InventoryMovementType RETURN = new InventoryMovementType(
            "RETURN", "Devolución", "Reingreso de existencias por una devolución.");

    private InventoryMovementType(String code, String name, String description) {
        super(code, name, description);
    }
}
