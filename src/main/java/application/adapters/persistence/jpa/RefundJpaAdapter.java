package application.adapters.persistence.jpa;

import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.adapters.persistence.jpa.mappers.RefundJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaRefundRepository;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefundJpaAdapter implements RefundRepositoryPort {

    private final SpringDataJpaRefundRepository refundRepository;
    private final ReturnRepositoryPort returnRepository;

    public RefundJpaAdapter(SpringDataJpaRefundRepository refundRepository, ReturnRepositoryPort returnRepository) {
        this.refundRepository = refundRepository;
        this.returnRepository = returnRepository;
    }

    @Override
    public Refund save(Refund refund) {
        refundRepository.save(RefundJpaMapper.toEntity(refund));
        return refund;
    }

    @Override
    public Optional<Refund> findByIdentifier(String identifier) {
        Optional<RefundJpaEntity> found = refundRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public Optional<Refund> findByReturnIdentifier(String returnIdentifier) {
        Optional<RefundJpaEntity> found = refundRepository.findByReturnIdentifier(returnIdentifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    // La devolución trae consigo su pedido.
    private Refund toDomain(RefundJpaEntity entity) {
        Return returnRequest = returnRepository.findByIdentifier(entity.getReturnIdentifier()).orElse(null);
        return RefundJpaMapper.toDomain(entity, returnRequest);
    }
}
