package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.PersonJpaEntity;
import application.adapters.persistence.jpa.mappers.PersonJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaPersonRepository;
import application.domain.models.Person;
import application.domain.ports.out.PersonRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Implementa el puerto de personas guardando en MySQL.
@Repository
public class PersonJpaAdapter implements PersonRepositoryPort {

    private final SpringDataJpaPersonRepository personRepository;

    public PersonJpaAdapter(SpringDataJpaPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person save(Person person) {
        personRepository.save(PersonJpaMapper.toEntity(person));
        return person;
    }

    @Override
    public Optional<Person> findByIdentification(String identification) {
        Optional<PersonJpaEntity> found = personRepository.findById(identification);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(PersonJpaMapper.toDomain(found.get()));
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return personRepository.existsById(identification);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmailIgnoreCase(email);
    }
}
