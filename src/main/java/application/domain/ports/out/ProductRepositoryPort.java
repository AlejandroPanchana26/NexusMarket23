package application.domain.ports.out;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findByIdentifier(String identifier);

    // Con PUBLISHED se obtiene el catálogo público.
    List<Product> findByStatus(ProductStatus status);

    List<Product> findBySellerIdentification(String sellerIdentification);
}
