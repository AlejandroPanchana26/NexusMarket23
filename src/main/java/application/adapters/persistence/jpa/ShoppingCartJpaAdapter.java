package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.CartLineEmbeddable;
import application.adapters.persistence.jpa.entities.ShoppingCartJpaEntity;
import application.adapters.persistence.jpa.mappers.ShoppingCartJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaShoppingCartRepository;
import application.domain.models.Buyer;
import application.domain.models.Person;
import application.domain.models.Product;
import application.domain.models.ShoppingCart;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.ShoppingCartRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ShoppingCartJpaAdapter implements ShoppingCartRepositoryPort {

    private final SpringDataJpaShoppingCartRepository cartRepository;
    private final PersonRepositoryPort personRepository;
    private final ProductRepositoryPort productRepository;

    public ShoppingCartJpaAdapter(SpringDataJpaShoppingCartRepository cartRepository,
                                  PersonRepositoryPort personRepository,
                                  ProductRepositoryPort productRepository) {
        this.cartRepository = cartRepository;
        this.personRepository = personRepository;
        this.productRepository = productRepository;
    }

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        cartRepository.save(ShoppingCartJpaMapper.toEntity(cart));
        return cart;
    }

    @Override
    public Optional<ShoppingCart> findByBuyerIdentification(String buyerIdentification) {
        Optional<ShoppingCartJpaEntity> found = cartRepository.findByBuyerIdentification(buyerIdentification);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    // Reconstruye el carrito con su comprador y cada línea con su producto.
    private ShoppingCart toDomain(ShoppingCartJpaEntity entity) {
        Buyer buyer = null;
        Optional<Person> person = personRepository.findByIdentification(entity.getBuyerIdentification());
        if (person.isPresent() && person.get() instanceof Buyer found) {
            buyer = found;
        }
        ShoppingCart cart = ShoppingCartJpaMapper.toDomain(entity, buyer);
        for (CartLineEmbeddable line : entity.getItems()) {
            Optional<Product> product = productRepository.findByIdentifier(line.getProductIdentifier());
            if (product.isPresent()) {
                cart.getItems().add(ShoppingCartJpaMapper.toDomainItem(line, product.get()));
            }
        }
        return cart;
    }
}
