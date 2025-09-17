package ru.yandex.practicum.commerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.commerce.order.client.DeliveryClient;
import ru.yandex.practicum.commerce.order.client.PaymentClient;
import ru.yandex.practicum.commerce.order.client.ShoppingCartClient;
import ru.yandex.practicum.commerce.order.client.WarehouseClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {
        WarehouseClient.class,
        DeliveryClient.class,
        PaymentClient.class,
        ShoppingCartClient.class
})
public class OrderApp {
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }
}