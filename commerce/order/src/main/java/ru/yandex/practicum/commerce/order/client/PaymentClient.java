package ru.yandex.practicum.commerce.order.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.feign.contract.PaymentFeignContract;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentFeignContract {

    @Override
    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto);

    @Override
    @PostMapping("/totalCost")
    Double getTotalCost(@RequestBody @Valid OrderDto orderDto);

    @Override
    @PostMapping("/refund")
    void paymentSuccess(@RequestBody UUID orderId);

    @Override
    @PostMapping("/productCost")
    Double productCost(@RequestBody @Valid OrderDto orderDto);

    @Override
    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID orderId);
}