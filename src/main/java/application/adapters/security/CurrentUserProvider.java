package application.adapters.security;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

// Entrega a los controladores el usuario que hace la petición.
// TEMPORAL: lo toma del encabezado X-Username. En el paso F7 lo tomará del token JWT,
// y como los controladores solo llaman a getCurrentUser(), no habrá que modificarlos.
@Component
public class CurrentUserProvider {

    private final UserRepositoryPort userRepository;

    public CurrentUserProvider(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String username = request.getHeader("X-Username");
        if (username == null || username.isBlank()) {
            throw new UnauthorizedOperationException("Authentication is required.");
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UnauthorizedOperationException("Authentication is required.");
        }
        return user.get();
    }
}
