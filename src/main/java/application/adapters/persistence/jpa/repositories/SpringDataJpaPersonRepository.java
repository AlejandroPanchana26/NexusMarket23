package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.PersonJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// Spring crea la implementación; la consulta se arma a partir del nombre del método.
public interface SpringDataJpaPersonRepository extends JpaRepository<PersonJpaEntity, String> {

    boolean existsByEmailIgnoreCase(String email);
}
