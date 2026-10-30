package application.domain.ports.in;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.ShoppingCart;
import application.domain.models.User;

import java.util.List;

// Operaciones del comprador.
public interface BuyerPort {

    ShoppingCart viewCart(User user);

    ShoppingCart addToCart(User user, String productIdentifier, int quantity);

    ShoppingCart removeFromCart(User user, String productIdentifier);

    ShoppingCart clearCart(User user);

    Order placeOrder(User user);

    Order payOrder(User user, String orderIdentifier);

    List<Order> listMyOrders(User user);

    Invoice getInvoice(User user, String orderIdentifier);

    Shipment trackShipment(User user, String orderIdentifier);

    Return requestReturn(User user, String orderIdentifier, String reason);

    List<Return> listReturns(User user, String orderIdentifier);

    Refund getRefund(User user, String returnIdentifier);
}
