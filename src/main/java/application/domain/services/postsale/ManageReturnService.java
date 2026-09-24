package application.domain.services.postsale;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// La devolución aplica al pedido completo.
@Service
public class ManageReturnService {

    private final ReturnRepositoryPort returnRepository;
    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;

    public ManageReturnService(ReturnRepositoryPort returnRepository,
                               OrderRepositoryPort orderRepository,
                               InventoryRepositoryPort inventoryRepository,
                               InventoryMovementRepositoryPort movementRepository) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    public Return request(User buyerUser, String orderIdentifier, String reason) {
        AccessValidator.requireRole(buyerUser, SystemRole.BUYER);
        Optional<Order> found = orderRepository.findByIdentifier(orderIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Order");
        }
        Order order = found.get();
        if (!order.getBuyer().getIdentification().equals(buyerUser.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Buyers can only return their own orders.");
        }
        // Solo se permite una nueva solicitud si las anteriores fueron rechazadas.
        for (Return previous : returnRepository.findByOrderIdentifier(orderIdentifier)) {
            if (!ReturnStatus.REJECTED.equals(previous.getStatus())) {
                throw new DuplicateEntityException("This order already has a return in process.");
            }
        }
        Return returnRequest = new Return();
        returnRequest.setIdentifier(UUID.randomUUID().toString());
        returnRequest.requestFor(order, reason);
        return returnRepository.save(returnRequest);
    }

    public Return approve(User user, String returnIdentifier) {
        Return returnRequest = findReturn(returnIdentifier);
        requireApprover(user, returnRequest.getOrder());
        returnRequest.approve();
        return returnRepository.save(returnRequest);
    }

    public Return reject(User user, String returnIdentifier) {
        Return returnRequest = findReturn(returnIdentifier);
        requireApprover(user, returnRequest.getOrder());
        returnRequest.reject();
        return returnRepository.save(returnRequest);
    }

    // Al recibir el producto devuelto, las unidades vuelven a la bodega de donde salieron.
    public Return complete(User user, String returnIdentifier) {
        AccessValidator.requireRole(user, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR);
        Return returnRequest = findReturn(returnIdentifier);
        returnRequest.complete();

        Order order = returnRequest.getOrder();
        for (OrderDetail detail : order.getDetails()) {
            if (!detail.getProduct().requiresInventory()) {
                continue;
            }
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(
                    detail.getProduct().getIdentifier(), order.getFulfillmentWarehouse().getIdentifier()).get();
            inventory.receiveReturn(detail.getQuantity());
            inventoryRepository.save(inventory);

            InventoryMovement movement = new InventoryMovement(inventory, InventoryMovementType.RETURN,
                    detail.getQuantity(), user);
            movement.setIdentifier(UUID.randomUUID().toString());
            movementRepository.save(movement);
        }
        return returnRepository.save(returnRequest);
    }

    private Return findReturn(String returnIdentifier) {
        Optional<Return> found = returnRepository.findByIdentifier(returnIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Return");
        }
        return found.get();
    }

    // Aprueba el administrador o un vendedor que tenga productos en el pedido.
    static void requireApprover(User user, Order order) {
        AccessValidator.requireRole(user, SystemRole.ADMINISTRATOR, SystemRole.SELLER);
        if (!user.hasRole(SystemRole.SELLER)) {
            return;
        }
        String sellerId = user.getPerson().getIdentification();
        for (OrderDetail detail : order.getDetails()) {
            if (sellerId.equals(detail.getProduct().getSeller().getIdentification())) {
                return;
            }
        }
        throw new UnauthorizedOperationException("Sellers can only manage returns of their own products.");
    }
}
