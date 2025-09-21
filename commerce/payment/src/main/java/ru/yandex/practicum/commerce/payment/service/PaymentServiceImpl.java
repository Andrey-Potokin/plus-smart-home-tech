package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.payment.client.OrderClient;
import ru.yandex.practicum.commerce.payment.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentState;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.exception.NoPaymentFoundException;
import ru.yandex.practicum.interaction.api.exception.NotEnoughInfoInOrderToCalculateException;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    PaymentMapper paymentMapper;
    PaymentRepository paymentRepository;
    ShoppingStoreClient shoppingStoreClient;
    OrderClient orderClient;

    private final String MESSAGE_NOT_INFORMATION = "Недостаточно информации в заказе для расчёта.";
    private final String MESSAGE_PAYMENT_NOT_FOUND = "Указанная оплата не найдена.";

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        checkOrder(orderDto);
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(orderDto.getTotalPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .productsTotal(orderDto.getProductsPrice())
                .feeTotal(getTax(orderDto.getTotalPrice()))
                .status(PaymentState.PENDING)
                .build();
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalCost(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        return orderDto.getProductsPrice() + getTax(orderDto.getProductsPrice()) + orderDto.getDeliveryPrice();
    }

    @Override
    public void paymentSuccess(UUID uuid) {
        Payment payment = paymentRepository.findById(uuid).orElseThrow(
                () -> new NoPaymentFoundException(MESSAGE_PAYMENT_NOT_FOUND));
        payment.setStatus(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
    }

    @Override
    @Transactional(readOnly = true)
    public Double productCost(OrderDto orderDto) {
        double productCost = 0.0;
        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto product = shoppingStoreClient.getProduct(entry.getKey());
            productCost += product.getPrice() * entry.getValue();
        }
        return productCost;
    }

    @Override
    public void paymentFailed(UUID uuid) {
        Payment payment = paymentRepository.findById(uuid).orElseThrow(
                () -> new NoPaymentFoundException(MESSAGE_PAYMENT_NOT_FOUND));
        payment.setStatus(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private void checkOrder(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        if (orderDto.getProductsPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
        if (orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(MESSAGE_NOT_INFORMATION);
        }
    }

    private double getTax(double totalPrice) {
        return totalPrice * 0.1;
    }
}