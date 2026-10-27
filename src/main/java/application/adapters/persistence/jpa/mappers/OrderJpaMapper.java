package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.OrderLineEmbeddable;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.models.Product;
import application.domain.models.Warehouse;

public final class OrderJpaMapper {

    private OrderJpaMapper() {
    }

    public static OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setIdentifier(order.getIdentifier());
        entity.setBuyerIdentification(order.getBuyer().getIdentification());
        entity.setStatus(CatalogMapper.toCode(order.getStatus()));
        entity.setCreationDate(order.getCreationDate());
        entity.setTotal(order.getTotal());
        if (order.getFulfillmentWarehouse() != null) {
            entity.setFulfillmentWarehouseIdentifier(order.getFulfillmentWarehouse().getIdentifier());
        }
        for (OrderDetail detail : order.getDetails()) {
            OrderLineEmbeddable line = new OrderLineEmbeddable();
            line.setProductIdentifier(detail.getProduct().getIdentifier());
            line.setQuantity(detail.getQuantity());
            line.setUnitPrice(detail.getUnitPrice());
            line.setSubtotal(detail.getSubtotal());
            entity.getDetails().add(line);
        }
        return entity;
    }

    // Las líneas se agregan después desde el adaptador, cuando ya tiene cada producto.
    public static Order toDomain(OrderJpaEntity entity, Buyer buyer, Warehouse fulfillmentWarehouse) {
        Order order = new Order();
        order.setIdentifier(entity.getIdentifier());
        order.setBuyer(buyer);
        order.setStatus(CatalogMapper.orderStatus(entity.getStatus()));
        order.setCreationDate(entity.getCreationDate());
        order.setTotal(entity.getTotal());
        order.setFulfillmentWarehouse(fulfillmentWarehouse);
        return order;
    }

    // Se conservan el precio y el subtotal guardados al momento de la compra.
    public static OrderDetail toDomainDetail(OrderLineEmbeddable line, Product product) {
        OrderDetail detail = new OrderDetail();
        detail.setProduct(product);
        detail.setQuantity(line.getQuantity());
        detail.setUnitPrice(line.getUnitPrice());
        detail.setSubtotal(line.getSubtotal());
        return detail;
    }
}
