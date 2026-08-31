package application.domain.models;

import application.domain.valueobjects.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

    // Identificador interno del usuario del sistema.
    private Integer userId;
    private String username;
    private String password;
    private UserStatus status;

    // Persona (comprador o vendedor) que este usuario representa en el sistema.
    private Person person;
}
