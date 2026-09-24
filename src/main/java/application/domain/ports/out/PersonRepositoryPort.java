package application.domain.ports.out;

import application.domain.models.Person;

import java.util.Optional;

// Compradores, vendedores y empleados comparten puerto porque su identificación y correo deben ser únicos en toda la plataforma.
public interface PersonRepositoryPort {

    Person save(Person person);

    Optional<Person> findByIdentification(String identification);

    boolean existsByIdentification(String identification);

    boolean existsByEmail(String email);
}
