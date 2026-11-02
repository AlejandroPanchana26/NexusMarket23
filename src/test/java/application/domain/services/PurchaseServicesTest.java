package application.domain.services;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.DigitalProduct;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.PhysicalProduct;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.identity.UserAccountService;
import application.domain.services.purchase.ManageCartService;
import application.domain.services.purchase.PayOrderService;
import application.domain.services.purchase.PlaceOrderService;
import application.domain.valueobjects.InvoiceStatus;
import application.domain.valueobjects.OrderStatus;
import application.support.InMemoryPorts;
import application.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Flujo de compra completo usando los servicios reales con puertos en memoria.
class PurchaseServicesTest {

    private InMemoryPorts.Orders orders;
    private InMemoryPorts.Invoices invoices;
    private ManageCartService cartService;
    private PlaceOrderService placeOrderService;
    private PayOrderService payOrderService;
    private Inventory inventory;
    private PhysicalProduct shirt;
    private DigitalProduct ebook;
    private User ana;
    private User luis;

    @BeforeEach
    void setUp() {
        InMemoryPorts.People people = new InMemoryPorts.People();
        InMemoryPorts.Users users = new InMemoryPorts.Users();
        InMemoryPorts.Products products = new InMemoryPorts.Products();
        InMemoryPorts.Inventories inventories = new InMemoryPorts.Inventories();
        InMemoryPorts.Carts carts = new InMemoryPorts.Carts();
        orders = new InMemoryPorts.Orders();
        invoices = new InMemoryPorts.Invoices();
        UserAccountService accounts = new UserAccountService(people, users, new InMemoryPorts.Encoder());

        cartService = new ManageCartService(carts, products);
        placeOrderService = new PlaceOrderService(carts, orders, inventories, new InMemoryPorts.Movements(), invoices);
        payOrderService = new PayOrderService(orders, invoices);

        Seller seller = TestData.activeSeller("200", "Tecno");
        Warehouse warehouse = TestData.sellerWarehouse("W1", seller);
        shirt = TestData.publishedPhysicalProduct("P1", "50000", seller);
        ebook = TestData.publishedDigitalProduct("P2", "20000", seller);
        products.save(shirt);
        products.save(ebook);
        inventory = TestData.inventoryWithStock("I1", shirt, warehouse, 10);
        inventories.save(inventory);

        Buyer anaPerson = TestData.activeBuyer("400", "Ana");
        Buyer luisPerson = TestData.activeBuyer("401", "Luis");
        people.save(anaPerson);
        people.save(luisPerson);
        ana = accounts.createUser(anaPerson, "ana", "secret");
        luis = accounts.createUser(luisPerson, "luis", "secret");
    }

    @Test
    void placingAnOrderReservesStockIssuesInvoiceAndEmptiesCart() {
        cartService.addItem(ana, "P1", 3);

        Order order = placeOrderService.placeOrder(ana);

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(7, inventory.getAvailableQuantity());
        assertEquals(3, inventory.getReservedQuantity());
        assertEquals(InvoiceStatus.ISSUED, invoices.findByOrderIdentifier(order.getIdentifier()).get().getStatus());
        assertTrue(cartService.view(ana).isEmpty());
    }

    @Test
    void orderIsNotCreatedWithoutEnoughStock() {
        cartService.addItem(ana, "P1", 50);

        assertThrows(BusinessRuleViolationException.class, () -> placeOrderService.placeOrder(ana));

        // No queda nada a medias: ni pedido, ni reserva, y el carrito sigue intacto.
        assertTrue(orders.data.isEmpty());
        assertEquals(0, inventory.getReservedQuantity());
        assertEquals(1, cartService.view(ana).getItems().size());
    }

    @Test
    void payingADigitalOnlyOrderDeliversIt() {
        cartService.addItem(ana, "P2", 1);
        Order order = placeOrderService.placeOrder(ana);

        payOrderService.pay(ana, order.getIdentifier());

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertEquals(InvoiceStatus.PAID, invoices.findByOrderIdentifier(order.getIdentifier()).get().getStatus());
    }

    @Test
    void buyerCannotPayAnotherBuyersOrder() {
        cartService.addItem(ana, "P1", 1);
        Order order = placeOrderService.placeOrder(ana);

        assertThrows(UnauthorizedOperationException.class, () -> payOrderService.pay(luis, order.getIdentifier()));
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }
}
