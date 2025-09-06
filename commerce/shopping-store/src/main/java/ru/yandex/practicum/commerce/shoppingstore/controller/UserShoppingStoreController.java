package ru.yandex.practicum.commerce.shoppingstore.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.shoppingstore.service.ShoppingStoreService;
import shoppingstore.ProductCategory;
import shoppingstore.ProductDto;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserShoppingStoreController {

    ShoppingStoreService service;

    @GetMapping
    public List<ProductDto> getProductByCategory(@RequestParam ProductCategory category, Pageable pageable) {
        return service.getProductByCategory(category, pageable);
    }

    @GetMapping("/{productId}")
    public ProductDto findByProductId(@PathVariable UUID productId) {
        return service.findByProductId(productId);
    }
}