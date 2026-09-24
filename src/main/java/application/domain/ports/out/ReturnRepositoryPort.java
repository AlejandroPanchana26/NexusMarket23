package application.domain.ports.out;

import application.domain.models.Return;
import application.domain.valueobjects.ReturnStatus;

import java.util.List;
import java.util.Optional;

public interface ReturnRepositoryPort {

    Return save(Return returnRequest);

    Optional<Return> findByIdentifier(String identifier);

    List<Return> findByOrderIdentifier(String orderIdentifier);

    List<Return> findByStatus(ReturnStatus status);
}
