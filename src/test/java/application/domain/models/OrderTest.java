package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.OrderStatus;
import application.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Ciclo de vida del pedido según el tipo de productos que contiene.
class OrderTest {

    private Buyer buyer;
    private PhysicalProduct shirt;
    private DigitalProduct ebook;

    @BeforeEach
    void setUp() {
        Seller seller = TestData.activeSeller("200", "Tecno");
        buyer = TestData.activeBuyer("400", "Ana");
        shirt = TestData.publishedPhysicalProduct("P1", "50000", seller);
        ebook = TestData.publishedDigitalProduct("P2", "20000", seller);
    }

    private Order orderWith(Product product, int quantity) {
        ShoppingCart cart = new ShoppingCart();
        cart.setBuyer(buyer);
        cart.addItem(product, quantity);
        return Order.fromCart(cart);
    }

    @Test
    void orderKeepsThePriceOfTheCartEvenIfTheProductPriceChanges() {
        ShoppingCart cart = new ShoppingCart();
        cart.setBuyer(buyer);
        cart.addItem(shirt, 2);
        shirt.changePrice(new BigDecimal("99000"));

        Order order = Order.fromCart(cart);

        assertEquals(0, new BigDecimal("100000").compareTo(order.getTotal()));
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
    }

    @Test
    void cannotCreateOrderFromEmptyCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setBuyer(buyer);

        assertThrows(BusinessRuleViolationException.class, () -> Order.fromCart(cart));
    }

    @Test
    void digitalOrderIsDeliveredRightAfterPayment() {
        Order order = orderWith(ebook, 1);
        order.markAsPaid();
        order.markAsDelivered();

        assertTrue(order.isFinalized());
    }

    @Test
    void physicalOrderMustBeShippedBeforeDelivery() {
        Order order = orderWith(shirt, 1);
        order.markAsPaid();

        assertThrows(InvalidStatusTransitionException.class, order::markAsDelivered);

        order.markAsShipped();
        order.markAsDelivered();
        assertTrue(order.isFinalized());
    }

    @Test
    void orderCannotBePaidTwice() {
        Order order = orderWith(shirt, 1);
        order.markAsPaid();

        assertThrows(InvalidStatusTransitionException.class, order::markAsPaid);
    }

    @Test
    void deliveredOrderCannotChangeAnymore() {
        Order order = orderWith(ebook, 1);
        order.markAsPaid();
        order.markAsDelivered();

        assertThrows(InvalidStatusTransitionException.class, order::markAsPaid);
        assertThrows(InvalidStatusTransitionException.class, order::markAsShipped);
    }
}
