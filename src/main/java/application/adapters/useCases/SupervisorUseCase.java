package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SupervisorPort;
import application.domain.services.identity.ConsultUsersService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.postsale.ConsultReturnsService;
import application.domain.services.purchase.ConsultOrdersService;
import application.domain.services.report.SupervisorReportService;
import application.domain.services.shipment.ConsultShipmentsService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// Implementa SupervisorPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class SupervisorUseCase implements SupervisorPort {

    private final ConsultUsersService consultUsersService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final SupervisorReportService supervisorReportService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultReturnsService consultReturnsService;

    public SupervisorUseCase(ConsultUsersService consultUsersService,
                             ConsultWarehousesService consultWarehousesService,
                             ConsultInventoryService consultInventoryService,
                             ConsultOrdersService consultOrdersService,
                             SupervisorReportService supervisorReportService,
                             ConsultShipmentsService consultShipmentsService,
                             ConsultReturnsService consultReturnsService) {
        this.consultUsersService = consultUsersService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultInventoryService = consultInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.supervisorReportService = supervisorReportService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultReturnsService = consultReturnsService;
    }

    @Override
    public List<User> listUsers(User user) {
        return consultUsersService.listAll(user);
    }

    @Override
    public List<Warehouse> listWarehouses(User user) {
        return consultWarehousesService.listAll(user);
    }

    @Override
    public List<Inventory> listInventory(User user) {
        return consultInventoryService.listAll(user);
    }

    @Override
    public List<Inventory> listProductInventory(User user, String productIdentifier) {
        return consultInventoryService.listByProduct(user, productIdentifier);
    }

    @Override
    public List<InventoryMovement> listInventoryMovements(User user, String inventoryIdentifier) {
        return consultInventoryService.listMovements(user, inventoryIdentifier);
    }

    @Override
    public List<Order> listOrders(User user) {
        return consultOrdersService.listAll(user);
    }

    @Override
    public List<Order> listOrdersByStatus(User user, OrderStatus status) {
        return supervisorReportService.listOrdersByStatus(user, status);
    }

    @Override
    public Invoice getInvoice(User user, String orderIdentifier) {
        return consultOrdersService.getInvoice(user, orderIdentifier);
    }

    @Override
    public List<Shipment> listShipments(User user) {
        return consultShipmentsService.listAll(user);
    }

    @Override
    public Shipment findShipmentByOrder(User user, String orderIdentifier) {
        return consultShipmentsService.findByOrder(user, orderIdentifier);
    }

    @Override
    public List<Return> listReturnsByStatus(User user, ReturnStatus status) {
        return consultReturnsService.listByStatus(user, status);
    }

    @Override
    public List<Return> listReturnsForOrder(User user, String orderIdentifier) {
        return consultReturnsService.listForOrder(user, orderIdentifier);
    }

    @Override
    public Refund getRefund(User user, String returnIdentifier) {
        return consultReturnsService.findRefund(user, returnIdentifier);
    }

    @Override
    public int countOrdersByStatus(User user, OrderStatus status) {
        return supervisorReportService.countOrdersByStatus(user, status);
    }

    @Override
    public BigDecimal calculateTotalSales(User user) {
        return supervisorReportService.calculateTotalSales(user);
    }

    @Override
    public List<Inventory> listDamagedInventory(User user) {
        return supervisorReportService.listDamagedInventory(user);
    }
}
