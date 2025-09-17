package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentFeignContract {

    PaymentDto createPayment(@RequestBody OrderDto orderDto);

    Double getTotalCost(@RequestBody OrderDto orderDto);

    void paymentSuccess(@RequestBody UUID orderId);

    Double productCost(@RequestBody OrderDto orderDto);

    void paymentFailed(@RequestBody UUID orderId);
}