package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.InvoiceJpaEntity;
import application.domain.models.Invoice;
import application.domain.models.Order;

public final class InvoiceJpaMapper {

    private InvoiceJpaMapper() {
    }

    public static InvoiceJpaEntity toEntity(Invoice invoice) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();
        entity.setIdentifier(invoice.getIdentifier());
        entity.setOrderIdentifier(invoice.getOrder().getIdentifier());
        entity.setIssueDate(invoice.getIssueDate());
        entity.setTotalAmount(invoice.getTotalAmount());
        entity.setStatus(CatalogMapper.toCode(invoice.getStatus()));
        return entity;
    }

    public static Invoice toDomain(InvoiceJpaEntity entity, Order order) {
        Invoice invoice = new Invoice();
        invoice.setIdentifier(entity.getIdentifier());
        invoice.setOrder(order);
        invoice.setIssueDate(entity.getIssueDate());
        invoice.setTotalAmount(entity.getTotalAmount());
        invoice.setStatus(CatalogMapper.invoiceStatus(entity.getStatus()));
        return invoice;
    }
}
