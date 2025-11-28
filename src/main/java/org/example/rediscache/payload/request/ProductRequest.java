package org.example.rediscache.payload.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String sku;
    private BigDecimal price;
    private String status;
    private String description;
    private String category;
}
