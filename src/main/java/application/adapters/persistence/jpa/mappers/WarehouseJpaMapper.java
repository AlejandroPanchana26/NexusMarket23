package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.domain.models.Seller;
import application.domain.models.Warehouse;

public final class WarehouseJpaMapper {

    private WarehouseJpaMapper() {
    }

    public static WarehouseJpaEntity toEntity(Warehouse warehouse) {
        WarehouseJpaEntity entity = new WarehouseJpaEntity();
        entity.setIdentifier(warehouse.getIdentifier());
        entity.setName(warehouse.getName());
        entity.setAddress(warehouse.getAddress());
        entity.setType(CatalogMapper.toCode(warehouse.getType()));
        if (warehouse.getOwner() != null) {
            entity.setOwnerIdentification(warehouse.getOwner().getIdentification());
        }
        return entity;
    }

    // El dueño ya viene convertido desde el adaptador; es null si la bodega es del marketplace.
    public static Warehouse toDomain(WarehouseJpaEntity entity, Seller owner) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(entity.getIdentifier());
        warehouse.setName(entity.getName());
        warehouse.setAddress(entity.getAddress());
        warehouse.setType(CatalogMapper.warehouseType(entity.getType()));
        warehouse.setOwner(owner);
        return warehouse;
    }
}
