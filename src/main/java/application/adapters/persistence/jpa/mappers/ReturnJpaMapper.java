package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.ReturnJpaEntity;
import application.domain.models.Order;
import application.domain.models.Return;

public final class ReturnJpaMapper {

    private ReturnJpaMapper() {
    }

    public static ReturnJpaEntity toEntity(Return returnRequest) {
        ReturnJpaEntity entity = new ReturnJpaEntity();
        entity.setIdentifier(returnRequest.getIdentifier());
        entity.setOrderIdentifier(returnRequest.getOrder().getIdentifier());
        entity.setReason(returnRequest.getReason());
        entity.setStatus(CatalogMapper.toCode(returnRequest.getStatus()));
        entity.setRequestDate(returnRequest.getRequestDate());
        return entity;
    }

    public static Return toDomain(ReturnJpaEntity entity, Order order) {
        Return returnRequest = new Return();
        returnRequest.setIdentifier(entity.getIdentifier());
        returnRequest.setOrder(order);
        returnRequest.setReason(entity.getReason());
        returnRequest.setStatus(CatalogMapper.returnStatus(entity.getStatus()));
        returnRequest.setRequestDate(entity.getRequestDate());
        return returnRequest;
    }
}
