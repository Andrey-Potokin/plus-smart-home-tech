package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.warehouse.model.Warehouse;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper {

    Warehouse toWarehouse(NewProductInWarehouseRequestDto newProductInWarehouseRequest);
}