package application.adapters.rest.mappers;

import application.adapters.persistence.CatalogMapper;
import application.adapters.rest.dtos.responses.WarehouseResponse;
import application.domain.models.Warehouse;

import java.util.ArrayList;
import java.util.List;

public final class WarehouseRestMapper {

    private WarehouseRestMapper() {
    }

    public static WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getIdentifier(),
                warehouse.getName(),
                warehouse.getAddress(),
                CatalogMapper.toCode(warehouse.getType()),
                warehouse.getOwner() == null ? null : warehouse.getOwner().getIdentification(),
                warehouse.getOwner() == null ? null : warehouse.getOwner().getName());
    }

    public static List<WarehouseResponse> toResponseList(List<Warehouse> warehouses) {
        List<WarehouseResponse> responses = new ArrayList<>();
        for (Warehouse warehouse : warehouses) {
            responses.add(toResponse(warehouse));
        }
        return responses;
    }

    public static Warehouse toWarehouse(String name, String address) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setAddress(address);
        return warehouse;
    }
}
