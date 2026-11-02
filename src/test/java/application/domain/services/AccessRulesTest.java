package application.domain.services;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Buyer;
import application.domain.models.Employee;
import application.domain.models.PhysicalProduct;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.services.catalog.ManageProductService;
import application.domain.services.identity.LoginService;
import application.domain.services.identity.RegisterBuyerService;
import application.domain.services.identity.RegisterSellerService;
import application.domain.services.identity.UserAccountService;
import application.domain.valueobjects.SystemRole;
import application.support.InMemoryPorts;
import application.support.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Reglas de acceso (RG-01 y RG-03): cada rol solo puede hacer lo suyo y sobre lo suyo.
class AccessRulesTest {

    private InMemoryPorts.People people;
    private InMemoryPorts.Users users;
    private InMemoryPorts.Products products;
    private UserAccountService accounts;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        people = new InMemoryPorts.People();
        users = new InMemoryPorts.Users();
        products = new InMemoryPorts.Products();
        InMemoryPorts.Encoder encoder = new InMemoryPorts.Encoder();
        accounts = new UserAccountService(people, users, encoder);
        loginService = new LoginService(users, encoder);
    }

    private User employee(String identification, SystemRole role, String username) {
        Employee employee = new Employee();
        employee.setIdentification(identification);
        employee.setName("Employee " + identification);
        employee.setEmail(identification + "@nexus.com");
        employee.register(role);
        people.save(employee);
        return accounts.createUser(employee, username, "secret");
    }

    @Test
    void sellerCannotChangeAnotherSellersProduct() {
        Seller tecno = TestData.activeSeller("200", "Tecno");
        Seller moda = TestData.activeSeller("201", "Moda");
        people.save(tecno);
        people.save(moda);
        accounts.createUser(tecno, "tecno", "secret");
        User modaUser = accounts.createUser(moda, "moda", "secret");
        PhysicalProduct shirt = TestData.publishedPhysicalProduct("P1", "50000", tecno);
        products.save(shirt);
        ManageProductService manageProduct = new ManageProductService(products);

        assertThrows(UnauthorizedOperationException.class,
                () -> manageProduct.changePrice(modaUser, "P1", new BigDecimal("1")));
        assertEquals(0, new BigDecimal("50000").compareTo(shirt.getPrice()));
    }

    @Test
    void supervisorCannotRegisterSellers() {
        User supervisor = employee("301", SystemRole.SUPERVISOR, "supervisor");
        RegisterSellerService registerSeller = new RegisterSellerService(people, new InMemoryPorts.Warehouses(), accounts);
        Seller seller = new Seller();
        seller.setIdentification("200");
        seller.setName("Tecno");
        seller.setEmail("tecno@store.com");
        Warehouse warehouse = new Warehouse();
        warehouse.setName("North");
        warehouse.setAddress("Street 1");

        assertThrows(UnauthorizedOperationException.class,
                () -> registerSeller.register(supervisor, seller, warehouse, "tecno", "secret"));
    }

    @Test
    void emailCannotBeRegisteredTwice() {
        RegisterBuyerService registerBuyer = new RegisterBuyerService(people, accounts);
        registerBuyer.register(TestData.activeBuyer("400", "Ana"), "ana", "secret");
        Buyer copy = TestData.activeBuyer("401", "Copy");
        copy.setEmail("400@MAIL.com");

        assertThrows(DuplicateEntityException.class, () -> registerBuyer.register(copy, "copy", "secret"));
    }

    @Test
    void wrongPasswordIsRejected() {
        employee("100", SystemRole.ADMINISTRATOR, "admin");

        assertThrows(UnauthorizedOperationException.class, () -> loginService.login("admin", "wrong"));
    }

    @Test
    void blockedUserCannotLogIn() {
        User operator = employee("300", SystemRole.LOGISTICS_OPERATOR, "operator");
        operator.block();

        assertThrows(UnauthorizedOperationException.class, () -> loginService.login("operator", "secret"));
    }
}
