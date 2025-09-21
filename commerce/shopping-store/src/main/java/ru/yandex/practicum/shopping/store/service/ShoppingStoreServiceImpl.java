package ru.yandex.practicum.shopping.store.service;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.store.ProductCategory;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.dto.store.ProductState;
import ru.yandex.practicum.interaction.api.dto.store.QuantityState;
import ru.yandex.practicum.interaction.api.exception.ProductNotFoundException;
import ru.yandex.practicum.shopping.store.mapper.ProductMapper;
import ru.yandex.practicum.shopping.store.model.Product;
import ru.yandex.practicum.shopping.store.repository.ShoppingStoreRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    ShoppingStoreRepository repository;
    ProductMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = repository.findAllByProductCategory(category, pageable);

        return mapper.toDtoPage(products);
    }

    @Override
    public ProductDto createProduct(@Valid ProductDto newProductDto) {
        Product product = mapper.toEntity(newProductDto);

        return mapper.toDto(repository.save(product));
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto updateProductDto) {
        Product product = validateProductExist(updateProductDto.getProductId());
        mapper.updateFromDto(updateProductDto, product);

        return mapper.toDto(repository.save(product));
    }

    @Override
    public boolean deleteProduct(UUID productId) {
        Product product = validateProductExist(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Override
    public boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        if (productId == null) {
            throw new IllegalArgumentException("productId не может быть null");
        }
        if (quantityState == null) {
            throw new IllegalArgumentException("quantityState не может быть null");
        }

        Product product = validateProductExist(productId);
        product.setQuantityState(quantityState);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = validateProductExist(productId);
        return mapper.toDto(product);
    }

    private Product validateProductExist(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Товар c id = %s не найден",
                        productId)));
    }
}