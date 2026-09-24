package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultInventoryService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final ProductRepositoryPort productRepository;

    public ConsultInventoryService(InventoryRepositoryPort inventoryRepository,
                                   InventoryMovementRepositoryPort movementRepository,
                                   ProductRepositoryPort productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
    }

    public List<Inventory> listAll(User user) {
        AccessValidator.requireRole(user, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return inventoryRepository.findAll();
    }

    // Existencias de un producto en todas sus bodegas.
    public List<Inventory> listByProduct(User user, String productIdentifier) {
        AccessValidator.requireRole(user, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        Optional<Product> product = productRepository.findByIdentifier(productIdentifier);
        if (product.isEmpty()) {
            throw new EntityNotFoundException("Product");
        }
        AccessValidator.requireOwnerIfSeller(user, product.get().getSeller());
        return inventoryRepository.findByProductIdentifier(productIdentifier);
    }

    // Historial de movimientos de un registro de inventario.
    public List<InventoryMovement> listMovements(User user, String inventoryIdentifier) {
        AccessValidator.requireRole(user, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        Optional<Inventory> inventory = inventoryRepository.findByIdentifier(inventoryIdentifier);
        if (inventory.isEmpty()) {
            throw new EntityNotFoundException("Inventory");
        }
        AccessValidator.requireOwnerIfSeller(user, inventory.get().getProduct().getSeller());
        return movementRepository.findByInventoryIdentifier(inventoryIdentifier);
    }
}
