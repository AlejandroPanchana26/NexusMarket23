package application.adapters.persistence.mongodb.repositories;

import application.adapters.persistence.mongodb.documents.InventoryMovementDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// Igual que en JPA, Spring crea la implementación y arma la consulta a partir del nombre del método.
public interface SpringDataMongoInventoryMovementRepository extends MongoRepository<InventoryMovementDocument, String> {

    List<InventoryMovementDocument> findByInventoryIdentifier(String inventoryIdentifier);
}
