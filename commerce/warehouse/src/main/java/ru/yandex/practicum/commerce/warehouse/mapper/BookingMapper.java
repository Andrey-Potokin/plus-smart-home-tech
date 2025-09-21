package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.warehouse.model.Booking;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    BookedProductsDto toBookedProductsDto(Booking booking);
}