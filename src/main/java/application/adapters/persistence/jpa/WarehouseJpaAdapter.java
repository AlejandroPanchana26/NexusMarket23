package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.WarehouseJpaEntity;
import application.adapters.persistence.jpa.mappers.WarehouseJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaWarehouseRepository;
import application.domain.models.Person;
import application.domain.models.Seller;
import application.domain.models.Warehouse;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseJpaAdapter implements WarehouseRepositoryPort {

    private final SpringDataJpaWarehouseRepository warehouseRepository;
    private final PersonRepositoryPort personRepository;

    public WarehouseJpaAdapter(SpringDataJpaWarehouseRepository warehouseRepository,
                               PersonRepositoryPort personRepository) {
        this.warehouseRepository = warehouseRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        warehouseRepository.save(WarehouseJpaMapper.toEntity(warehouse));
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findByIdentifier(String identifier) {
        Optional<WarehouseJpaEntity> found = warehouseRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Warehouse> findBySellerIdentification(String sellerIdentification) {
        return toDomainList(warehouseRepository.findByOwnerIdentification(sellerIdentification));
    }

    @Override
    public List<Warehouse> findAll() {
        return toDomainList(warehouseRepository.findAll());
    }

    private List<Warehouse> toDomainList(List<WarehouseJpaEntity> entities) {
        List<Warehouse> warehouses = new ArrayList<>();
        for (WarehouseJpaEntity entity : entities) {
            warehouses.add(toDomain(entity));
        }
        return warehouses;
    }

    // Si la bodega tiene dueño, se busca el vendedor para devolverla completa.
    private Warehouse toDomain(WarehouseJpaEntity entity) {
        Seller owner = null;
        if (entity.getOwnerIdentification() != null) {
            Optional<Person> person = personRepository.findByIdentification(entity.getOwnerIdentification());
            if (person.isPresent() && person.get() instanceof Seller seller) {
                owner = seller;
            }
        }
        return WarehouseJpaMapper.toDomain(entity, owner);
    }
}
