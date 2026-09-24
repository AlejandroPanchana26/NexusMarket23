package application.domain.services.catalog;

import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultCatalogService {

    private final ProductRepositoryPort productRepository;

    public ConsultCatalogService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    // Catálogo público: solo productos publicados.
    public List<Product> listPublished() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }

    // El vendedor ve todos sus productos, incluidos los no publicados.
    public List<Product> listOwn(User sellerUser) {
        AccessValidator.requireRole(sellerUser, SystemRole.SELLER);
        return productRepository.findBySellerIdentification(sellerUser.getPerson().getIdentification());
    }
}
