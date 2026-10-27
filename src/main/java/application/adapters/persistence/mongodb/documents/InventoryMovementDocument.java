package application.adapters.persistence.mongodb.documents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// Cada movimiento es un documento de la colección inventory_movements.
// Se guardan también el producto y la bodega para que el documento se entienda por sí solo.
@Document(collection = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
public class InventoryMovementDocument {

    @Id
    private String id;

    private String inventoryIdentifier;
    private String productIdentifier;
    private String warehouseIdentifier;

    // ENTRY, RESERVATION, SALE_EXIT, ADJUSTMENT o RETURN.
    private String movementType;

    private Integer quantity;
    private LocalDateTime date;
    private String performedByUsername;
}
