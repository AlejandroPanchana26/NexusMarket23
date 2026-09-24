package application.domain.services.identity;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Person;
import application.domain.models.Seller;
import application.domain.ports.out.PersonRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Busca personas de un tipo específico; varios servicios lo necesitan.
@Service
public class PersonLookupService {

    private final PersonRepositoryPort personRepository;

    public PersonLookupService(PersonRepositoryPort personRepository) {
        this.personRepository = personRepository;
    }

    public Seller findSeller(String identification) {
        Optional<Person> found = personRepository.findByIdentification(identification);
        if (found.isPresent() && found.get() instanceof Seller seller) {
            return seller;
        }
        throw new EntityNotFoundException("Seller");
    }

    public Buyer findBuyer(String identification) {
        Optional<Person> found = personRepository.findByIdentification(identification);
        if (found.isPresent() && found.get() instanceof Buyer buyer) {
            return buyer;
        }
        throw new EntityNotFoundException("Buyer");
    }
}
