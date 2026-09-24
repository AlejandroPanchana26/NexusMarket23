package application.domain.exceptions;

import application.domain.valueobjects.DomainCatalog;

public class InvalidStatusTransitionException extends DomainException {

    public InvalidStatusTransitionException(String entity, DomainCatalog from, DomainCatalog to) {
        super("Invalid " + entity + " status transition from " + codeOf(from) + " to " + codeOf(to) + ".");
    }

    private static String codeOf(DomainCatalog status) {
        return status == null ? "NONE" : status.getCode();
    }
}
