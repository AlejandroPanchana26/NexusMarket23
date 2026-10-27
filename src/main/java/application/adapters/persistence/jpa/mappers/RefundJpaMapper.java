package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.persistence.jpa.entities.RefundJpaEntity;
import application.domain.models.Refund;
import application.domain.models.Return;

public final class RefundJpaMapper {

    private RefundJpaMapper() {
    }

    public static RefundJpaEntity toEntity(Refund refund) {
        RefundJpaEntity entity = new RefundJpaEntity();
        entity.setIdentifier(refund.getIdentifier());
        entity.setReturnIdentifier(refund.getReturnRequest().getIdentifier());
        entity.setAmount(refund.getAmount());
        entity.setStatus(CatalogMapper.toCode(refund.getStatus()));
        entity.setRefundDate(refund.getDate());
        return entity;
    }

    public static Refund toDomain(RefundJpaEntity entity, Return returnRequest) {
        Refund refund = new Refund();
        refund.setIdentifier(entity.getIdentifier());
        refund.setReturnRequest(returnRequest);
        refund.setAmount(entity.getAmount());
        refund.setStatus(CatalogMapper.refundStatus(entity.getStatus()));
        refund.setDate(entity.getRefundDate());
        return refund;
    }
}
