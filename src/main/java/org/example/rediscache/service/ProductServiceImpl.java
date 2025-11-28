package org.example.rediscache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rediscache.event.ProductDeleteEvent;
import org.example.rediscache.event.ProductUpdateEvent;
import org.example.rediscache.model.Product;
import org.example.rediscache.payload.dto.ProductResponse;
import org.example.rediscache.payload.request.ProductRequest;
import org.example.rediscache.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final BasicRedisService basicRedisService;


    @Override
    @Cacheable(
            value = "product",
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

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = mapToProductEntity(productRequest);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
//    @CacheEvict(
//            value = "product",
//            key = "#id"
//    )
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        String key = "products_" + id;
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return null;
        }
        applicationEventPublisher.publishEvent(new ProductUpdateEvent(this, id));
        product.setName(productRequest.getName());
        product.setSku(productRequest.getSku());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        Product updatedProduct = productRepository.save(product);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        basicRedisService.set(key , mapToProductResponse(updatedProduct), 10L, java.util.concurrent.TimeUnit.MINUTES);
        applicationEventPublisher.publishEvent(new ProductUpdateEvent(this, id));
        return mapToProductResponse(updatedProduct);
    }

    @Override
//    @CacheEvict(
//            value = "products",
//            key = "#id"
//    )
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id {} from database", id);
        productRepository.deleteById(id);
        applicationEventPublisher.publishEvent(new ProductDeleteEvent(this, id));
    }

    @Override
//    @Cacheable(
//            value = "product_search",
//            key = "#keyword",
//            condition = "#keyword.length() >= 3",
//            unless = "#result.isEmpty()"
//    )
    public List<ProductResponse> getAllProductsBykeyword(String keyword) {
        String key = "product_search_" + keyword;
        List<ProductResponse> cachedProducts = basicRedisService.getList(key, ProductResponse.class);
        if (cachedProducts != null) {
            log.info("Fetching products with keyword '{}' from cache", keyword);
            return cachedProducts;
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        List<ProductResponse> productResponseList = products.stream().map(this::mapToProductResponse).toList();
        basicRedisService.set(key, productResponseList, 10L, java.util.concurrent.TimeUnit.MINUTES);
        log.info("Fetching products with keyword '{}' from database", keyword);
        return productResponseList;
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
