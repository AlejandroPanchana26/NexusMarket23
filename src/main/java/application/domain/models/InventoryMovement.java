package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.InventoryMovementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InventoryMovement {

    private String identifier;

    // Inventario afectado por este movimiento.
    private Inventory inventory;

    // Tipo de movimiento: ingreso, reserva, salida por venta, ajuste o devolución.
    private InventoryMovementType movementType;

    private Integer quantity;

    // Fecha y hora en que ocurrió el movimiento.
    private LocalDateTime date;

    // Usuario que realizó el movimiento.
    private User performedBy;

    public InventoryMovement(Inventory inventory, InventoryMovementType movementType,
                             int quantity, User performedBy) {
        if (inventory == null || movementType == null || performedBy == null) {
            throw new BusinessRuleViolationException("Movement requires inventory, type and user.");
        }
        if (quantity < 0) {
            throw new BusinessRuleViolationException("Movement quantity cannot be negative.");
        }
        this.inventory = inventory;
        this.movementType = movementType;
        this.quantity = quantity;
        this.performedBy = performedBy;
        this.date = LocalDateTime.now();
    }
}
