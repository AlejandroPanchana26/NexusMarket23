package application.domain.ports.out;

import application.domain.models.Shipment;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);

    Optional<Shipment> findByIdentifier(String identifier);

    Optional<Shipment> findByOrderIdentifier(String orderIdentifier);

    List<Shipment> findAll();
}
