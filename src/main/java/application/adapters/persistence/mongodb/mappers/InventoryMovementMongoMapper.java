package application.adapters.persistence.mongodb.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.mongodb.documents.InventoryMovementDocument;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;

public final class InventoryMovementMongoMapper {

    private InventoryMovementMongoMapper() {
    }

    public static InventoryMovementDocument toDocument(InventoryMovement movement) {
        InventoryMovementDocument document = new InventoryMovementDocument();
        document.setId(movement.getIdentifier());
        document.setInventoryIdentifier(movement.getInventory().getIdentifier());
        document.setProductIdentifier(movement.getInventory().getProduct().getIdentifier());
        document.setWarehouseIdentifier(movement.getInventory().getWarehouse().getIdentifier());
        document.setMovementType(CatalogMapper.toCode(movement.getMovementType()));
        document.setQuantity(movement.getQuantity());
        document.setDate(movement.getDate());
        document.setPerformedByUsername(movement.getPerformedBy().getUsername());
        return document;
    }

    // Se usa el constructor vacío para conservar la fecha guardada.
    public static InventoryMovement toDomain(InventoryMovementDocument document, Inventory inventory, User performedBy) {
        InventoryMovement movement = new InventoryMovement();
        movement.setIdentifier(document.getId());
        movement.setInventory(inventory);
        movement.setMovementType(CatalogMapper.inventoryMovementType(document.getMovementType()));
        movement.setQuantity(document.getQuantity());
        movement.setDate(document.getDate());
        movement.setPerformedBy(performedBy);
        return movement;
    }
}
