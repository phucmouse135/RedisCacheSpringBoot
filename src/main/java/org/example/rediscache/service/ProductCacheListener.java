package org.example.rediscache.service;

import lombok.RequiredArgsConstructor;
import org.example.rediscache.event.ProductDeleteEvent;
import org.example.rediscache.event.ProductUpdateEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCacheListener {
    private final BasicRedisService basicRedisService;

    @EventListener
    @CacheEvict(
            value = "product",
            key = "#event.productId"
    )
    public void handleProductDeleteEvent(ProductDeleteEvent event) {
        // Cache eviction is handled by the @CacheEvict annotation
    }

    @EventListener
    @CacheEvict(
            value = "product",
            key = "#event.productId"
    )
    public void handleProductUpdateEvent(ProductUpdateEvent event) {
    }
}
