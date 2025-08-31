package ru.yandex.practicum.commerce.shoppingstore.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shoppingstore.repository.ShoppingStoreRepository;
import shoppingstore.Pageable;
import shoppingstore.ProductDto;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    ShoppingStoreRepository repository;

    @Override
    public List<ProductDto> getProductByCategory(String category, Pageable pageable) {
        return List.of();
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        return ProductMapper.toProductDto(repository.save(ProductMapper.toProduct(dto)));
    }

    @Override
    public ProductDto updateProduct(ProductDto dto) {
        return null;
    }

    @Override
    public boolean removeProductId(String productId) {
        return false;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return false;
    }

    @Override
    public ProductDto findProductById(String productId) {
        return null;
    }
}