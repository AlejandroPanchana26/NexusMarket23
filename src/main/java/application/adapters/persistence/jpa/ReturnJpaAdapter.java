package application.adapters.persistence.jpa;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.ReturnJpaEntity;
import application.adapters.persistence.jpa.mappers.ReturnJpaMapper;
import application.adapters.persistence.jpa.repositories.SpringDataJpaReturnRepository;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.ports.out.ReturnRepositoryPort;
import application.domain.valueobjects.ReturnStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReturnJpaAdapter implements ReturnRepositoryPort {

    private final SpringDataJpaReturnRepository returnRepository;
    private final OrderRepositoryPort orderRepository;

    public ReturnJpaAdapter(SpringDataJpaReturnRepository returnRepository, OrderRepositoryPort orderRepository) {
        this.returnRepository = returnRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Return save(Return returnRequest) {
        returnRepository.save(ReturnJpaMapper.toEntity(returnRequest));
        return returnRequest;
    }

    @Override
    public Optional<Return> findByIdentifier(String identifier) {
        Optional<ReturnJpaEntity> found = returnRepository.findById(identifier);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDomain(found.get()));
    }

    @Override
    public List<Return> findByOrderIdentifier(String orderIdentifier) {
        return toDomainList(returnRepository.findByOrderIdentifier(orderIdentifier));
    }

    @Override
    public List<Return> findByStatus(ReturnStatus status) {
        return toDomainList(returnRepository.findByStatus(CatalogMapper.toCode(status)));
    }

    private List<Return> toDomainList(List<ReturnJpaEntity> entities) {
        List<Return> returns = new ArrayList<>();
        for (ReturnJpaEntity entity : entities) {
            returns.add(toDomain(entity));
        }
        return returns;
    }

    private Return toDomain(ReturnJpaEntity entity) {
        Order order = orderRepository.findByIdentifier(entity.getOrderIdentifier()).orElse(null);
        return ReturnJpaMapper.toDomain(entity, order);
    }
}
