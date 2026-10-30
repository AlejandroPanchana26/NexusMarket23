package application.adapters.security;

import application.domain.models.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Crea y verifica los tokens JWT. El token va firmado con una clave secreta que solo conoce el servidor,
// por eso no se puede falsificar ni modificar sin que la firma deje de coincidir.
@Component
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    // El token guarda el nombre de usuario, su rol y la fecha en que vence.
    public String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMinutes * 60 * 1000);
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().getCode())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // Devuelve el usuario del token, o null si el token es falso, fue modificado o ya venció.
    public String getUsername(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }
}
