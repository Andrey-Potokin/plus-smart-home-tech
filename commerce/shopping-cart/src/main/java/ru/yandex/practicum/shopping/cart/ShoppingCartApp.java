package ru.yandex.practicum.shopping.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.shopping.cart.client.WarehouseClient;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan
@EnableFeignClients(clients = {WarehouseClient.class})
public class ShoppingCartApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }
}