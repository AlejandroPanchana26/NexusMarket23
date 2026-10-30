package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.models.Warehouse;

import java.math.BigDecimal;
import java.util.List;

// Operaciones del vendedor. Cada vendedor solo puede actuar sobre lo suyo.
public interface SellerPort {

    Product createProduct(User user, Product product);

    Product publishProduct(User user, String productIdentifier);

    Product suspendProduct(User user, String productIdentifier);

    Product discontinueProduct(User user, String productIdentifier);

    Product changeProductPrice(User user, String productIdentifier, BigDecimal newPrice);

    Product addProductVariant(User user, String productIdentifier, String variant);

    List<Product> listMyProducts(User user);

    List<Warehouse> listMyWarehouses(User user);

    Inventory registerInventory(User user, String productIdentifier, String warehouseIdentifier, int initialQuantity);

    Inventory addStock(User user, String inventoryIdentifier, int quantity);

    Inventory adjustStock(User user, String inventoryIdentifier, int newAvailableQuantity);

    Inventory markInventoryAsDamaged(User user, String inventoryIdentifier);

    List<Inventory> listProductInventory(User user, String productIdentifier);

    List<InventoryMovement> listInventoryMovements(User user, String inventoryIdentifier);

    List<Order> listMyOrders(User user);

    Return approveReturn(User user, String returnIdentifier);

    Return rejectReturn(User user, String returnIdentifier);

    Refund createRefund(User user, String returnIdentifier, BigDecimal amount);
}
