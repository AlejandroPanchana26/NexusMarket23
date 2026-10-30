package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.User;

import java.util.List;

// Operaciones disponibles sin iniciar sesión.
public interface PublicAccessPort {

    User registerBuyer(Buyer buyer, String username, String password);

    User login(String username, String password);

    List<Product> listPublishedProducts();
}
