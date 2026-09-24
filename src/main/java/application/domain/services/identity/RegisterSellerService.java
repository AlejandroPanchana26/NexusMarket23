package application.domain.services.identity;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.UUID;

// Los vendedores no se auto-registran: el administrador los registra junto con su primera bodega.
@Service
public class RegisterSellerService {

    private final PersonRepositoryPort personRepository;
    private final WarehouseRepositoryPort warehouseRepository;
    private final UserAccountService userAccountService;

    public RegisterSellerService(PersonRepositoryPort personRepository,
                                 WarehouseRepositoryPort warehouseRepository,
                                 UserAccountService userAccountService) {
        this.personRepository = personRepository;
        this.warehouseRepository = warehouseRepository;
        this.userAccountService = userAccountService;
    }

    public User register(User administrator, Seller seller, Warehouse firstWarehouse,
                         String username, String password) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        if (seller == null || firstWarehouse == null) {
            throw new BusinessRuleViolationException("Seller and first warehouse are required.");
        }
        seller.register();
        userAccountService.validateNewAccount(seller, username, password);

        firstWarehouse.setIdentifier(UUID.randomUUID().toString());
        seller.addWarehouse(firstWarehouse);
        firstWarehouse.register();

        personRepository.save(seller);
        warehouseRepository.save(firstWarehouse);
        return userAccountService.createUser(seller, username, password);
    }
}
