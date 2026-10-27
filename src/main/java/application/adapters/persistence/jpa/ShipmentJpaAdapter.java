package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import application.adapters.persistence.jpa.mappers.ShipmentJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaShipmentRepository;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ShipmentJpaAdapter implements ShipmentRepositoryPort {

    private final SpringDataJpaShipmentRepository shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final UserRepositoryPort userRepository;

    public ShipmentJpaAdapter(SpringDataJpaShipmentRepository shipmentRepository,
                              OrderRepositoryPort orderRepository,
                              WarehouseRepositoryPort warehouseRepository,
                              UserRepositoryPort userRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        shipmentRepository.save(ShipmentJpaMapper.toEntity(shipment));
        return shipment;
    }

    @Override
    public Optional<Shipment> findByIdentifier(String identifier) {
        Optional<ShipmentJpaEntity> found = shipmentRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public Optional<Shipment> findByOrderIdentifier(String orderIdentifier) {
        Optional<ShipmentJpaEntity> found = shipmentRepository.findByOrderIdentifier(orderIdentifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Shipment> findAll() {
        List<Shipment> shipments = new ArrayList<>();
        for (ShipmentJpaEntity entity : shipmentRepository.findAll()) {
            shipments.add(toDomain(entity));
        }
        return shipments;
    }

    // Busca el pedido, la bodega de origen y el operador logístico.
    private Shipment toDomain(ShipmentJpaEntity entity) {
        Order order = orderRepository.findByIdentifier(entity.getOrderIdentifier()).orElse(null);
        Warehouse origin = warehouseRepository.findByIdentifier(entity.getOriginWarehouseIdentifier()).orElse(null);
        User operator = userRepository.findByUsername(entity.getLogisticsOperatorUsername()).orElse(null);
        return ShipmentJpaMapper.toDomain(entity, order, origin, operator);
    }
}
