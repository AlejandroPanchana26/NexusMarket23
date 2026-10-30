package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Employee;
import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;

import java.math.BigDecimal;
import java.util.List;

// Operaciones del administrador.
public interface AdministratorPort {

    User registerSeller(User user, Seller seller, Warehouse firstWarehouse, String username, String password);

    User registerEmployee(User user, Employee employee, SystemRole role, String username, String password);

    List<User> listUsers(User user);

    User blockUser(User user, String username);

    User deactivateUser(User user, String username);

    User activateUser(User user, String username);

    Seller suspendSeller(User user, String sellerIdentification);

    Seller activateSeller(User user, String sellerIdentification);

    Seller deactivateSeller(User user, String sellerIdentification);

    Buyer suspendBuyer(User user, String buyerIdentification);

    Buyer activateBuyer(User user, String buyerIdentification);

    Buyer blockBuyer(User user, String buyerIdentification);

    Warehouse registerWarehouse(User user, Warehouse warehouse, String sellerIdentification);

    List<Warehouse> listWarehouses(User user);

    List<Inventory> listInventory(User user);

    List<Order> listOrders(User user);

    Invoice getInvoice(User user, String orderIdentifier);

    List<Shipment> listShipments(User user);

    List<Return> listReturnsByStatus(User user, ReturnStatus status);

    Return approveReturn(User user, String returnIdentifier);

    Return rejectReturn(User user, String returnIdentifier);

    Return completeReturn(User user, String returnIdentifier);

    Refund createRefund(User user, String returnIdentifier, BigDecimal amount);

    Refund processRefund(User user, String refundIdentifier);

    Refund rejectRefund(User user, String refundIdentifier);

    int countOrdersByStatus(User user, OrderStatus status);

    BigDecimal calculateTotalSales(User user);

    List<Inventory> listDamagedInventory(User user);
}
