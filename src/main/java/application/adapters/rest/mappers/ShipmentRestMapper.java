package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.responses.ShipmentResponse;
import application.domain.models.Shipment;

import java.util.ArrayList;
import java.util.List;

public final class ShipmentRestMapper {

    private ShipmentRestMapper() {
    }

    public static ShipmentResponse toResponse(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getIdentifier(),
                shipment.getOrder().getIdentifier(),
                shipment.getOriginWarehouse() == null ? null : shipment.getOriginWarehouse().getName(),
                shipment.getDestinationAddress(),
                CatalogMapper.toCode(shipment.getStatus()),
                shipment.getLogisticsOperator() == null ? null : shipment.getLogisticsOperator().getUsername(),
                shipment.getShipmentDate(),
                shipment.getDeliveryDate());
    }

    public static List<ShipmentResponse> toResponseList(List<Shipment> shipments) {
        List<ShipmentResponse> responses = new ArrayList<>();
        for (Shipment shipment : shipments) {
            responses.add(toResponse(shipment));
        }
        return responses;
    }
}
