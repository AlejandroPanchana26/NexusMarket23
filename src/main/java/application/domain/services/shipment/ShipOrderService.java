package application.domain.services.shipment;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShipmentRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// Operación logística del pedido: preparar, despachar, transportar y entregar.
@Service
public class ShipOrderService {

    private final ShipmentRepositoryPort shipmentRepository;
    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;

    public ShipOrderService(ShipmentRepositoryPort shipmentRepository,
                            OrderRepositoryPort orderRepository,
                            InventoryRepositoryPort inventoryRepository,
                            InventoryMovementRepositoryPort movementRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
    }

    // Si no se indica dirección, se usa la dirección principal del comprador.
    public Shipment prepare(User operator, String orderIdentifier, String destinationAddress) {
        AccessValidator.requireRole(operator, SystemRole.LOGISTICS_OPERATOR);
        Optional<Order> foundOrder = orderRepository.findByIdentifier(orderIdentifier);
        if (foundOrder.isEmpty()) {
            throw new EntityNotFoundException("Order");
        }
        Order order = foundOrder.get();
        if (shipmentRepository.findByOrderIdentifier(orderIdentifier).isPresent()) {
            throw new DuplicateEntityException("This order already has a shipment.");
        }

        Buyer buyer = order.getBuyer();
        String address = destinationAddress;
        if (address == null || address.isBlank()) {
            address = buyer.getPrincipalAddress();
        } else if (!address.equals(buyer.getPrincipalAddress()) && !buyer.getAdditionalAddresses().contains(address)) {
            throw new BusinessRuleViolationException("Address is not registered for this buyer.");
        }

        Shipment shipment = new Shipment();
        shipment.setIdentifier(UUID.randomUUID().toString());
        shipment.prepareFor(order, order.getFulfillmentWarehouse(), address, operator);
        return shipmentRepository.save(shipment);
    }

    // Al despachar, las unidades reservadas salen de la bodega.
    public Shipment dispatch(User operator, String shipmentIdentifier) {
        Shipment shipment = findShipment(operator, shipmentIdentifier);
        Order order = shipment.getOrder();
        shipment.dispatch();
        order.markAsShipped();

        for (OrderDetail detail : order.getDetails()) {
            if (!detail.getProduct().requiresInventory()) {
                continue;
            }
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(
                    detail.getProduct().getIdentifier(), shipment.getOriginWarehouse().getIdentifier()).get();
            inventory.confirmSale(detail.getQuantity());
            inventoryRepository.save(inventory);

            InventoryMovement movement = new InventoryMovement(inventory, InventoryMovementType.SALE_EXIT,
                    detail.getQuantity(), operator);
            movement.setIdentifier(UUID.randomUUID().toString());
            movementRepository.save(movement);
        }
        orderRepository.save(order);
        return shipmentRepository.save(shipment);
    }

    public Shipment markInTransit(User operator, String shipmentIdentifier) {
        Shipment shipment = findShipment(operator, shipmentIdentifier);
        shipment.markInTransit();
        return shipmentRepository.save(shipment);
    }

    // Regla del PDF: el pedido se finaliza tras la entrega confirmada.
    public Shipment confirmDelivery(User operator, String shipmentIdentifier) {
        Shipment shipment = findShipment(operator, shipmentIdentifier);
        shipment.markAsDelivered();
        shipment.getOrder().markAsDelivered();
        orderRepository.save(shipment.getOrder());
        return shipmentRepository.save(shipment);
    }

    private Shipment findShipment(User operator, String shipmentIdentifier) {
        AccessValidator.requireRole(operator, SystemRole.LOGISTICS_OPERATOR);
        Optional<Shipment> found = shipmentRepository.findByIdentifier(shipmentIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Shipment");
        }
        return found.get();
    }
}
