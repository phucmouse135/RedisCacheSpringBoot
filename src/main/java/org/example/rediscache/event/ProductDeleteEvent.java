package org.example.rediscache.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProductDeleteEvent extends ApplicationEvent {
    private final Long productId;

    public ProductDeleteEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }
}
