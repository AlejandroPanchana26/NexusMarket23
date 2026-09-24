package application.domain.services.identity;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.DuplicateEntityException;
import application.domain.models.Person;
import application.domain.models.User;
import application.domain.ports.out.PasswordEncoderPort;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

// Pasos comunes a todos los registros: validar que la cuenta sea nueva y crear el usuario.
@Service
public class UserAccountService {

    private final PersonRepositoryPort personRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UserAccountService(PersonRepositoryPort personRepository,
                              UserRepositoryPort userRepository,
                              PasswordEncoderPort passwordEncoder) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Regla del PDF: identificación y correo únicos en la plataforma. El usuario tampoco puede repetirse.
    public void validateNewAccount(Person person, String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessRuleViolationException("Username and password are required.");
        }
        if (personRepository.existsByIdentification(person.getIdentification())) {
            throw new DuplicateEntityException("Identification is already registered.");
        }
        if (personRepository.existsByEmail(person.getEmail())) {
            throw new DuplicateEntityException("Email is already registered.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException("Username is already taken.");
        }
    }

    // La contraseña se guarda cifrada, nunca tal como la escribió el usuario.
    public User createUser(Person person, String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPerson(person);
        user.register();
        return userRepository.save(user);
    }
}
