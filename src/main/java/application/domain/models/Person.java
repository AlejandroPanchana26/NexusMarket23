package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {

    // Identificador único de la persona.
    private String identification;
    private String name;
    private String email;
    private String phoneNumber;

    // El rol se define a nivel de Person porque representa qué es la persona dentro del sistema.
    private SystemRole role;

    public boolean hasRole(SystemRole expectedRole) {
        return expectedRole != null && expectedRole.equals(role);
    }

    protected void validateIdentity() {
        requireText(identification, "Identification");
        requireText(name, "Name");
        requireText(email, "Email");
        if (!email.contains("@")) {
            throw new BusinessRuleViolationException("Email must be a valid address.");
        }
    }

    protected static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException(field + " is required.");
        }
    }
}
