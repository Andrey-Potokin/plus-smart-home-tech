package ru.yandex.practicum.commerce.payment.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.feign.contract.PaymentFeignContract;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController implements PaymentFeignContract {

    PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody @Valid OrderDto orderDto) {
        log.info("Формирование оплаты для заказа (переход в платежный шлюз): {}", orderDto);
        return paymentService.createPayment(orderDto);
    }

    @Override
    @PostMapping("/totalCost")
    public Double getTotalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Расчёт полной стоимости заказа: {}", orderDto);
        return paymentService.getTotalCost(orderDto);
    }

    @Override
    @PostMapping("/refund")
    public void paymentSuccess(@RequestBody UUID orderId) {
        log.info("Метод для эмуляции успешной оплаты в платежного шлюза: {}", orderId);
        paymentService.paymentSuccess(orderId);
    }

    @Override
    @PostMapping("/productCost")
    public Double productCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Расчёт стоимости товаров в заказе: {}", orderDto);
        return paymentService.productCost(orderDto);
    }

    @Override
    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID orderId) {
        log.info("Метод для эмуляции отказа в оплате платежного шлюза: {}", orderId);
        paymentService.paymentFailed(orderId);
    }
}