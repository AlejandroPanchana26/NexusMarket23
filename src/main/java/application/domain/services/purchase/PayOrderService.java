package application.domain.services.purchase;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;

// El pago se registra como confirmado: el PDF no define una pasarela de pagos.
@Service
public class PayOrderService {

    private final OrderRepositoryPort orderRepository;
    private final InvoiceRepositoryPort invoiceRepository;

    public PayOrderService(OrderRepositoryPort orderRepository, InvoiceRepositoryPort invoiceRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Order pay(User buyerUser, String orderIdentifier) {
        AccessValidator.requireRole(buyerUser, SystemRole.BUYER);
        Optional<Order> foundOrder = orderRepository.findByIdentifier(orderIdentifier);
        if (foundOrder.isEmpty()) {
            throw new EntityNotFoundException("Order");
        }
        Order order = foundOrder.get();
        if (!order.getBuyer().getIdentification().equals(buyerUser.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Buyers can only pay their own orders.");
        }
        Optional<Invoice> foundInvoice = invoiceRepository.findByOrderIdentifier(orderIdentifier);
        if (foundInvoice.isEmpty()) {
            throw new EntityNotFoundException("Invoice");
        }

        order.markAsPaid();
        foundInvoice.get().markAsPaid();

        // Los productos digitales se entregan apenas se paga.
        if (!order.containsPhysicalProducts()) {
            order.markAsDelivered();
        }
        invoiceRepository.save(foundInvoice.get());
        return orderRepository.save(order);
    }
}
