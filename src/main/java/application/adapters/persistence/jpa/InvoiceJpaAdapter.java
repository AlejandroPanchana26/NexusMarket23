package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.adapters.persistence.jpa.mappers.InvoiceJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaInvoiceRepository;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InvoiceJpaAdapter implements InvoiceRepositoryPort {

    private final SpringDataJpaInvoiceRepository invoiceRepository;
    private final OrderRepositoryPort orderRepository;

    public InvoiceJpaAdapter(SpringDataJpaInvoiceRepository invoiceRepository, OrderRepositoryPort orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        invoiceRepository.save(InvoiceJpaMapper.toEntity(invoice));
        return invoice;
    }

    @Override
    public Optional<Invoice> findByOrderIdentifier(String orderIdentifier) {
        Optional<InvoiceJpaEntity> found = invoiceRepository.findByOrderIdentifier(orderIdentifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        for (InvoiceJpaEntity entity : invoiceRepository.findAll()) {
            invoices.add(toDomain(entity));
        }
        return invoices;
    }

    private Invoice toDomain(InvoiceJpaEntity entity) {
        Order order = orderRepository.findByIdentifier(entity.getOrderIdentifier()).orElse(null);
        return InvoiceJpaMapper.toDomain(entity, order);
    }
}
