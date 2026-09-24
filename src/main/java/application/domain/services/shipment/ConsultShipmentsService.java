package application.domain.services.shipment;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultShipmentsService {

    private final ShipmentRepositoryPort shipmentRepository;

    public ConsultShipmentsService(ShipmentRepositoryPort shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public List<Shipment> listAll(User user) {
        AccessValidator.requireRole(user, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return shipmentRepository.findAll();
    }

    // El comprador solo puede seguir el envío de sus propios pedidos.
    public Shipment findByOrder(User user, String orderIdentifier) {
        AccessValidator.requireRole(user, SystemRole.BUYER, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        Optional<Shipment> found = shipmentRepository.findByOrderIdentifier(orderIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Shipment");
        }
        Shipment shipment = found.get();
        String buyerId = shipment.getOrder().getBuyer().getIdentification();
        if (user.hasRole(SystemRole.BUYER) && !buyerId.equals(user.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Buyers can only see their own shipments.");
        }
        return shipment;
    }
}
