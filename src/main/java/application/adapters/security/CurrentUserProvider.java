package application.adapters.security;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

// Entrega a los controladores el usuario que hace la petición.
// El usuario lo dejó JwtAuthenticationFilter después de verificar el token.
@Component
public class CurrentUserProvider {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedOperationException("Authentication is required.");
        }
        return user;
    }
}
