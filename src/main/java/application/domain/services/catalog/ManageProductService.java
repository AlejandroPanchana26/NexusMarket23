package application.domain.services.catalog;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

// Acciones del vendedor sobre sus propios productos.
@Service
public class ManageProductService {

    private final ProductRepositoryPort productRepository;

    public ManageProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public Product publish(User sellerUser, String productIdentifier) {
        Product product = findOwnProduct(sellerUser, productIdentifier);
        Seller seller = (Seller) sellerUser.getPerson();
        if (!seller.canSell()) {
            throw new UnauthorizedOperationException("Seller is not enabled to sell.");
        }
        product.publish();
        return productRepository.save(product);
    }

    public Product suspend(User sellerUser, String productIdentifier) {
        Product product = findOwnProduct(sellerUser, productIdentifier);
        product.suspend();
        return productRepository.save(product);
    }

    public Product discontinue(User sellerUser, String productIdentifier) {
        Product product = findOwnProduct(sellerUser, productIdentifier);
        product.discontinue();
        return productRepository.save(product);
    }

    public Product changePrice(User sellerUser, String productIdentifier, BigDecimal newPrice) {
        Product product = findOwnProduct(sellerUser, productIdentifier);
        product.changePrice(newPrice);
        return productRepository.save(product);
    }

    public Product addVariant(User sellerUser, String productIdentifier, String variant) {
        Product product = findOwnProduct(sellerUser, productIdentifier);
        product.addVariant(variant);
        return productRepository.save(product);
    }

    // Regla RG-03: un vendedor no puede modificar productos de otro vendedor.
    private Product findOwnProduct(User sellerUser, String productIdentifier) {
        AccessValidator.requireRole(sellerUser, SystemRole.SELLER);
        Optional<Product> found = productRepository.findByIdentifier(productIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Product");
        }
        Product product = found.get();
        String ownerId = product.getSeller().getIdentification();
        if (!ownerId.equals(sellerUser.getPerson().getIdentification())) {
            throw new UnauthorizedOperationException("Sellers can only manage their own products.");
        }
        return product;
    }
}
