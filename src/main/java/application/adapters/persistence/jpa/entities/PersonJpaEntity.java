package application.adapters.persistence.jpa.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Tabla única para compradores, vendedores y empleados; personType indica cuál es cada fila.
@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
public class PersonJpaEntity {

    @Id
    @Column(name = "identification", length = 60)
    private String identification;

    // BUYER, SELLER o EMPLOYEE.
    @Column(name = "person_type", nullable = false, length = 20)
    private String personType;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "phone_number", length = 40)
    private String phoneNumber;

    @Column(name = "role", length = 40)
    private String role;

    // Solo para compradores.
    @Column(name = "principal_address", length = 200)
    private String principalAddress;

    @Column(name = "commercial_status", length = 40)
    private String commercialStatus;

    // Solo para vendedores.
    @Column(name = "seller_status", length = 40)
    private String sellerStatus;

    // Direcciones adicionales del comprador, guardadas en la tabla auxiliar buyer_addresses.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "buyer_addresses", joinColumns = @JoinColumn(name = "person_identification"))
    @Column(name = "address", length = 200)
    private List<String> additionalAddresses = new ArrayList<>();
}
