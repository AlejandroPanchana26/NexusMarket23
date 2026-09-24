package application.domain.ports.out;

import application.domain.models.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {

    Inventory save(Inventory inventory);

    Optional<Inventory> findByIdentifier(String identifier);

    // Un mismo producto puede tener existencias en varias bodegas.
    List<Inventory> findByProductIdentifier(String productIdentifier);

    Optional<Inventory> findByProductAndWarehouse(String productIdentifier, String warehouseIdentifier);

    List<Inventory> findAll();
}
