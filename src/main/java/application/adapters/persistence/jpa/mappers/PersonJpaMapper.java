package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.PersonJpaEntity;
import application.domain.models.Buyer;
import application.domain.models.Employee;
import application.domain.models.Person;
import application.domain.models.Seller;

import java.util.ArrayList;

// Convierte entre las personas del dominio y la tabla persons.
public final class PersonJpaMapper {

    private static final String BUYER = "BUYER";
    private static final String SELLER = "SELLER";
    private static final String EMPLOYEE = "EMPLOYEE";

    private PersonJpaMapper() {
    }

    public static PersonJpaEntity toEntity(Person person) {
        PersonJpaEntity entity = new PersonJpaEntity();
        entity.setIdentification(person.getIdentification());
        entity.setName(person.getName());
        entity.setEmail(person.getEmail());
        entity.setPhoneNumber(person.getPhoneNumber());
        entity.setRole(CatalogMapper.toCode(person.getRole()));

        // Según el tipo de persona se llenan sus columnas propias.
        if (person instanceof Buyer buyer) {
            entity.setPersonType(BUYER);
            entity.setPrincipalAddress(buyer.getPrincipalAddress());
            entity.setAdditionalAddresses(new ArrayList<>(buyer.getAdditionalAddresses()));
            entity.setCommercialStatus(CatalogMapper.toCode(buyer.getCommercialStatus()));
        } else if (person instanceof Seller seller) {
            entity.setPersonType(SELLER);
            entity.setSellerStatus(CatalogMapper.toCode(seller.getSellerStatus()));
        } else {
            entity.setPersonType(EMPLOYEE);
        }
        return entity;
    }

    public static Person toDomain(PersonJpaEntity entity) {
        Person person;
        if (BUYER.equals(entity.getPersonType())) {
            Buyer buyer = new Buyer();
            buyer.setPrincipalAddress(entity.getPrincipalAddress());
            buyer.setAdditionalAddresses(new ArrayList<>(entity.getAdditionalAddresses()));
            buyer.setCommercialStatus(CatalogMapper.buyerStatus(entity.getCommercialStatus()));
            person = buyer;
        } else if (SELLER.equals(entity.getPersonType())) {
            Seller seller = new Seller();
            seller.setSellerStatus(CatalogMapper.sellerStatus(entity.getSellerStatus()));
            person = seller;
        } else {
            person = new Employee();
        }
        person.setIdentification(entity.getIdentification());
        person.setName(entity.getName());
        person.setEmail(entity.getEmail());
        person.setPhoneNumber(entity.getPhoneNumber());
        person.setRole(CatalogMapper.systemRole(entity.getRole()));
        return person;
    }
}
