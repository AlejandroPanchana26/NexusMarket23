package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;

public final class ShipmentJpaMapper {

    private ShipmentJpaMapper() {
    }

    public static ShipmentJpaEntity toEntity(Shipment shipment) {
        ShipmentJpaEntity entity = new ShipmentJpaEntity();
        entity.setIdentifier(shipment.getIdentifier());
        entity.setOrderIdentifier(shipment.getOrder().getIdentifier());
        entity.setOriginWarehouseIdentifier(shipment.getOriginWarehouse().getIdentifier());
        entity.setDestinationAddress(shipment.getDestinationAddress());
        entity.setStatus(CatalogMapper.toCode(shipment.getStatus()));
        entity.setLogisticsOperatorUsername(shipment.getLogisticsOperator().getUsername());
        entity.setShipmentDate(shipment.getShipmentDate());
        entity.setDeliveryDate(shipment.getDeliveryDate());
        return entity;
    }

    // El pedido, la bodega y el operador ya vienen convertidos desde el adaptador.
    public static Shipment toDomain(ShipmentJpaEntity entity, Order order, Warehouse origin, User operator) {
        Shipment shipment = new Shipment();
        shipment.setIdentifier(entity.getIdentifier());
        shipment.setOrder(order);
        shipment.setOriginWarehouse(origin);
        shipment.setDestinationAddress(entity.getDestinationAddress());
        shipment.setStatus(CatalogMapper.shipmentStatus(entity.getStatus()));
        shipment.setLogisticsOperator(operator);
        shipment.setShipmentDate(entity.getShipmentDate());
        shipment.setDeliveryDate(entity.getDeliveryDate());
        return shipment;
    }
}
