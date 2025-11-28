package org.example.rediscache.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dùng để test tìm kiếm text (Cache Condition: chỉ cache nếu tên dài > 3 ký tự)
    @Column(nullable = false)
    private String name;

    // Mã SKU (Dùng để test cache theo key khác ngoài ID)
    @Column(unique = true)
    private String sku;

    // Giá tiền (Dùng để test: Chỉ cache hàng cao cấp giá > 10 triệu)
    private BigDecimal price;

    // Trạng thái: ACTIVE / INACTIVE / OUT_OF_STOCK
    // Dùng để test: Không bao giờ cache sản phẩm INACTIVE
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    // Mô tả rất dài (TEXT). 
    // Đây là lý do chính để Cache: Query trường này tốn I/O DB nhất.
    @Column(columnDefinition = "TEXT")
    private String description;

    // Danh mục (Dùng để test @CacheEvict theo group)
    private String category;

    // Đánh dấu thời gian cập nhật để kiểm tra tính tươi mới của dữ liệu
    private LocalDateTime updatedAt;
}

enum ProductStatus {
    ACTIVE, INACTIVE, OUT_OF_STOCK
}