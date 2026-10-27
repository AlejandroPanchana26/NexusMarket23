package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.PersonJpaEntity;
import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.PersonJpaMapper;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaPersonRepository;
import application.adapters.persistence.jpa.repositories.SpringDataJpaUserRepository;
import application.domain.models.Person;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementa el puerto de usuarios guardando en MySQL.
@Repository
public class UserJpaAdapter implements UserRepositoryPort {

    private final SpringDataJpaUserRepository userRepository;
    private final SpringDataJpaPersonRepository personRepository;

    public UserJpaAdapter(SpringDataJpaUserRepository userRepository,
                          SpringDataJpaPersonRepository personRepository) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    // Al guardar un usuario nuevo, MySQL le asigna su número; se copia al objeto del dominio.
    @Override
    public User save(User user) {
        UserJpaEntity saved = userRepository.save(UserJpaMapper.toEntity(user));
        user.setUserId(saved.getUserId());
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<UserJpaEntity> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (UserJpaEntity entity : userRepository.findAll()) {
            users.add(toDomain(entity));
        }
        return users;
    }

    // Busca la persona del usuario para devolverlo completo.
    private User toDomain(UserJpaEntity entity) {
        Person person = null;
        Optional<PersonJpaEntity> personEntity = personRepository.findById(entity.getPersonIdentification());
        if (personEntity.isPresent()) {
            person = PersonJpaMapper.toDomain(personEntity.get());
        }
        return UserJpaMapper.toDomain(entity, person);
    }
}
