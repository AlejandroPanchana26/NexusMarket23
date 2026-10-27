package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInventoryRepository;
import application.domain.models.Inventory;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryJpaAdapter implements InventoryRepositoryPort {

    private final SpringDataJpaInventoryRepository inventoryRepository;
    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;

    public InventoryJpaAdapter(SpringDataJpaInventoryRepository inventoryRepository,
                               ProductRepositoryPort productRepository,
                               WarehouseRepositoryPort warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Inventory save(Inventory inventory) {
        inventoryRepository.save(InventoryJpaMapper.toEntity(inventory));
        return inventory;
    }

    @Override
    public Optional<Inventory> findByIdentifier(String identifier) {
        Optional<InventoryJpaEntity> found = inventoryRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Inventory> findByProductIdentifier(String productIdentifier) {
        return toDomainList(inventoryRepository.findByProductIdentifier(productIdentifier));
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(String productIdentifier, String warehouseIdentifier) {
        Optional<InventoryJpaEntity> found = inventoryRepository
                .findByProductIdentifierAndWarehouseIdentifier(productIdentifier, warehouseIdentifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Inventory> findAll() {
        return toDomainList(inventoryRepository.findAll());
    }

    private List<Inventory> toDomainList(List<InventoryJpaEntity> entities) {
        List<Inventory> inventories = new ArrayList<>();
        for (InventoryJpaEntity entity : entities) {
            inventories.add(toDomain(entity));
        }
        return inventories;
    }

    // Busca el producto y la bodega para devolver el inventario completo.
    private Inventory toDomain(InventoryJpaEntity entity) {
        PhysicalProduct product = null;
        Optional<Product> foundProduct = productRepository.findByIdentifier(entity.getProductIdentifier());
        if (foundProduct.isPresent() && foundProduct.get() instanceof PhysicalProduct physical) {
            product = physical;
        }
        Warehouse warehouse = warehouseRepository.findByIdentifier(entity.getWarehouseIdentifier()).orElse(null);
        return InventoryJpaMapper.toDomain(entity, product, warehouse);
    }
}
