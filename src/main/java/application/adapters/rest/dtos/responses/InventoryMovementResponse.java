package application.adapters.rest.dtos.responses;

import java.time.LocalDateTime;

public record InventoryMovementResponse(String identifier, String movementType, Integer quantity,
                                        LocalDateTime date, String performedBy) {
}
