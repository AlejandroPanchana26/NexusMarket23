package application.adapters.useCases;

import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.in.SellerPort;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.ManageProductService;
import application.domain.services.inventory.ConsultInventoryService;
import application.domain.services.inventory.ManageStockService;
import application.domain.services.inventory.RegisterInventoryService;
import application.domain.services.postsale.ManageRefundService;
import application.domain.services.postsale.ManageReturnService;
import application.domain.services.purchase.ConsultOrdersService;
import application.domain.services.warehouse.ConsultWarehousesService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// Implementa SellerPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class SellerUseCase implements SellerPort {

    private final CreateProductService createProductService;
    private final ManageProductService manageProductService;
    private final ConsultCatalogService consultCatalogService;
    private final ConsultWarehousesService consultWarehousesService;
    private final RegisterInventoryService registerInventoryService;
    private final ManageStockService manageStockService;
    private final ConsultInventoryService consultInventoryService;
    private final ConsultOrdersService consultOrdersService;
    private final ManageReturnService manageReturnService;
    private final ManageRefundService manageRefundService;

    public SellerUseCase(CreateProductService createProductService,
                         ManageProductService manageProductService,
                         ConsultCatalogService consultCatalogService,
                         ConsultWarehousesService consultWarehousesService,
                         RegisterInventoryService registerInventoryService,
                         ManageStockService manageStockService,
                         ConsultInventoryService consultInventoryService,
                         ConsultOrdersService consultOrdersService,
                         ManageReturnService manageReturnService,
                         ManageRefundService manageRefundService) {
        this.createProductService = createProductService;
        this.manageProductService = manageProductService;
        this.consultCatalogService = consultCatalogService;
        this.consultWarehousesService = consultWarehousesService;
        this.registerInventoryService = registerInventoryService;
        this.manageStockService = manageStockService;
        this.consultInventoryService = consultInventoryService;
        this.consultOrdersService = consultOrdersService;
        this.manageReturnService = manageReturnService;
        this.manageRefundService = manageRefundService;
    }

    @Override
    public Product createProduct(User user, Product product) {
        return createProductService.create(user, product);
    }

    @Override
    public Product publishProduct(User user, String productIdentifier) {
        return manageProductService.publish(user, productIdentifier);
    }

    @Override
    public Product suspendProduct(User user, String productIdentifier) {
        return manageProductService.suspend(user, productIdentifier);
    }

    @Override
    public Product discontinueProduct(User user, String productIdentifier) {
        return manageProductService.discontinue(user, productIdentifier);
    }

    @Override
    public Product changeProductPrice(User user, String productIdentifier, BigDecimal newPrice) {
        return manageProductService.changePrice(user, productIdentifier, newPrice);
    }

    @Override
    public Product addProductVariant(User user, String productIdentifier, String variant) {
        return manageProductService.addVariant(user, productIdentifier, variant);
    }

    @Override
    public List<Product> listMyProducts(User user) {
        return consultCatalogService.listOwn(user);
    }

    @Override
    public List<Warehouse> listMyWarehouses(User user) {
        return consultWarehousesService.listOwn(user);
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
    public List<Inventory> listProductInventory(User user, String productIdentifier) {
        return consultInventoryService.listByProduct(user, productIdentifier);
    }

    @Override
    public List<InventoryMovement> listInventoryMovements(User user, String inventoryIdentifier) {
        return consultInventoryService.listMovements(user, inventoryIdentifier);
    }

    @Override
    public List<Order> listMyOrders(User user) {
        return consultOrdersService.listForSeller(user);
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
    public Refund createRefund(User user, String returnIdentifier, BigDecimal amount) {
        return manageRefundService.create(user, returnIdentifier, amount);
    }
}
