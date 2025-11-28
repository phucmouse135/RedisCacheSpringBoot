package org.example.rediscache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rediscache.model.Product;
import org.example.rediscache.payload.dto.ProductResponse;
import org.example.rediscache.payload.request.ProductRequest;
import org.example.rediscache.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;


    @Override
    @Cacheable(
            value = "products",
            key = "#id",
            unless = "#result == null"
    )
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id {} from database", id);
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = mapToProductEntity(productRequest);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @CachePut(
            value = "products",
            key = "#id",
            unless = "#result == null"
    )
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        product.setName(productRequest.getName());
        product.setSku(productRequest.getSku());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @CacheEvict(
            value = "products",
            key = "#id"
    )
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Cacheable(
            value = "product_search",
            key = "#keyword",
            condition = "#keyword.length() >= 3",
            unless = "#result.isEmpty()"
    )
    public List<ProductResponse> getAllProductsBykeyword(String keyword) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductBySku(String sku) {
        return null;
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    private Product mapToProductEntity(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.getName())
                .sku(productRequest.getSku())
                .price(productRequest.getPrice())
                .description(productRequest.getDescription())
                .build();
    }
}
