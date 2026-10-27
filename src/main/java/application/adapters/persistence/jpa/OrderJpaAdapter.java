package application.adapters.persistence.jpa;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.OrderLineEmbeddable;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaOrderRepository;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Person;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderJpaAdapter implements OrderRepositoryPort {

    private final SpringDataJpaOrderRepository orderRepository;
    private final PersonRepositoryPort personRepository;
    private final ProductRepositoryPort productRepository;
    private final WarehouseRepositoryPort warehouseRepository;

    public OrderJpaAdapter(SpringDataJpaOrderRepository orderRepository,
                           PersonRepositoryPort personRepository,
                           ProductRepositoryPort productRepository,
                           WarehouseRepositoryPort warehouseRepository) {
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Order save(Order order) {
        orderRepository.save(OrderJpaMapper.toEntity(order));
        return order;
    }

    @Override
    public Optional<Order> findByIdentifier(String identifier) {
        Optional<OrderJpaEntity> found = orderRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Order> findByBuyerIdentification(String buyerIdentification) {
        return toDomainList(orderRepository.findByBuyerIdentification(buyerIdentification));
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return toDomainList(orderRepository.findByStatus(CatalogMapper.toCode(status)));
    }

    @Override
    public List<Order> findAll() {
        return toDomainList(orderRepository.findAll());
    }

    private List<Order> toDomainList(List<OrderJpaEntity> entities) {
        List<Order> orders = new ArrayList<>();
        for (OrderJpaEntity entity : entities) {
            orders.add(toDomain(entity));
        }
        return orders;
    }

    // Reconstruye el pedido con su comprador, su bodega y cada línea con su producto.
    private Order toDomain(OrderJpaEntity entity) {
        Buyer buyer = null;
        Optional<Person> person = personRepository.findByIdentification(entity.getBuyerIdentification());
        if (person.isPresent() && person.get() instanceof Buyer found) {
            buyer = found;
        }
        Warehouse warehouse = null;
        if (entity.getFulfillmentWarehouseIdentifier() != null) {
            warehouse = warehouseRepository.findByIdentifier(entity.getFulfillmentWarehouseIdentifier()).orElse(null);
        }
        Order order = OrderJpaMapper.toDomain(entity, buyer, warehouse);
        for (OrderLineEmbeddable line : entity.getDetails()) {
            Optional<Product> product = productRepository.findByIdentifier(line.getProductIdentifier());
            if (product.isPresent()) {
                order.getDetails().add(OrderJpaMapper.toDomainDetail(line, product.get()));
            }
        }
        return order;
    }
}
