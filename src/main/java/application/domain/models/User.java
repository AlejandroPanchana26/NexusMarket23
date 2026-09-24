package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.exceptions.InvalidStatusTransitionException;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

    // Identificador interno del usuario del sistema.
    private Integer userId;
    private String username;
    private String password;
    private UserStatus status;

    // Persona que representa este usuario: comprador, vendedor o empleado.
    private Person person;

    public void register() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessRuleViolationException("Username and password are required.");
        }
        if (person == null || person.getRole() == null) {
            throw new BusinessRuleViolationException("User must represent a person with a role.");
        }
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }

    public boolean hasRole(SystemRole role) {
        return person != null && person.hasRole(role);
    }

    public SystemRole getRole() {
        return person == null ? null : person.getRole();
    }

    public void block() {
        requireActive(UserStatus.BLOCKED);
        this.status = UserStatus.BLOCKED;
    }

    public void deactivate() {
        requireActive(UserStatus.INACTIVE);
        this.status = UserStatus.INACTIVE;
    }

    // Un usuario bloqueado o inactivo solo puede volver a quedar activo.
    public void activate() {
        if (status == null || UserStatus.ACTIVE.equals(status)) {
            throw new InvalidStatusTransitionException("User", status, UserStatus.ACTIVE);
        }
        this.status = UserStatus.ACTIVE;
    }

    private void requireActive(UserStatus target) {
        if (!isActive()) {
            throw new InvalidStatusTransitionException("User", status, target);
        }
    }
}
