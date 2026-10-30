package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.LogisticsOperatorPort;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.ManageStockService;
import application.domain.services.inventory.RegisterInventoryService;
import application.domain.services.postsale.ConsultReturnsService;
import application.domain.services.postsale.ManageReturnService;
import application.domain.services.purchase.ConsultOrdersService;
import application.domain.services.shipment.ConsultShipmentsService;
import application.domain.services.shipment.ShipOrderService;
import application.domain.services.warehouse.ConsultWarehousesService;
import application.domain.valueobjects.ReturnStatus;
import org.springframework.stereotype.Component;

import java.util.List;

// Implementa LogisticsOperatorPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class LogisticsOperatorUseCase implements LogisticsOperatorPort {

    private final ConsultWarehousesService consultWarehousesService;
    private final RegisterInventoryService registerInventoryService;
    private final ManageStockService manageStockService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final ShipOrderService shipOrderService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ConsultReturnsService consultReturnsService;
    private final ManageReturnService manageReturnService;

    public LogisticsOperatorUseCase(ConsultWarehousesService consultWarehousesService,
                                    RegisterInventoryService registerInventoryService,
                                    ManageStockService manageStockService,
                                    ConsultInventoryService consultInventoryService,
                                    ConsultOrdersService consultOrdersService,
                                    ShipOrderService shipOrderService,
                                    ConsultShipmentsService consultShipmentsService,
                                    ConsultReturnsService consultReturnsService,
                                    ManageReturnService manageReturnService) {
        this.consultWarehousesService = consultWarehousesService;
        this.registerInventoryService = registerInventoryService;
        this.manageStockService = manageStockService;
        this.consultInventoryService = consultInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.shipOrderService = shipOrderService;
        this.consultShipmentsService = consultShipmentsService;
        this.consultReturnsService = consultReturnsService;
        this.manageReturnService = manageReturnService;
    }

    @Override
    public List<Warehouse> listWarehouses(User user) {
        return consultWarehousesService.listAll(user);
    }

    @Override
    public Inventory registerInventory(User user, String productIdentifier, String warehouseIdentifier, int initialQuantity) {
        return registerInventoryService.register(user, productIdentifier, warehouseIdentifier, initialQuantity);
    }

    @Override
    public Inventory addStock(User user, String inventoryIdentifier, int quantity) {
        return manageStockService.addStock(user, inventoryIdentifier, quantity);
    }

    @Override
    public Inventory adjustStock(User user, String inventoryIdentifier, int newAvailableQuantity) {
        return manageStockService.adjust(user, inventoryIdentifier, newAvailableQuantity);
    }

    @Override
    public Inventory markInventoryAsDamaged(User user, String inventoryIdentifier) {
        return manageStockService.markAsDamaged(user, inventoryIdentifier);
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
    public Shipment prepareShipment(User user, String orderIdentifier, String destinationAddress) {
        return shipOrderService.prepare(user, orderIdentifier, destinationAddress);
    }

    @Override
    public Shipment dispatchShipment(User user, String shipmentIdentifier) {
        return shipOrderService.dispatch(user, shipmentIdentifier);
    }

    @Override
    public Shipment markShipmentInTransit(User user, String shipmentIdentifier) {
        return shipOrderService.markInTransit(user, shipmentIdentifier);
    }

    @Override
    public Shipment confirmDelivery(User user, String shipmentIdentifier) {
        return shipOrderService.confirmDelivery(user, shipmentIdentifier);
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
    public Return completeReturn(User user, String returnIdentifier) {
        return manageReturnService.complete(user, returnIdentifier);
    }
}
