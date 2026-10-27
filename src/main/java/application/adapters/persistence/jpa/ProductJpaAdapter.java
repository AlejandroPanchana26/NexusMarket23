package application.adapters.persistence.jpa;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaProductRepository;
import application.domain.models.Person;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.valueobjects.ProductStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductJpaAdapter implements ProductRepositoryPort {

    private final SpringDataJpaProductRepository productRepository;
    private final PersonRepositoryPort personRepository;

    public ProductJpaAdapter(SpringDataJpaProductRepository productRepository,
                             PersonRepositoryPort personRepository) {
        this.productRepository = productRepository;
        this.personRepository = personRepository;
    }

    @Override
    public Product save(Product product) {
        productRepository.save(ProductJpaMapper.toEntity(product));
        return product;
    }

    @Override
    public Optional<Product> findByIdentifier(String identifier) {
        Optional<ProductJpaEntity> found = productRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Product> findByStatus(ProductStatus status) {
        return toDomainList(productRepository.findByStatus(CatalogMapper.toCode(status)));
    }

    @Override
    public List<Product> findBySellerIdentification(String sellerIdentification) {
        return toDomainList(productRepository.findBySellerIdentification(sellerIdentification));
    }

    private List<Product> toDomainList(List<ProductJpaEntity> entities) {
        List<Product> products = new ArrayList<>();
        for (ProductJpaEntity entity : entities) {
            products.add(toDomain(entity));
        }
        return products;
    }

    // Busca el vendedor del producto para devolverlo completo.
    private Product toDomain(ProductJpaEntity entity) {
        Seller seller = null;
        Optional<Person> person = personRepository.findByIdentification(entity.getSellerIdentification());
        if (person.isPresent() && person.get() instanceof Seller found) {
            seller = found;
        }
        return ProductJpaMapper.toDomain(entity, seller);
    }
}
