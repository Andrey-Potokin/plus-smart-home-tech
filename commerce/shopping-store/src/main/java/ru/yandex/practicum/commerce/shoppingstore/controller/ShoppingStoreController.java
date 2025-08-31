package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.shoppingstore.service.ShoppingStoreService;
import shoppingstore.Pageable;
import shoppingstore.ProductDto;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingStoreController {

    ShoppingStoreService service;

    @GetMapping
    public List<ProductDto> getProductByCategory(@RequestBody String category, Pageable pageable) {
        return service.getProductByCategory(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        log.info("Создание нового продукта: {}", dto.getProductName());
        ProductDto createdProduct = service.createProduct(dto);
        log.debug("Продукт успешно создан с ID: {}", createdProduct.getProductId());
        return createdProduct;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto dto) {
        return service.updateProduct(dto);
    }

    // Для админа
    @PostMapping("/removeProductFromStore")
    public boolean removeProductId(@RequestBody String productId) {
        return service.removeProductId(productId);
    }

    // Для склада
    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return service.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto findProductById(@PathVariable String productId) {
        return service.findProductById(productId);
    }
}