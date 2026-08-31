package application.domain.models;

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
}
