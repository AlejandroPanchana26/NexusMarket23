package application.adapters.useCases;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.ports.in.BuyerPort;
import application.domain.services.postsale.ConsultReturnsService;
import application.domain.services.postsale.ManageReturnService;
import application.domain.services.purchase.ConsultOrdersService;
import application.domain.services.purchase.ManageCartService;
import application.domain.services.purchase.PayOrderService;
import application.domain.services.purchase.PlaceOrderService;
import application.domain.services.shipment.ConsultShipmentsService;
import org.springframework.stereotype.Component;

import java.util.List;

// Implementa BuyerPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class BuyerUseCase implements BuyerPort {

    private final ManageCartService manageCartService;
    private final PlaceOrderService placeOrderService;
    private final PayOrderService payOrderService;
    private final ConsultOrdersService consultOrdersService;
    private final ConsultShipmentsService consultShipmentsService;
    private final ManageReturnService manageReturnService;
    private final ConsultReturnsService consultReturnsService;

    public BuyerUseCase(ManageCartService manageCartService,
                        PlaceOrderService placeOrderService,
                        PayOrderService payOrderService,
                        ConsultOrdersService consultOrdersService,
                        ConsultShipmentsService consultShipmentsService,
                        ManageReturnService manageReturnService,
                        ConsultReturnsService consultReturnsService) {
        this.manageCartService = manageCartService;
        this.placeOrderService = placeOrderService;
        this.payOrderService = payOrderService;
        this.consultOrdersService = consultOrdersService;
        this.consultShipmentsService = consultShipmentsService;
        this.manageReturnService = manageReturnService;
        this.consultReturnsService = consultReturnsService;
    }

    @Override
    public ShoppingCart viewCart(User user) {
        return manageCartService.view(user);
    }

    @Override
    public ShoppingCart addToCart(User user, String productIdentifier, int quantity) {
        return manageCartService.addItem(user, productIdentifier, quantity);
    }

    @Override
    public ShoppingCart removeFromCart(User user, String productIdentifier) {
        return manageCartService.removeItem(user, productIdentifier);
    }

    @Override
    public ShoppingCart clearCart(User user) {
        return manageCartService.clear(user);
    }

    @Override
    public Order placeOrder(User user) {
        return placeOrderService.placeOrder(user);
    }

    @Override
    public Order payOrder(User user, String orderIdentifier) {
        return payOrderService.pay(user, orderIdentifier);
    }

    @Override
    public List<Order> listMyOrders(User user) {
        return consultOrdersService.listOwn(user);
    }

    @Override
    public Invoice getInvoice(User user, String orderIdentifier) {
        return consultOrdersService.getInvoice(user, orderIdentifier);
    }

    @Override
    public Shipment trackShipment(User user, String orderIdentifier) {
        return consultShipmentsService.findByOrder(user, orderIdentifier);
    }

    @Override
    public Return requestReturn(User user, String orderIdentifier, String reason) {
        return manageReturnService.request(user, orderIdentifier, reason);
    }

    @Override
    public List<Return> listReturns(User user, String orderIdentifier) {
        return consultReturnsService.listForOrder(user, orderIdentifier);
    }

    @Override
    public Refund getRefund(User user, String returnIdentifier) {
        return consultReturnsService.findRefund(user, returnIdentifier);
    }
}
