package application.domain.services.identity;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.Employee;
import application.domain.models.User;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

// El administrador registra al personal interno: operador logístico, administrador o supervisor.
@Service
public class RegisterEmployeeService {

    private final PersonRepositoryPort personRepository;
    private final UserAccountService userAccountService;

    public RegisterEmployeeService(PersonRepositoryPort personRepository, UserAccountService userAccountService) {
        this.personRepository = personRepository;
        this.userAccountService = userAccountService;
    }

    public User register(User administrator, Employee employee, SystemRole role,
                         String username, String password) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        if (employee == null) {
            throw new BusinessRuleViolationException("Employee data is required.");
        }
        employee.register(role);
        userAccountService.validateNewAccount(employee, username, password);

        personRepository.save(employee);
        return userAccountService.createUser(employee, username, password);
    }
}
