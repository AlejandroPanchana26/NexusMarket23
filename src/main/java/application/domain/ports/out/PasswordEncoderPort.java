package application.domain.ports.out;

// El dominio nunca guarda contraseñas en texto plano; el cifrado real lo implementa un adaptador.
public interface PasswordEncoderPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
