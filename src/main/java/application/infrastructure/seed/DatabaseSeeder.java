package application.infrastructure.seed;

import application.domain.models.Buyer;
import application.domain.models.DigitalProduct;
import application.domain.models.Employee;
import application.domain.models.PhysicalProduct;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.out.PersonRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.services.catalog.CreateProductService;
import application.domain.services.catalog.ManageProductService;
import application.domain.services.identity.RegisterBuyerService;
import application.domain.services.identity.RegisterEmployeeService;
import application.domain.services.identity.RegisterSellerService;
import application.domain.services.identity.UserAccountService;
import application.domain.services.inventory.RegisterInventoryService;
import application.domain.services.warehouse.RegisterWarehouseService;
import application.domain.valueobjects.SystemRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Crea los datos iniciales al arrancar. Solo se ejecuta si todavía no existe el administrador.
// Usa los servicios del dominio, así los datos cumplen las mismas reglas del negocio.
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final PersonRepositoryPort personRepository;
    private final UserRepositoryPort userRepository;
    private final UserAccountService userAccountService;
    private final RegisterEmployeeService registerEmployeeService;
    private final RegisterSellerService registerSellerService;
    private final RegisterBuyerService registerBuyerService;
    private final RegisterWarehouseService registerWarehouseService;
    private final CreateProductService createProductService;
    private final ManageProductService manageProductService;
    private final RegisterInventoryService registerInventoryService;

    public DatabaseSeeder(PersonRepositoryPort personRepository,
                          UserRepositoryPort userRepository,
                          UserAccountService userAccountService,
                          RegisterEmployeeService registerEmployeeService,
                          RegisterSellerService registerSellerService,
                          RegisterBuyerService registerBuyerService,
                          RegisterWarehouseService registerWarehouseService,
                          CreateProductService createProductService,
                          ManageProductService manageProductService,
                          RegisterInventoryService registerInventoryService) {
        this.personRepository = personRepository;
        this.userRepository = userRepository;
        this.userAccountService = userAccountService;
        this.registerEmployeeService = registerEmployeeService;
        this.registerSellerService = registerSellerService;
        this.registerBuyerService = registerBuyerService;
        this.registerWarehouseService = registerWarehouseService;
        this.createProductService = createProductService;
        this.manageProductService = manageProductService;
        this.registerInventoryService = registerInventoryService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) {
            log.info("Seed data already exists. Skipping.");
            return;
        }
        log.info("Creating seed data...");

        // El primer administrador se crea directamente, porque registrar empleados exige un administrador.
        Employee adminPerson = new Employee();
        adminPerson.setIdentification("1000000001");
        adminPerson.setName("Main Administrator");
        adminPerson.setEmail("admin@nexusmarket.com");
        adminPerson.register(SystemRole.ADMINISTRATOR);
        personRepository.save(adminPerson);
        User admin = userAccountService.createUser(adminPerson, "admin", "admin123");

        registerEmployeeService.register(admin, employee("1000000002", "Logistics Operator", "operator@nexusmarket.com"),
                SystemRole.LOGISTICS_OPERATOR, "operador", "operador123");
        registerEmployeeService.register(admin, employee("1000000003", "Operations Supervisor", "supervisor@nexusmarket.com"),
                SystemRole.SUPERVISOR, "supervisor", "supervisor123");

        Seller sellerPerson = new Seller();
        sellerPerson.setIdentification("900123456");
        sellerPerson.setName("TecnoStore");
        sellerPerson.setEmail("contact@tecnostore.com");
        Warehouse northWarehouse = warehouse("Bodega Norte", "Calle 10 # 20-30");
        User seller = registerSellerService.register(admin, sellerPerson, northWarehouse, "tecnostore", "vendedor123");

        registerWarehouseService.register(admin, warehouse("Bodega Central", "Carrera 50 # 40-20"), null);

        Buyer buyerPerson = new Buyer();
        buyerPerson.setIdentification("1017123456");
        buyerPerson.setName("Ana Gomez");
        buyerPerson.setEmail("ana.gomez@mail.com");
        buyerPerson.setPrincipalAddress("Calle 50 # 40-20");
        registerBuyerService.register(buyerPerson, "ana", "comprador123");

        PhysicalProduct shirt = new PhysicalProduct();
        shirt.setName("Cotton T-Shirt");
        shirt.setDescription("Basic cotton t-shirt");
        shirt.setPrice(new BigDecimal("50000"));
        shirt.setWeight(new BigDecimal("0.300"));
        createProductService.create(seller, shirt);
        manageProductService.publish(seller, shirt.getIdentifier());
        registerInventoryService.register(seller, shirt.getIdentifier(), northWarehouse.getIdentifier(), 20);

        DigitalProduct ebook = new DigitalProduct();
        ebook.setName("Java Basics E-Book");
        ebook.setDescription("Introductory Java e-book");
        ebook.setPrice(new BigDecimal("20000"));
        ebook.setDownloadUrl("https://downloads.nexusmarket.com/java-basics.pdf");
        createProductService.create(seller, ebook);
        manageProductService.publish(seller, ebook.getIdentifier());

        log.info("Seed data created. Administrator user: admin");
    }

    private Employee employee(String identification, String name, String email) {
        Employee employee = new Employee();
        employee.setIdentification(identification);
        employee.setName(name);
        employee.setEmail(email);
        return employee;
    }

    private Warehouse warehouse(String name, String address) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setAddress(address);
        return warehouse;
    }
}
