package org.example.rediscache.payload.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String status;
    private String description;
    private String category;
    private LocalDateTime updatedAt;
}
