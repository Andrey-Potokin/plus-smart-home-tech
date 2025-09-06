package ru.yandex.practicum.commerce.shoppingstore.service;

import org.springframework.data.domain.Pageable;
import shoppingstore.ProductCategory;
import shoppingstore.ProductDto;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    List<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable);

    ProductDto createProduct(ProductDto dto);

    ProductDto updateProduct(ProductDto dto);

    boolean removeByProductId(UUID productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto findByProductId(UUID productId);
}