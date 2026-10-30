package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ReturnStatus;

import java.util.List;

// Operaciones del operador logístico: inventario, envíos y recepción de devoluciones.
public interface LogisticsOperatorPort {

    List<Warehouse> listWarehouses(User user);

    Inventory registerInventory(User user, String productIdentifier, String warehouseIdentifier, int initialQuantity);

    Inventory addStock(User user, String inventoryIdentifier, int quantity);

    Inventory adjustStock(User user, String inventoryIdentifier, int newAvailableQuantity);

    Inventory markInventoryAsDamaged(User user, String inventoryIdentifier);

    List<Inventory> listInventory(User user);

    List<Inventory> listProductInventory(User user, String productIdentifier);

    List<InventoryMovement> listInventoryMovements(User user, String inventoryIdentifier);

    List<Order> listOrders(User user);

    Shipment prepareShipment(User user, String orderIdentifier, String destinationAddress);

    Shipment dispatchShipment(User user, String shipmentIdentifier);

    Shipment markShipmentInTransit(User user, String shipmentIdentifier);

    Shipment confirmDelivery(User user, String shipmentIdentifier);

    List<Shipment> listShipments(User user);

    Shipment findShipmentByOrder(User user, String orderIdentifier);

    List<Return> listReturnsByStatus(User user, ReturnStatus status);

    Return completeReturn(User user, String returnIdentifier);
}
