package application.domain.services.purchase;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultOrdersService {

    private final OrderRepositoryPort orderRepository;
    private final InvoiceRepositoryPort invoiceRepository;

    public ConsultOrdersService(OrderRepositoryPort orderRepository, InvoiceRepositoryPort invoiceRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public List<Order> listOwn(User buyerUser) {
        AccessValidator.requireRole(buyerUser, SystemRole.BUYER);
        return orderRepository.findByBuyerIdentification(buyerUser.getPerson().getIdentification());
    }

    // El vendedor ve los pedidos que incluyen al menos uno de sus productos.
    public List<Order> listForSeller(User sellerUser) {
        AccessValidator.requireRole(sellerUser, SystemRole.SELLER);
        String sellerId = sellerUser.getPerson().getIdentification();
        List<Order> result = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            for (OrderDetail detail : order.getDetails()) {
                if (sellerId.equals(detail.getProduct().getSeller().getIdentification())) {
                    result.add(order);
                    break;
                }
            }
        }
        return result;
    }

    public List<Order> listAll(User user) {
        AccessValidator.requireRole(user, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR);
        return orderRepository.findAll();
    }

    // El comprador solo puede ver la factura de sus pedidos.
    public Invoice getInvoice(User user, String orderIdentifier) {
        AccessValidator.requireRole(user, SystemRole.BUYER, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        Optional<Invoice> found = invoiceRepository.findByOrderIdentifier(orderIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Invoice");
        }
        Invoice invoice = found.get();
        if (user.hasRole(SystemRole.BUYER)
                && !invoice.getOrder().getBuyer().getIdentification().equals(user.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Buyers can only see their own invoices.");
        }
        return invoice;
    }
}
