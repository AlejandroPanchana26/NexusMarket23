package application.domain.services.report;

import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Consultas administrativas del objetivo OBJ-12 del PDF.
@Service
public class SupervisorReportService {

    private final OrderRepositoryPort orderRepository;
    private final InventoryRepositoryPort inventoryRepository;

    public SupervisorReportService(OrderRepositoryPort orderRepository, InventoryRepositoryPort inventoryRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public int countOrdersByStatus(User user, OrderStatus status) {
        AccessValidator.requireRole(user, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        return orderRepository.findByStatus(status).size();
    }

    public List<Order> listOrdersByStatus(User user, OrderStatus status) {
        AccessValidator.requireRole(user, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        return orderRepository.findByStatus(status);
    }

    // Solo cuentan como venta los pedidos que ya fueron pagados.
    public BigDecimal calculateTotalSales(User user) {
        AccessValidator.requireRole(user, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        BigDecimal total = BigDecimal.ZERO;
        for (Order order : orderRepository.findAll()) {
            OrderStatus status = order.getStatus();
            if (OrderStatus.PAID.equals(status) || OrderStatus.SHIPPED.equals(status) || OrderStatus.DELIVERED.equals(status)) {
                total = total.add(order.getTotal());
            }
        }
        return total;
    }

    public List<Inventory> listDamagedInventory(User user) {
        AccessValidator.requireRole(user, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
        List<Inventory> result = new ArrayList<>();
        for (Inventory inventory : inventoryRepository.findAll()) {
            if (inventory.isDamaged()) {
                result.add(inventory);
            }
        }
        return result;
    }
}
