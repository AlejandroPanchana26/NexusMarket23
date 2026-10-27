package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaInventoryRepository extends JpaRepository<InventoryJpaEntity, String> {

    List<InventoryJpaEntity> findByProductIdentifier(String productIdentifier);

    Optional<InventoryJpaEntity> findByProductIdentifierAndWarehouseIdentifier(String productIdentifier,
                                                                                String warehouseIdentifier);
}
