package application.domain.services.warehouse;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.services.identity.PersonLookupService;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegisterWarehouseService {

    private final WarehouseRepositoryPort warehouseRepository;
    private final PersonLookupService personLookup;

    public RegisterWarehouseService(WarehouseRepositoryPort warehouseRepository, PersonLookupService personLookup) {
        this.warehouseRepository = warehouseRepository;
        this.personLookup = personLookup;
    }

    // Sin vendedor se crea una bodega del marketplace; con vendedor, una bodega adicional de ese vendedor.
    public Warehouse register(User administrator, Warehouse warehouse, String sellerIdentification) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        if (warehouse == null) {
            throw new BusinessRuleViolationException("Warehouse data is required.");
        }
        warehouse.setIdentifier(UUID.randomUUID().toString());

        if (sellerIdentification == null || sellerIdentification.isBlank()) {
            warehouse.setType(WarehouseType.MARKETPLACE);
            warehouse.setOwner(null);
        } else {
            Seller seller = personLookup.findSeller(sellerIdentification);
            seller.addWarehouse(warehouse);
        }
        warehouse.register();
        return warehouseRepository.save(warehouse);
    }
}
