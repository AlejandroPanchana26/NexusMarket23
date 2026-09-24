package application.domain.services.identity;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.util.Optional;

// El administrador controla el acceso de los usuarios al sistema.
@Service
public class ManageUserStatusService {

    private final UserRepositoryPort userRepository;

    public ManageUserStatusService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User block(User administrator, String username) {
        User user = findUser(administrator, username);
        user.block();
        return userRepository.save(user);
    }

    public User deactivate(User administrator, String username) {
        User user = findUser(administrator, username);
        user.deactivate();
        return userRepository.save(user);
    }

    public User activate(User administrator, String username) {
        User user = findUser(administrator, username);
        user.activate();
        return userRepository.save(user);
    }

    private User findUser(User administrator, String username) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("User");
        }
        return found.get();
    }
}
