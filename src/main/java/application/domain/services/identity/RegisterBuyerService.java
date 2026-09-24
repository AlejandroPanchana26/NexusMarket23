package application.domain.services.identity;

import application.domain.exceptions.BusinessRuleViolationException;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.PersonRepositoryPort;
import org.springframework.stereotype.Service;

// Registro público: cualquier persona puede crear su cuenta de comprador.
@Service
public class RegisterBuyerService {

    private final PersonRepositoryPort personRepository;
    private final UserAccountService userAccountService;

    public RegisterBuyerService(PersonRepositoryPort personRepository, UserAccountService userAccountService) {
        this.personRepository = personRepository;
        this.userAccountService = userAccountService;
    }

    public User register(Buyer buyer, String username, String password) {
        if (buyer == null) {
            throw new BusinessRuleViolationException("Buyer data is required.");
        }
        buyer.register();
        userAccountService.validateNewAccount(buyer, username, password);

        personRepository.save(buyer);
        return userAccountService.createUser(buyer, username, password);
    }
}
