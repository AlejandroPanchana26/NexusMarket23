package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaRefundRepository extends JpaRepository<RefundJpaEntity, String> {

    Optional<RefundJpaEntity> findByReturnIdentifier(String returnIdentifier);
}
