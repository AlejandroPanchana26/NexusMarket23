package application.domain.services.identity;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

// El administrador controla si un vendedor puede seguir vendiendo.
@Service
public class ManageSellerStatusService {

    private final PersonRepositoryPort personRepository;
    private final PersonLookupService personLookup;

    public ManageSellerStatusService(PersonRepositoryPort personRepository, PersonLookupService personLookup) {
        this.personRepository = personRepository;
        this.personLookup = personLookup;
    }

    public Seller suspend(User administrator, String sellerIdentification) {
        Seller seller = findSeller(administrator, sellerIdentification);
        seller.suspend();
        personRepository.save(seller);
        return seller;
    }

    public Seller activate(User administrator, String sellerIdentification) {
        Seller seller = findSeller(administrator, sellerIdentification);
        seller.activate();
        personRepository.save(seller);
        return seller;
    }

    public Seller deactivate(User administrator, String sellerIdentification) {
        Seller seller = findSeller(administrator, sellerIdentification);
        seller.deactivate();
        personRepository.save(seller);
        return seller;
    }

    private Seller findSeller(User administrator, String sellerIdentification) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        return personLookup.findSeller(sellerIdentification);
    }
}
