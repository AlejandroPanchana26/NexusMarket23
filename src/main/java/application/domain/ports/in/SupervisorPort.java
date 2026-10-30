package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;

import java.math.BigDecimal;
import java.util.List;

// Operaciones del supervisor: solo consultas y reportes.
public interface SupervisorPort {

    List<User> listUsers(User user);

    List<Warehouse> listWarehouses(User user);

    List<Inventory> listInventory(User user);

    List<Inventory> listProductInventory(User user, String productIdentifier);

    List<InventoryMovement> listInventoryMovements(User user, String inventoryIdentifier);

    List<Order> listOrders(User user);

    List<Order> listOrdersByStatus(User user, OrderStatus status);

    Invoice getInvoice(User user, String orderIdentifier);

    List<Shipment> listShipments(User user);

    Shipment findShipmentByOrder(User user, String orderIdentifier);

    List<Return> listReturnsByStatus(User user, ReturnStatus status);

    List<Return> listReturnsForOrder(User user, String orderIdentifier);

    Refund getRefund(User user, String returnIdentifier);

    int countOrdersByStatus(User user, OrderStatus status);

    BigDecimal calculateTotalSales(User user);

    List<Inventory> listDamagedInventory(User user);
}
