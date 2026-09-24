package application.domain.services;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

// Aplica las reglas RG-01 y RG-03 del PDF: usuario autenticado y con un rol permitido.
public final class AccessValidator {

    // No se crean objetos de esta clase; solo se usa su método.
    private AccessValidator() {
    }

    public static void requireRole(User user, SystemRole... allowedRoles) {
        if (user == null || !user.isActive()) {
            throw new UnauthorizedOperationException("An active authenticated user is required.");
        }
        for (SystemRole role : allowedRoles) {
            if (user.hasRole(role)) {
                return;
            }
        }
        throw new UnauthorizedOperationException("User role is not allowed to perform this operation.");
    }

    // Regla RG-03: si quien opera es un vendedor, solo puede tocar lo que le pertenece.
    public static void requireOwnerIfSeller(User user, Seller owner) {
        if (!user.hasRole(SystemRole.SELLER)) {
            return;
        }
        String userId = user.getPerson().getIdentification();
        if (owner == null || !userId.equals(owner.getIdentification())) {
            throw new UnauthorizedOperationException("Sellers can only manage their own information.");
        }
    }
}
