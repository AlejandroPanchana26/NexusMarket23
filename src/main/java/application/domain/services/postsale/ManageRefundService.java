package application.domain.services.postsale;

import application.domain.exceptions.DuplicateEntityException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.User;
import application.domain.ports.out.RefundRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.services.AccessValidator;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageRefundService {

    private final RefundRepositoryPort refundRepository;
    private final ReturnRepositoryPort returnRepository;

    public ManageRefundService(RefundRepositoryPort refundRepository, ReturnRepositoryPort returnRepository) {
        this.refundRepository = refundRepository;
        this.returnRepository = returnRepository;
    }

    public Refund create(User user, String returnIdentifier, BigDecimal amount) {
        Optional<Return> found = returnRepository.findByIdentifier(returnIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Return");
        }
        ManageReturnService.requireApprover(user, found.get().getOrder());
        if (refundRepository.findByReturnIdentifier(returnIdentifier).isPresent()) {
            throw new DuplicateEntityException("This return already has a refund.");
        }
        Refund refund = new Refund();
        refund.setIdentifier(UUID.randomUUID().toString());
        refund.createFor(found.get(), amount);
        return refundRepository.save(refund);
    }

    // Procesar o rechazar el reembolso lo hace el administrador, porque la plataforma maneja el dinero.
    public Refund process(User administrator, String refundIdentifier) {
        Refund refund = findRefund(administrator, refundIdentifier);
        refund.process();
        return refundRepository.save(refund);
    }

    public Refund reject(User administrator, String refundIdentifier) {
        Refund refund = findRefund(administrator, refundIdentifier);
        refund.reject();
        return refundRepository.save(refund);
    }

    private Refund findRefund(User administrator, String refundIdentifier) {
        AccessValidator.requireRole(administrator, SystemRole.ADMINISTRATOR);
        Optional<Refund> found = refundRepository.findByIdentifier(refundIdentifier);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Refund");
        }
        return found.get();
    }
}
