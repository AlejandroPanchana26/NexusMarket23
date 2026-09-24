package application.domain.models;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Personal interno de la plataforma: operador logístico, administrador o supervisor.
@Getter
@Setter
@NoArgsConstructor
public class Employee extends Person {

    public void register(SystemRole employeeRole) {
        validateIdentity();
        if (!isInternalRole(employeeRole)) {
            throw new BusinessRuleViolationException("Role is not valid for an internal employee.");
        }
        setRole(employeeRole);
    }

    private static boolean isInternalRole(SystemRole role) {
        return SystemRole.LOGISTICS_OPERATOR.equals(role)
                || SystemRole.ADMINISTRATOR.equals(role)
                || SystemRole.SUPERVISOR.equals(role);
    }
}
