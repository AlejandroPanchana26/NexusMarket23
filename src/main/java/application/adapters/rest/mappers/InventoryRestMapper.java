package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.responses.InventoryMovementResponse;
import application.adapters.rest.dtos.responses.InventoryResponse;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;

import java.util.ArrayList;
import java.util.List;

// Convierte inventarios y su historial de movimientos.
public final class InventoryRestMapper {

    private InventoryRestMapper() {
    }

    public static InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getIdentifier(),
                inventory.getProduct() == null ? null : inventory.getProduct().getIdentifier(),
                inventory.getProduct() == null ? null : inventory.getProduct().getName(),
                inventory.getWarehouse() == null ? null : inventory.getWarehouse().getIdentifier(),
                inventory.getWarehouse() == null ? null : inventory.getWarehouse().getName(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                CatalogMapper.toCode(inventory.getStatus()));
    }

    public static List<InventoryResponse> toResponseList(List<Inventory> inventories) {
        List<InventoryResponse> responses = new ArrayList<>();
        for (Inventory inventory : inventories) {
            responses.add(toResponse(inventory));
        }
        return responses;
    }

    public static List<InventoryMovementResponse> toMovementResponseList(List<InventoryMovement> movements) {
        List<InventoryMovementResponse> responses = new ArrayList<>();
        for (InventoryMovement movement : movements) {
            responses.add(new InventoryMovementResponse(
                    movement.getIdentifier(),
                    CatalogMapper.toCode(movement.getMovementType()),
                    movement.getQuantity(),
                    movement.getDate(),
                    movement.getPerformedBy() == null ? null : movement.getPerformedBy().getUsername()));
        }
        return responses;
    }
}
