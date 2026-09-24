package application.domain.services.identity;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.PasswordEncoderPort;
import application.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public LoginService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Se usa el mismo mensaje si el usuario no existe o si la contraseña está mal,
    // para no revelar qué nombres de usuario existen.
    public User login(String username, String password) {
        if (username == null || password == null) {
            throw new UnauthorizedOperationException("Invalid username or password.");
        }
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isEmpty() || !passwordEncoder.matches(password, found.get().getPassword())) {
            throw new UnauthorizedOperationException("Invalid username or password.");
        }
        User user = found.get();
        if (!user.isActive()) {
            throw new UnauthorizedOperationException("User is not active.");
        }
        return user;
    }
}
