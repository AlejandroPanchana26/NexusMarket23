package application.adapters.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserJpaEntity {

    // MySQL genera el número automáticamente al guardar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, unique = true, length = 80)
    private String username;

    // Siempre cifrada.
    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "status", length = 40)
    private String status;

    // Identificación de la persona que representa el usuario.
    @Column(name = "person_identification", nullable = false, length = 60)
    private String personIdentification;
}
