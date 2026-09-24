package application.domain.services.identity;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

// El administrador controla si un comprador puede seguir comprando.
@Service
public class ManageBuyerStatusService {

    private final PersonRepositoryPort personRepository;
    private final PersonLookupService personLookup;

    public ManageBuyerStatusService(PersonRepositoryPort personRepository, PersonLookupService personLookup) {
        this.personRepository = personRepository;
        this.personLookup = personLookup;
    }

    public Buyer suspend(User administrator, String buyerIdentification) {
        Buyer buyer = findBuyer(administrator, buyerIdentification);
        buyer.suspend();
        personRepository.save(buyer);
        return buyer;
    }

    public Buyer activate(User administrator, String buyerIdentification) {
        Buyer buyer = findBuyer(administrator, buyerIdentification);
        buyer.activate();
        personRepository.save(buyer);
        return buyer;
    }

    public Buyer block(User administrator, String buyerIdentification) {
        Buyer buyer = findBuyer(administrator, buyerIdentification);
        buyer.block();
        personRepository.save(buyer);
        return buyer;
    }

    private Buyer findBuyer(User administrator, String buyerIdentification) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        return personLookup.findBuyer(buyerIdentification);
    }
}
