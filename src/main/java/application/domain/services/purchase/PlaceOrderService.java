package application.domain.services.purchase;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.CartItem;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.OrderDetail;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryMovementRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.InvoiceRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Convierte el carrito en un pedido: reserva existencias, emite la factura y vacía el carrito.
@Service
public class PlaceOrderService {

    private final ShoppingCartRepositoryPort cartRepository;
    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final InvoiceRepositoryPort invoiceRepository;

    public PlaceOrderService(ShoppingCartRepositoryPort cartRepository,
                             OrderRepositoryPort orderRepository,
                             InventoryRepositoryPort inventoryRepository,
                             InventoryMovementRepositoryPort movementRepository,
                             InvoiceRepositoryPort invoiceRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Order placeOrder(User buyerUser) {
        AccessValidator.requireRole(buyerUser, SystemRole.BUYER);
        Buyer buyer = (Buyer) buyerUser.getPerson();

        Optional<ShoppingCart> foundCart = cartRepository.findByBuyerIdentification(buyer.getIdentification());
        if (foundCart.isEmpty()) {
            throw new EntityNotFoundException("Shopping cart");
        }
        ShoppingCart cart = foundCart.get();

        // Un producto pudo suspenderse después de agregarlo al carrito.
        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().isAvailableForSale()) {
                throw new BusinessRuleViolationException("Product " + item.getProduct().getName() + " is no longer available.");
            }
        }

        Order order = Order.fromCart(cart);
        order.setIdentifier(UUID.randomUUID().toString());

        if (order.containsPhysicalProducts()) {
            Warehouse warehouse = findWarehouseWithStock(order);
            reserveStock(order, warehouse, buyerUser);
            order.setFulfillmentWarehouse(warehouse);
        }
        orderRepository.save(order);

        Invoice invoice = new Invoice();
        invoice.setIdentifier(UUID.randomUUID().toString());
        invoice.issueFor(order);
        invoiceRepository.save(invoice);

        cart.clear();
        cartRepository.save(cart);
        return order;
    }

    // Busca la primera bodega que tenga unidades suficientes de todos los productos físicos del pedido.
    private Warehouse findWarehouseWithStock(Order order) {
        OrderDetail firstPhysical = null;
        for (OrderDetail detail : order.getDetails()) {
            if (detail.getProduct().requiresInventory()) {
                firstPhysical = detail;
                break;
            }
        }
        List<Inventory> candidates = inventoryRepository.findByProductIdentifier(firstPhysical.getProduct().getIdentifier());
        for (Inventory candidate : candidates) {
            if (warehouseCoversOrder(order, candidate.getWarehouse())) {
                return candidate.getWarehouse();
            }
        }
        throw new BusinessRuleViolationException("There is not enough stock in a single warehouse for this order.");
    }

    private boolean warehouseCoversOrder(Order order, Warehouse warehouse) {
        for (OrderDetail detail : order.getDetails()) {
            if (!detail.getProduct().requiresInventory()) {
                continue;
            }
            Optional<Inventory> inventory = inventoryRepository.findByProductAndWarehouse(
                    detail.getProduct().getIdentifier(), warehouse.getIdentifier());
            if (inventory.isEmpty() || !inventory.get().hasAvailable(detail.getQuantity())) {
                return false;
            }
        }
        return true;
    }

    private void reserveStock(Order order, Warehouse warehouse, User buyerUser) {
        for (OrderDetail detail : order.getDetails()) {
            if (!detail.getProduct().requiresInventory()) {
                continue;
            }
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(
                    detail.getProduct().getIdentifier(), warehouse.getIdentifier()).get();
            inventory.reserve(detail.getQuantity());
            inventoryRepository.save(inventory);

            InventoryMovement movement = new InventoryMovement(inventory, InventoryMovementType.RESERVATION,
                    detail.getQuantity(), buyerUser);
            movement.setIdentifier(UUID.randomUUID().toString());
            movementRepository.save(movement);
        }
    }
}
