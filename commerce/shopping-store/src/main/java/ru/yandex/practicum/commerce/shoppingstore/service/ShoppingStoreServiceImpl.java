package ru.yandex.practicum.commerce.shoppingstore.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.shoppingstore.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.shoppingstore.mapper.ProductMapper;
import ru.yandex.practicum.commerce.shoppingstore.model.Product;
import ru.yandex.practicum.commerce.shoppingstore.repository.ShoppingStoreRepository;
import shoppingstore.ProductCategory;
import shoppingstore.ProductDto;
import shoppingstore.ProductState;
import shoppingstore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    ShoppingStoreRepository repository;

    @Override
    public List<ProductDto> getProductByCategory(ProductCategory category, Pageable pageable) {
        log.info("Получение списка продуктов по категории: {}", category);
        Page<Product> products = repository.findByCategory(category, pageable);
        return products.map(ProductMapper::toProductDto).stream().toList();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        log.info("Создание продукта: {}", dto);
        ProductDto createdProduct = ProductMapper.toProductDto(repository.save(ProductMapper.toProduct(dto)));
        log.info("Продукт создан: {}", createdProduct);
        return createdProduct;
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto dto) {
        log.info("Обновление продукта с id: {}", dto.getProductId());
        Product oldProduct = repository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + dto.getProductId() + " не найден!",
                        new RuntimeException("Основная причина"), "Продукт не найден!", HttpStatus.NOT_FOUND));
        ProductDto updatedProduct = ProductMapper.toProductDto(repository.save(updateProductFromDto(oldProduct, dto)));
        log.info("Продукт обновлен: {}", updatedProduct);
        return updatedProduct;
    }

    @Override
    @Transactional
    public boolean removeByProductId(UUID productId) {
        log.info("Удаление продукта с id: {}", productId);
        Product removedProduct = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productId + " не найден!",
                        new RuntimeException("Основная причина"), "Продукт не найден!", HttpStatus.NOT_FOUND));
        removedProduct.setProductState(ProductState.DEACTIVATE);
        repository.save(removedProduct);
        log.info("Продукт с id {} был деактивирован", productId);
        return true;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("Изменение состояния количества продукта с id: {}", request.getProductId());
        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + request.getProductId() + " не найден!",
                        new RuntimeException("Основная причина"), "Продукт не найден!", HttpStatus.NOT_FOUND));
        product.setQuantityState(request.getQuantityState());
        log.info("Состояние количества продукта с id {} изменено на {}", request.getProductId(), request.getQuantityState());
        return true;
    }

    @Override
    public ProductDto findByProductId(UUID productId) {
        log.info("Поиск продукта с id: {}", productId);
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт с id" + productId + " не найден!",
                        new RuntimeException("Основная причина"), "Продукт не найден!", HttpStatus.NOT_FOUND));
        ProductDto foundProduct = ProductMapper.toProductDto(product);
        log.info("Продукт найден: {}", foundProduct);
        return foundProduct;
    }

    private Product updateProductFromDto(Product oldProduct, ProductDto dto) {
        oldProduct.setProductName(dto.getProductName());
        oldProduct.setImageSrc(dto.getImageSrc());
        oldProduct.setQuantityState(dto.getQuantityState());
        oldProduct.setCategory(dto.getProductCategory());
        oldProduct.setPrice(dto.getPrice());
        log.info("Обновлены данные продукта: {}", oldProduct);
        return oldProduct;
    }
}