package ru.yandex.practicum.commerce.shoppingstore.service;

import shoppingstore.Pageable;
import shoppingstore.ProductDto;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.List;

public interface ShoppingStoreService {
    List<ProductDto> getProductByCategory(String category, Pageable pageable);

    ProductDto createProduct(ProductDto dto);

    ProductDto updateProduct(ProductDto dto);

    boolean removeProductId(String productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto findProductById(String productId);
}