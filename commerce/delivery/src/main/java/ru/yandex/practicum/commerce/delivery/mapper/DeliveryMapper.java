package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeliveryMapper {

    DeliveryDto toDeliveryDto(Delivery delivery);

    Delivery toDelivery(DeliveryDto deliveryDto);
}