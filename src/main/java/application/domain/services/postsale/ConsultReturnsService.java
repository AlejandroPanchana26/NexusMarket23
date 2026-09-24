package application.domain.services.postsale;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConsultReturnsService {

    private final ReturnRepositoryPort returnRepository;
    private final RefundRepositoryPort refundRepository;
    private final OrderRepositoryPort orderRepository;

    public ConsultReturnsService(ReturnRepositoryPort returnRepository,
                                 RefundRepositoryPort refundRepository,
                                 OrderRepositoryPort orderRepository) {
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
    }

    // Por ejemplo, las devoluciones REQUESTED son las que están esperando respuesta.
    public List<Return> listByStatus(User user, ReturnStatus status) {
        AccessValidator.requireRole(user, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR);
        return returnRepository.findByStatus(status);
    }

    public List<Return> listForOrder(User user, String orderIdentifier) {
        requireCanSeeOrder(user, orderIdentifier);
        return returnRepository.findByOrderIdentifier(orderIdentifier);
    }

    public Refund findRefund(User user, String returnIdentifier) {
        Optional<Return> foundReturn = returnRepository.findByIdentifier(returnIdentifier);
        if (foundReturn.isEmpty()) {
            throw new EntityNotFoundException("Return");
        }
        requireCanSeeOrder(user, foundReturn.get().getOrder().getIdentifier());
        Optional<Refund> found = refundRepository.findByReturnIdentifier(returnIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Refund");
        }
        return found.get();
    }

    // El comprador solo ve lo relacionado con sus propios pedidos.
    private void requireCanSeeOrder(User user, String orderIdentifier) {
        AccessValidator.requireRole(user, SystemRole.BUYER, SystemRole.ADMINISTRATOR,
                SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR);
        Optional<Order> order = orderRepository.findByIdentifier(orderIdentifier);
        if (order.isEmpty()) {
            throw new EntityNotFoundException("Order");
        }
        String buyerId = order.get().getBuyer().getIdentification();
        if (user.hasRole(SystemRole.BUYER) && !buyerId.equals(user.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Buyers can only see their own returns.");
        }
    }
}
