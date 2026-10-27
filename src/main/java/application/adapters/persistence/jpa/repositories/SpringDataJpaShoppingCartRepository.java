package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.ShoppingCartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaShoppingCartRepository extends JpaRepository<ShoppingCartJpaEntity, String> {

    Optional<ShoppingCartJpaEntity> findByBuyerIdentification(String buyerIdentification);
}
