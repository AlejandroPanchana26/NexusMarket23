package application.adapters.persistence.mongodb;

import application.adapters.persistence.mongodb.documents.InventoryMovementDocument;
import application.adapters.persistence.mongodb.mappers.InventoryMovementMongoMapper;
import application.adapters.persistence.mongodb.repositories.SpringDataMongoInventoryMovementRepository;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

// Implementa el puerto del historial de movimientos guardando en MongoDB.
@Repository
public class InventoryMovementMongoAdapter implements InventoryMovementRepositoryPort {

    private final SpringDataMongoInventoryMovementRepository movementRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final UserRepositoryPort userRepository;

    public InventoryMovementMongoAdapter(SpringDataMongoInventoryMovementRepository movementRepository,
                                         InventoryRepositoryPort inventoryRepository,
                                         UserRepositoryPort userRepository) {
        this.movementRepository = movementRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        movementRepository.save(InventoryMovementMongoMapper.toDocument(movement));
        return movement;
    }

    @Override
    public List<InventoryMovement> findByInventoryIdentifier(String inventoryIdentifier) {
        // El inventario es el mismo para todo el historial, así que se busca una sola vez.
        Inventory inventory = inventoryRepository.findByIdentifier(inventoryIdentifier).orElse(null);
        List<InventoryMovement> movements = new ArrayList<>();
        for (InventoryMovementDocument document : movementRepository.findByInventoryIdentifier(inventoryIdentifier)) {
            User performedBy = userRepository.findByUsername(document.getPerformedByUsername()).orElse(null);
            movements.add(InventoryMovementMongoMapper.toDomain(document, inventory, performedBy));
        }
        return movements;
    }
}
