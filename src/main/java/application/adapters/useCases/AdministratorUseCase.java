package application.adapters.useCases;

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
import application.domain.ports.in.AdministratorPort;
import application.domain.services.identity.ConsultUsersService;
import application.domain.services.identity.ManageBuyerStatusService;
import application.domain.services.identity.ManageSellerStatusService;
import application.domain.services.identity.ManageUserStatusService;
import application.domain.services.identity.RegisterEmployeeService;
import application.domain.services.identity.RegisterSellerService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.postsale.ConsultReturnsService;
import application.domain.services.postsale.ManageRefundService;
import application.domain.services.postsale.ManageReturnService;
import application.domain.services.purchase.ConsultOrdersService;
import application.domain.services.report.SupervisorReportService;
import application.domain.services.shipment.ConsultShipmentsService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.services.warehouse.RegisterWarehouseService;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// Implementa AdministratorPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class AdministratorUseCase implements AdministratorPort {

    private final RegisterSellerService registerSellerService;
    private final RegisterEmployeeService registerEmployeeService;
    private final ConsultUsersService consultUsersService;
    private final ManageUserStatusService manageUserStatusService;
    private final ManageSellerStatusService manageSellerStatusService;
    private final ManageBuyerStatusService manageBuyerStatusService;
    private final RegisterWarehouseService registerWarehouseService;
    private final ConsultWarehousesService consultWarehousesService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultReturnsService consultReturnsService;
    private final ManageReturnService manageReturnService;
    private final ManageRefundService manageRefundService;
    private final SupervisorReportService supervisorReportService;

    public AdministratorUseCase(RegisterSellerService registerSellerService,
                                RegisterEmployeeService registerEmployeeService,
                                ConsultUsersService consultUsersService,
                                ManageUserStatusService manageUserStatusService,
                                ManageSellerStatusService manageSellerStatusService,
                                ManageBuyerStatusService manageBuyerStatusService,
                                RegisterWarehouseService registerWarehouseService,
                                ConsultWarehousesService consultWarehousesService,
                                ConsultInventoryService consultInventoryService,
                                ConsultOrdersService consultOrdersService,
                                ConsultShipmentsService consultShipmentsService,
                                ConsultReturnsService consultReturnsService,
                                ManageReturnService manageReturnService,
                                ManageRefundService manageRefundService,
                                SupervisorReportService supervisorReportService) {
        this.registerSellerService = registerSellerService;
        this.registerEmployeeService = registerEmployeeService;
        this.consultUsersService = consultUsersService;
        this.manageUserStatusService = manageUserStatusService;
        this.manageSellerStatusService = manageSellerStatusService;
        this.manageBuyerStatusService = manageBuyerStatusService;
        this.registerWarehouseService = registerWarehouseService;
        this.consultWarehousesService = consultWarehousesService;
        this.consultInventoryService = consultInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultReturnsService = consultReturnsService;
        this.manageReturnService = manageReturnService;
        this.manageRefundService = manageRefundService;
        this.supervisorReportService = supervisorReportService;
    }

    @Override
    public User registerSeller(User user, Seller seller, Warehouse firstWarehouse, String username, String password) {
        return registerSellerService.register(user, seller, firstWarehouse, username, password);
    }

    @Override
    public User registerEmployee(User user, Employee employee, SystemRole role, String username, String password) {
        return registerEmployeeService.register(user, employee, role, username, password);
    }

    @Override
    public List<User> listUsers(User user) {
        return consultUsersService.listAll(user);
    }

    @Override
    public User blockUser(User user, String username) {
        return manageUserStatusService.block(user, username);
    }

    @Override
    public User deactivateUser(User user, String username) {
        return manageUserStatusService.deactivate(user, username);
    }

    @Override
    public User activateUser(User user, String username) {
        return manageUserStatusService.activate(user, username);
    }

    @Override
    public Seller suspendSeller(User user, String sellerIdentification) {
        return manageSellerStatusService.suspend(user, sellerIdentification);
    }

    @Override
    public Seller activateSeller(User user, String sellerIdentification) {
        return manageSellerStatusService.activate(user, sellerIdentification);
    }

    @Override
    public Seller deactivateSeller(User user, String sellerIdentification) {
        return manageSellerStatusService.deactivate(user, sellerIdentification);
    }

    @Override
    public Buyer suspendBuyer(User user, String buyerIdentification) {
        return manageBuyerStatusService.suspend(user, buyerIdentification);
    }

    @Override
    public Buyer activateBuyer(User user, String buyerIdentification) {
        return manageBuyerStatusService.activate(user, buyerIdentification);
    }

    @Override
    public Buyer blockBuyer(User user, String buyerIdentification) {
        return manageBuyerStatusService.block(user, buyerIdentification);
    }

    @Override
    public Warehouse registerWarehouse(User user, Warehouse warehouse, String sellerIdentification) {
        return registerWarehouseService.register(user, warehouse, sellerIdentification);
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
    public List<Order> listOrders(User user) {
        return consultOrdersService.listAll(user);
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
    public List<Return> listReturnsByStatus(User user, ReturnStatus status) {
        return consultReturnsService.listByStatus(user, status);
    }

    @Override
    public Return approveReturn(User user, String returnIdentifier) {
        return manageReturnService.approve(user, returnIdentifier);
    }

    @Override
    public Return rejectReturn(User user, String returnIdentifier) {
        return manageReturnService.reject(user, returnIdentifier);
    }

    @Override
    public Return completeReturn(User user, String returnIdentifier) {
        return manageReturnService.complete(user, returnIdentifier);
    }

    @Override
    public Refund createRefund(User user, String returnIdentifier, BigDecimal amount) {
        return manageRefundService.create(user, returnIdentifier, amount);
    }

    @Override
    public Refund processRefund(User user, String refundIdentifier) {
        return manageRefundService.process(user, refundIdentifier);
    }

    @Override
    public Refund rejectRefund(User user, String refundIdentifier) {
        return manageRefundService.reject(user, refundIdentifier);
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
