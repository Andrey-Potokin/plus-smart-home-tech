package ru.yandex.practicum.interaction.api.feign.contract;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;

import java.util.Map;
import java.util.UUID;

public interface ShoppingCartFeignContract {

    ShoppingCartDto getShoppingCart(@RequestParam String username);

    ShoppingCartDto addProductToShoppingCart(@RequestParam String username,
                                             @RequestBody Map<UUID, Long> request);
    void deactivateCurrentShoppingCart(@RequestParam String username);

    ShoppingCartDto removeFromShoppingCart(@RequestParam String username,
                                           @RequestBody Map<UUID, Long> request);

    ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                          @RequestBody ChangeProductQuantityRequestDto requestDto);

    BookedProductsDto bookingProductsForUser(@RequestParam String username);
}