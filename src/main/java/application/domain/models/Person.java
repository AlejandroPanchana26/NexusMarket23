package application.domain.models;

import application.domain.valueobjects.SystemRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Person {

    // Identificador único de la persona.
    private String identification;
    private String name;
    private String email;
    private String phoneNumber;

    // El rol se define a nivel de Person porque representa qué es la persona dentro del sistema.
    private SystemRole role;
}
