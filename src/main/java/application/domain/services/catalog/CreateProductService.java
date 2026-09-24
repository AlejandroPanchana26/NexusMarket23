package application.domain.services.catalog;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.UUID;

// El producto se guarda sin estado; solo queda visible cuando el vendedor lo publica.
@Service
public class CreateProductService {

    private final ProductRepositoryPort productRepository;

    public CreateProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(User sellerUser, Product product) {
        AccessValidator.requireRole(sellerUser, SystemRole.SELLER);
        Seller seller = (Seller) sellerUser.getPerson();
        if (!seller.canSell()) {
            throw new UnauthorizedOperationException("Seller is not enabled to sell.");
        }
        if (product == null || product.getName() == null || product.getName().isBlank()) {
            throw new BusinessRuleViolationException("Product name is required.");
        }
        product.setIdentifier(UUID.randomUUID().toString());
        seller.addProduct(product);
        return productRepository.save(product);
    }
}
