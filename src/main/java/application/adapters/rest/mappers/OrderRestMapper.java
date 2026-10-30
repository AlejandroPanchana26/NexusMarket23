package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.responses.InvoiceResponse;
import application.adapters.rest.dtos.responses.OrderLineResponse;
import application.adapters.rest.dtos.responses.OrderResponse;
import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.DigitalProduct;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.valueobjects.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public final class OrderRestMapper {

    private OrderRestMapper() {
    }

    // Para el comprador: los enlaces de descarga aparecen cuando el pedido ya está pagado.
    public static OrderResponse toBuyerResponse(Order order) {
        OrderStatus status = order.getStatus();
        boolean paid = OrderStatus.PAID.equals(status) || OrderStatus.SHIPPED.equals(status)
                || OrderStatus.DELIVERED.equals(status);
        return build(order, paid);
    }

    // Para vendedores y personal interno: no necesitan los enlaces de descarga.
    public static OrderResponse toResponse(Order order) {
        return build(order, false);
    }

    public static List<OrderResponse> toBuyerResponseList(List<Order> orders) {
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(toBuyerResponse(order));
        }
        return responses;
    }

    public static List<OrderResponse> toResponseList(List<Order> orders) {
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(toResponse(order));
        }
        return responses;
    }

    public static InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getIdentifier(),
                invoice.getOrder().getIdentifier(),
                invoice.getIssueDate(),
                invoice.getTotalAmount(),
                CatalogMapper.toCode(invoice.getStatus()));
    }

    private static OrderResponse build(Order order, boolean includeDownloads) {
        List<OrderLineResponse> lines = new ArrayList<>();
        for (OrderDetail detail : order.getDetails()) {
            String downloadUrl = null;
            if (includeDownloads && detail.getProduct() instanceof DigitalProduct digital) {
                downloadUrl = digital.getDownloadUrl();
            }
            lines.add(new OrderLineResponse(
                    detail.getProduct().getIdentifier(),
                    detail.getProduct().getName(),
                    detail.getProduct().getType().name(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getSubtotal(),
                    downloadUrl));
        }
        String warehouseName = order.getFulfillmentWarehouse() == null ? null : order.getFulfillmentWarehouse().getName();
        return new OrderResponse(
                order.getIdentifier(),
                CatalogMapper.toCode(order.getStatus()),
                order.getCreationDate(),
                order.getTotal(),
                order.getBuyer().getIdentification(),
                warehouseName,
                lines);
    }

    // Convierte el estado que llega como texto en la dirección; si no existe, responde 400.
    public static OrderStatus toOrderStatus(String code) {
        OrderStatus status = CatalogMapper.orderStatus(code);
        if (status == null) {
            throw new BusinessRuleViolationException("Invalid order status: " + code + ".");
        }
        return status;
    }
}
