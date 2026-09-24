package application.domain.ports.out;

import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findByIdentifier(String identifier);

    List<Order> findByBuyerIdentification(String buyerIdentification);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findAll();
}
