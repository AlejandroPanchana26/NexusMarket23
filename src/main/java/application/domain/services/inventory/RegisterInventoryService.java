package application.domain.services.inventory;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RegisterInventoryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;

    public RegisterInventoryService(InventoryRepositoryPort inventoryRepository,
                                    InventoryMovementRepositoryPort movementRepository,
                                    ProductRepositoryPort productRepository,
                                    WarehouseRepositoryPort warehouseRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public Inventory register(User user, String productIdentifier, String warehouseIdentifier, int initialQuantity) {
        AccessValidator.requireRole(user, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);

        Optional<Product> product = productRepository.findByIdentifier(productIdentifier);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product");
        }
        if (!product.get().requiresInventory()) {
            throw new BusinessRuleViolationException("Digital products do not use inventory.");
        }
        AccessValidator.requireOwnerIfSeller(user, product.get().getSeller());

        Optional<Warehouse> warehouse = warehouseRepository.findByIdentifier(warehouseIdentifier);
        if (warehouse.isEmpty()) {
            throw new EntityNotFoundException("Warehouse");
        }
        // Un vendedor solo puede usar sus bodegas o las del marketplace.
        if (user.hasRole(SystemRole.SELLER) && !warehouse.get().isMarketplaceWarehouse()
                && !warehouse.get().belongsTo(product.get().getSeller())) {
            throw new UnauthorizedOperationException("Sellers can only use their own or marketplace warehouses.");
        }
        if (inventoryRepository.findByProductAndWarehouse(productIdentifier, warehouseIdentifier).isPresent()) {
            throw new DuplicateEntityException("Product already has inventory in this warehouse.");
        }

        Inventory inventory = new Inventory();
        inventory.setIdentifier(UUID.randomUUID().toString());
        inventory.setProduct((PhysicalProduct) product.get());
        inventory.setWarehouse(warehouse.get());
        inventory.register();
        if (initialQuantity > 0) {
            inventory.addStock(initialQuantity);
        }
        inventoryRepository.save(inventory);

        if (initialQuantity > 0) {
            InventoryMovement movement = new InventoryMovement(inventory, InventoryMovementType.ENTRY, initialQuantity, user);
            movement.setIdentifier(UUID.randomUUID().toString());
            movementRepository.save(movement);
        }
        return inventory;
    }
}
