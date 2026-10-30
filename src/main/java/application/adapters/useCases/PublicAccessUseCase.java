package application.adapters.useCases;

import application.domain.models.Buyer;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.in.PublicAccessPort;
import application.domain.services.catalog.ConsultCatalogService;
import application.domain.services.identity.LoginService;
import application.domain.services.identity.RegisterBuyerService;
import org.springframework.stereotype.Component;

import java.util.List;

// Implementa PublicAccessPort: cada método solo delega en el servicio del dominio que corresponde.
@Component
public class PublicAccessUseCase implements PublicAccessPort {

    private final RegisterBuyerService registerBuyerService;
    private final LoginService loginService;
    private final ConsultCatalogService consultCatalogService;

    public PublicAccessUseCase(RegisterBuyerService registerBuyerService,
                               LoginService loginService,
                               ConsultCatalogService consultCatalogService) {
        this.registerBuyerService = registerBuyerService;
        this.loginService = loginService;
        this.consultCatalogService = consultCatalogService;
    }

    @Override
    public User registerBuyer(Buyer buyer, String username, String password) {
        return registerBuyerService.register(buyer, username, password);
    }

    @Override
    public User login(String username, String password) {
        return loginService.login(username, password);
    }

    @Override
    public List<Product> listPublishedProducts() {
        return consultCatalogService.listPublished();
    }
}
