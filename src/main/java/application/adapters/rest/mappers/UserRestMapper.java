package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.requests.RegisterBuyerRequest;
import application.adapters.rest.dtos.requests.RegisterEmployeeRequest;
import application.adapters.rest.dtos.requests.RegisterSellerRequest;
import application.adapters.rest.dtos.responses.PersonStatusResponse;
import application.adapters.rest.dtos.responses.UserResponse;
import application.domain.models.Buyer;
import application.domain.models.Employee;
import application.domain.models.Person;
import application.domain.models.Seller;
import application.domain.models.User;

import java.util.ArrayList;
import java.util.List;

// Convierte entre los DTOs de la API y los modelos de usuarios y personas.
public final class UserRestMapper {

    private UserRestMapper() {
    }

    public static UserResponse toResponse(User user) {
        Person person = user.getPerson();
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                CatalogMapper.toCode(user.getStatus()),
                CatalogMapper.toCode(user.getRole()),
                person == null ? null : person.getIdentification(),
                person == null ? null : person.getName(),
                person == null ? null : person.getEmail());
    }

    public static List<UserResponse> toResponseList(List<User> users) {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(toResponse(user));
        }
        return responses;
    }

    public static Buyer toBuyer(RegisterBuyerRequest request) {
        Buyer buyer = new Buyer();
        buyer.setIdentification(request.identification());
        buyer.setName(request.name());
        buyer.setEmail(request.email());
        buyer.setPhoneNumber(request.phoneNumber());
        buyer.setPrincipalAddress(request.principalAddress());
        if (request.additionalAddresses() != null) {
            for (String address : request.additionalAddresses()) {
                buyer.addAdditionalAddress(address);
            }
        }
        return buyer;
    }

    public static Seller toSeller(RegisterSellerRequest request) {
        Seller seller = new Seller();
        seller.setIdentification(request.identification());
        seller.setName(request.name());
        seller.setEmail(request.email());
        seller.setPhoneNumber(request.phoneNumber());
        return seller;
    }

    public static Employee toEmployee(RegisterEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setIdentification(request.identification());
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPhoneNumber(request.phoneNumber());
        return employee;
    }

    // El estado que se muestra depende del tipo de persona.
    public static PersonStatusResponse toPersonStatusResponse(Person person) {
        String status = null;
        if (person instanceof Seller seller) {
            status = CatalogMapper.toCode(seller.getSellerStatus());
        } else if (person instanceof Buyer buyer) {
            status = CatalogMapper.toCode(buyer.getCommercialStatus());
        }
        return new PersonStatusResponse(person.getIdentification(), person.getName(),
                CatalogMapper.toCode(person.getRole()), status);
    }
}
