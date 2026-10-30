package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.responses.RefundResponse;
import application.adapters.rest.dtos.responses.ReturnResponse;
import application.domain.models.Refund;
import application.domain.models.Return;

import java.util.ArrayList;
import java.util.List;

// Convierte devoluciones y reembolsos.
public final class PostSaleRestMapper {

    private PostSaleRestMapper() {
    }

    public static ReturnResponse toReturnResponse(Return returnRequest) {
        return new ReturnResponse(
                returnRequest.getIdentifier(),
                returnRequest.getOrder().getIdentifier(),
                returnRequest.getReason(),
                CatalogMapper.toCode(returnRequest.getStatus()),
                returnRequest.getRequestDate());
    }

    public static List<ReturnResponse> toReturnResponseList(List<Return> returns) {
        List<ReturnResponse> responses = new ArrayList<>();
        for (Return returnRequest : returns) {
            responses.add(toReturnResponse(returnRequest));
        }
        return responses;
    }

    public static RefundResponse toRefundResponse(Refund refund) {
        return new RefundResponse(
                refund.getIdentifier(),
                refund.getReturnRequest().getIdentifier(),
                refund.getAmount(),
                CatalogMapper.toCode(refund.getStatus()),
                refund.getDate());
    }
}
