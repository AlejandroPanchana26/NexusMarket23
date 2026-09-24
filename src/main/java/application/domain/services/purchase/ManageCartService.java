package application.domain.services.purchase;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.ShoppingCart;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ManageCartService {

    private final ShoppingCartRepositoryPort cartRepository;
    private final ProductRepositoryPort productRepository;

    public ManageCartService(ShoppingCartRepositoryPort cartRepository, ProductRepositoryPort productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public ShoppingCart addItem(User buyerUser, String productIdentifier, int quantity) {
        ShoppingCart cart = findOrCreateCart(buyerUser);
        cart.addItem(findProduct(productIdentifier), quantity);
        return cartRepository.save(cart);
    }

    public ShoppingCart removeItem(User buyerUser, String productIdentifier) {
        ShoppingCart cart = findOrCreateCart(buyerUser);
        cart.removeItem(findProduct(productIdentifier));
        return cartRepository.save(cart);
    }

    public ShoppingCart clear(User buyerUser) {
        ShoppingCart cart = findOrCreateCart(buyerUser);
        cart.clear();
        return cartRepository.save(cart);
    }

    public ShoppingCart view(User buyerUser) {
        return findOrCreateCart(buyerUser);
    }

    // Cada comprador tiene un solo carrito; si aún no existe, se crea vacío.
    private ShoppingCart findOrCreateCart(User buyerUser) {
        AccessValidator.requireRole(buyerUser, SystemRole.BUYER);
        Buyer buyer = (Buyer) buyerUser.getPerson();
        Optional<ShoppingCart> found = cartRepository.findByBuyerIdentification(buyer.getIdentification());
        if (found.isPresent()) {
            return found.get();
        }
        ShoppingCart cart = new ShoppingCart();
        cart.setIdentifier(UUID.randomUUID().toString());
        cart.setBuyer(buyer);
        return cart;
    }

    private Product findProduct(String productIdentifier) {
        Optional<Product> found = productRepository.findByIdentifier(productIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Product");
        }
        return found.get();
    }
}
