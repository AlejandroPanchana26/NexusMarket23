package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.requests.RegisterBuyerRequest;
import application.adapters.rest.dtos.responses.UserResponse;
import application.domain.models.Buyer;
import application.domain.models.Person;
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
}
