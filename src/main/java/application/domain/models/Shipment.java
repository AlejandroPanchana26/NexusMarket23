package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    private String identifier;

    // Pedido que se está enviando.
    private Order order;

    // Bodega desde donde sale el pedido.
    private Warehouse originWarehouse;

    // Dirección de entrega del comprador.
    private String destinationAddress;

    private ShipmentStatus status;

    // Operador logístico responsable del envío.
    private User logisticsOperator;

    private LocalDateTime shipmentDate;
    private LocalDateTime deliveryDate;

    // Solo se envían pedidos pagados que tengan al menos un producto físico.
    public void prepareFor(Order orderToShip, Warehouse origin, String address, User operator) {
        if (orderToShip == null || !OrderStatus.PAID.equals(orderToShip.getStatus())) {
            throw new BusinessRuleViolationException("Only paid orders can be shipped.");
        }
        if (!orderToShip.containsPhysicalProducts()) {
            throw new BusinessRuleViolationException("An order with only digital products is not shipped.");
        }
        if (origin == null || address == null || address.isBlank()) {
            throw new BusinessRuleViolationException("Shipment requires an origin warehouse and a destination address.");
        }
        if (operator == null || !operator.hasRole(SystemRole.LOGISTICS_OPERATOR)) {
            throw new BusinessRuleViolationException("Shipment must be handled by a logistics operator.");
        }
        this.order = orderToShip;
        this.originWarehouse = origin;
        this.destinationAddress = address;
        this.logisticsOperator = operator;
        this.status = ShipmentStatus.PREPARING;
    }

    public void dispatch() {
        requireStatus(ShipmentStatus.PREPARING, ShipmentStatus.SHIPPED);
        this.status = ShipmentStatus.SHIPPED;
        this.shipmentDate = LocalDateTime.now();
    }

    public void markInTransit() {
        requireStatus(ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT);
        this.status = ShipmentStatus.IN_TRANSIT;
    }

    public void markAsDelivered() {
        requireStatus(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED);
        this.status = ShipmentStatus.DELIVERED;
        this.deliveryDate = LocalDateTime.now();
    }

    private void requireStatus(ShipmentStatus expected, ShipmentStatus target) {
        if (!expected.equals(status)) {
            throw new InvalidStatusTransitionException("Shipment", status, target);
        }
    }
}
