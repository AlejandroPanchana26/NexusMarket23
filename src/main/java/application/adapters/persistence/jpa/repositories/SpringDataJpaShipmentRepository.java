package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ShipmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaShipmentRepository extends JpaRepository<ShipmentJpaEntity, String> {

    Optional<ShipmentJpaEntity> findByOrderIdentifier(String orderIdentifier);
}
