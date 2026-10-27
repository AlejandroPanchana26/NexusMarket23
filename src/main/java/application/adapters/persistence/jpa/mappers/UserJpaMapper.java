package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.domain.models.Person;
import application.domain.models.User;

// Convierte entre User y la tabla users. La persona se guarda solo por su identificación.
public final class UserJpaMapper {

    private UserJpaMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setUserId(user.getUserId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setStatus(CatalogMapper.toCode(user.getStatus()));
        entity.setPersonIdentification(user.getPerson().getIdentification());
        return entity;
    }

    // La persona ya viene convertida desde el adaptador.
    public static User toDomain(UserJpaEntity entity, Person person) {
        User user = new User();
        user.setUserId(entity.getUserId());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setStatus(CatalogMapper.userStatus(entity.getStatus()));
        user.setPerson(person);
        return user;
    }
}
