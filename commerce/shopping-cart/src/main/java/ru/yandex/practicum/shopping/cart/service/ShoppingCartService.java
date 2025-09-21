package ru.yandex.practicum.shopping.cart.service;

import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProduct(String username, Map<UUID, Long> products);

    void deactivateCart(String username);

    ShoppingCartDto deleteProduct(String username, Map<UUID, Long> request);

    ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequestDto requestDto);
}