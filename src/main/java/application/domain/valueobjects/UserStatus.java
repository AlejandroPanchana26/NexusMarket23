package application.domain.valueobjects;

public final class UserStatus extends DomainCatalog {

    public static final UserStatus ACTIVE = new UserStatus(
            "ACTIVE", "Activo", "El usuario puede acceder al sistema con normalidad.");
    public static final UserStatus INACTIVE = new UserStatus(
            "INACTIVE", "Inactivo", "El usuario existe pero no puede realizar operaciones en el sistema.");
    public static final UserStatus BLOCKED = new UserStatus(
            "BLOCKED", "Bloqueado", "El acceso del usuario ha sido suspendido.");

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }
}
