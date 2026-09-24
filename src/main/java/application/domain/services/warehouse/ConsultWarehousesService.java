package application.domain.services.warehouse;

import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultWarehousesService {

    private final WarehouseRepositoryPort warehouseRepository;

    public ConsultWarehousesService(WarehouseRepositoryPort warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<Warehouse> listAll(User user) {
        AccessValidator.requireRole(user, SystemRole.ADMINISTRATOR, SystemRole.LOGISTICS_OPERATOR, SystemRole.SUPERVISOR);
        return warehouseRepository.findAll();
    }

    // El vendedor solo ve sus propias bodegas.
    public List<Warehouse> listOwn(User sellerUser) {
        AccessValidator.requireRole(sellerUser, SystemRole.SELLER);
        return warehouseRepository.findBySellerIdentification(sellerUser.getPerson().getIdentification());
    }
}
