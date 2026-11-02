package application.adapters.security;

import application.domain.ports.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

// Reglas de acceso por dirección. Es la primera capa de seguridad; la segunda son las reglas de los servicios.
@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepositoryPort userRepository;

    public SecurityConfig(JwtService jwtService, UserRepositoryPort userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS usa la lista de orígenes definida en corsConfigurationSource().
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // Sin sesiones: cada petición se identifica solo con su token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**", "/actuator/health").permitAll()
                        .requestMatchers("/api/buyer/**").hasRole("BUYER")
                        .requestMatchers("/api/seller/**").hasRole("SELLER")
                        .requestMatchers("/api/operator/**").hasRole("LOGISTICS_OPERATOR")
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                        .requestMatchers("/api/supervisor/**").hasRole("SUPERVISOR")
                        .anyRequest().authenticated())
                // Sin token válido se responde 401; con token pero de otro rol, Spring responde 403.
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Páginas web (frontends) que el navegador deja llamar a esta API desde otro origen.
    // Los orígenes vienen de application.properties, separados por comas.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${cors.allowed-origins}") String allowedOrigins) {
        List<String> origins = new ArrayList<>();
        for (String origin : allowedOrigins.split(",")) {
            if (!origin.isBlank()) {
                origins.add(origin.trim());
            }
        }
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
