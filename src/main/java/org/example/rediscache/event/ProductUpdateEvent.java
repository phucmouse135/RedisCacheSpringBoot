package org.example.rediscache.event;

import org.springframework.context.ApplicationEvent;

public class ProductUpdateEvent extends ApplicationEvent {
    private final Long productId;

    public ProductUpdateEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
