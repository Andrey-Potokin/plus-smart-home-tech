package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentDto toPaymentDto(Payment payment);
}