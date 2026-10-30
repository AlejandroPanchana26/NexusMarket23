package application.adapters.security;

import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

// Se ejecuta en cada petición, antes del controlador. Si trae un token válido de un usuario activo,
// le dice a Spring Security quién es y cuál es su rol.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepositoryPort userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepositoryPort userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String username = jwtService.getUsername(header.substring(BEARER_PREFIX.length()));
            if (username != null) {
                // Se vuelve a buscar el usuario: si fue bloqueado, su token deja de servir de inmediato.
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent() && user.get().isActive()) {
                    String authority = "ROLE_" + user.get().getRole().getCode();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user.get(), null, List.of(new SimpleGrantedAuthority(authority)));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        // Si no hay token válido la petición sigue sin identidad, y SecurityConfig decide si la deja pasar.
        filterChain.doFilter(request, response);
    }
}
