package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.domain.models.Inventory;
import application.domain.models.PhysicalProduct;
import application.domain.models.Warehouse;

public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static InventoryJpaEntity toEntity(Inventory inventory) {
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setIdentifier(inventory.getIdentifier());
        entity.setProductIdentifier(inventory.getProduct().getIdentifier());
        entity.setWarehouseIdentifier(inventory.getWarehouse().getIdentifier());
        entity.setAvailableQuantity(inventory.getAvailableQuantity());
        entity.setReservedQuantity(inventory.getReservedQuantity());
        entity.setStatus(CatalogMapper.toCode(inventory.getStatus()));
        return entity;
    }

    // El producto y la bodega ya vienen convertidos desde el adaptador.
    public static Inventory toDomain(InventoryJpaEntity entity, PhysicalProduct product, Warehouse warehouse) {
        Inventory inventory = new Inventory();
        inventory.setIdentifier(entity.getIdentifier());
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setAvailableQuantity(entity.getAvailableQuantity());
        inventory.setReservedQuantity(entity.getReservedQuantity());
        inventory.setStatus(CatalogMapper.inventoryStatus(entity.getStatus()));
        return inventory;
    }
}
