package application.domain.services.identity;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.List;

// Objetivo OBJ-01 del PDF: el administrador consulta los usuarios para poder administrarlos.
@Service
public class ConsultUsersService {

    private final UserRepositoryPort userRepository;

    public ConsultUsersService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listAll(User user) {
        AccessValidator.requireRole(user, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR);
        return userRepository.findAll();
    }
}
