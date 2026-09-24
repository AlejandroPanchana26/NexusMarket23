package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// Cambios manuales de existencias. Cada cambio de cantidad queda registrado como movimiento.
@Service
public class ManageStockService {

    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;

    public ManageStockService(InventoryRepositoryPort inventoryRepository,
                              InventoryMovementRepositoryPort movementRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    public Inventory addStock(User user, String inventoryIdentifier, int quantity) {
        Inventory inventory = findInventory(user, inventoryIdentifier);
        inventory.addStock(quantity);
        inventoryRepository.save(inventory);
        saveMovement(inventory, InventoryMovementType.ENTRY, quantity, user);
        return inventory;
    }

    // Se usa después de un conteo físico; el movimiento guarda la nueva cantidad disponible.
    public Inventory adjust(User user, String inventoryIdentifier, int newAvailableQuantity) {
        Inventory inventory = findInventory(user, inventoryIdentifier);
        inventory.adjust(newAvailableQuantity);
        inventoryRepository.save(inventory);
        saveMovement(inventory, InventoryMovementType.ADJUSTMENT, newAvailableQuantity, user);
        return inventory;
    }

    public Inventory markAsDamaged(User user, String inventoryIdentifier) {
        Inventory inventory = findInventory(user, inventoryIdentifier);
        inventory.markAsDamaged();
        return inventoryRepository.save(inventory);
    }

    private Inventory findInventory(User user, String inventoryIdentifier) {
        AccessValidator.requireRole(user, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        Optional<Inventory> found = inventoryRepository.findByIdentifier(inventoryIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Inventory");
        }
        AccessValidator.requireOwnerIfSeller(user, found.get().getProduct().getSeller());
        return found.get();
    }

    private void saveMovement(Inventory inventory, InventoryMovementType type, int quantity, User user) {
        InventoryMovement movement = new InventoryMovement(inventory, type, quantity, user);
        movement.setIdentifier(UUID.randomUUID().toString());
        movementRepository.save(movement);
    }
}
