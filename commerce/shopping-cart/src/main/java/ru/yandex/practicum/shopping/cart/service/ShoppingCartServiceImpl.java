package ru.yandex.practicum.shopping.cart.service;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.cart.CartState;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.exception.BadRequestException;
import ru.yandex.practicum.interaction.api.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction.api.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.shopping.cart.client.WarehouseClient;
import ru.yandex.practicum.shopping.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.shopping.cart.model.ShoppingCart;
import ru.yandex.practicum.shopping.cart.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    WarehouseClient client;
    ShoppingCartMapper mapper;
    ShoppingCartRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        checkUsername(username);
        log.info("Поиск корзины для пользователя: {} ", username);

        ShoppingCart shoppingCart = repository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return ShoppingCart.builder()
                            .username(username)
                            .status(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });
        return mapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        checkUsername(username);
        log.info("Запрос на добавление товаров в корзину пользователя: {}. Количество товаров: {}", username,
                products.size());

        if (products.isEmpty()) {
            throw new BadRequestException("Список продуктов для добавления не может быть пустым");
        }

        log.info("Поиск корзины для пользователя: {} ", username);

        ShoppingCart shoppingCart = repository.findByUsername(username)
                .orElseGet(() -> {
                    log.info("Корзина для пользователя {} не найдена. Создаю новую.", username);
                    return ShoppingCart.builder()
                            .username(username)
                            .status(CartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                });

        shoppingCart = repository.save(shoppingCart);

        updateCartProducts(shoppingCart, products);

        try {
            log.info("проверяю наличие на складе: id = {}, products = {}", shoppingCart.getShoppingCartId(),
                    shoppingCart.getProducts());

            BookedProductsDto bookedProductsDto = client.checkProductQuantityEnoughForShoppingCart(
                    mapper.toDto(shoppingCart)
            );

            log.info("Проверено наличие на складе: {}", bookedProductsDto);

        } catch (FeignException e) {
            log.error("Ошибка вызова склада: {}", e.getMessage());
            throw new RuntimeException("Склад не доступен", e);
        }

        ShoppingCartDto result = mapper.toDto(shoppingCart);

        log.info("Товары добавлены в корзину пользователя: {}. Итоговое количество товаров: {}",
                username, result.getProducts().size());

        return result;
    }

    @Override
    public void deactivateCart(String username) {
        checkUsername(username);
        log.info("Запрос на деактивацию корзины для пользователя: {}", username);

        ShoppingCart shoppingCart = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("Корзина для пользователя %s не найдена",
                        username)));

        if (shoppingCart.getStatus() != CartState.DEACTIVATE) {
            shoppingCart.setStatus(CartState.DEACTIVATE);
            repository.save(shoppingCart);
            log.info("Корзина пользователя {} деактивирована", username);
        } else {
            log.debug("Корзина уже деактивирована для пользователя: {}", username);
        }
    }

    @Override
    public ShoppingCartDto deleteProduct(String username, Map<UUID, Long> request) {
        checkUsername(username);
        ShoppingCart shoppingCart = repository.findByUsername(username).orElseThrow(
                () -> new NoProductsInShoppingCartException(String.format("Корзина для пользователя %s не найдена", username))
        );
        shoppingCart.setProducts(request);
        return mapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequestDto requestDto) {
        checkUsername(username);

        if (requestDto == null) throw new BadRequestException("Запрос на обновление не может быть null");

        ShoppingCart shoppingCart = repository.findByUsernameAndStatus(username, CartState.ACTIVE)
                .orElseThrow(() -> new NotFoundException(String.format("Активной корзины покупок " +
                                                                       "для пользователя %s не найдено", username)));

        UUID productId = requestDto.getProductId();
        Long newQuantity = requestDto.getNewQuantity();

        Map<UUID, Long> products = shoppingCart.getProducts();

        if (products == null) {
            products = new HashMap<>();
            shoppingCart.setProducts(products);
        }

        if (!products.containsKey(productId)) {
            throw new BadRequestException(String.format("Товар с ID %s отсутствует в корзине", productId));
        }

        if (newQuantity == 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return mapper.toDto(repository.save(shoppingCart));
    }

    private void checkUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым.");
        }
    }

    private void updateCartProducts(ShoppingCart shoppingCart, Map<UUID, Long> newProducts) {
        if (shoppingCart.getProducts() == null) {
            shoppingCart.setProducts(new HashMap<>());
        }
        newProducts.forEach((productId, quantity) ->
                shoppingCart.getProducts().merge(productId, quantity, Long::sum));
    }
}