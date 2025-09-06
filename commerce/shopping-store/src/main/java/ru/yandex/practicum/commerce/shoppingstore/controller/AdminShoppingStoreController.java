package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.shoppingstore.service.ShoppingStoreService;
import shoppingstore.ProductDto;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminShoppingStoreController {

    ShoppingStoreService service;

    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        log.info("Создание нового продукта: {}", dto.getProductName());
        ProductDto createdProduct = service.createProduct(dto);
        log.debug("Продукт успешно создан с ID: {}", createdProduct.getProductId());
        return createdProduct;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto dto) {
        log.info("Обновление продукта: {}", dto.getProductName());
        ProductDto updatedProduct = service.updateProduct(dto);
        log.debug("Продукт успешно обновлен с ID: {}", updatedProduct.getProductId());
        return updatedProduct;
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeByProductId(@RequestBody UUID productId) {
        log.info("Удаление продукта с ID: {}", productId);
        return service.removeByProductId(productId);
    }

    // Для склада
    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        log.info("Изменение состояния количества продукта: {}", request);
        return service.setProductQuantityState(request);
    }
}