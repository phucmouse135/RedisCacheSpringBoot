package org.example.rediscache.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rediscache.payload.dto.ProductResponse;
import org.example.rediscache.payload.request.ProductRequest;
import org.example.rediscache.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest) {
        log.info("Received request to add product: {}", productRequest);
        ProductResponse createdProduct = productService.createProduct(productRequest);
        log.info("Product created successfully: {}", createdProduct);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        log.info("Received request to update product with id {}: {}", id, productRequest);
        ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
        log.info("Product updated successfully: {}", updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Received request to delete product with id {}", id);
        productService.deleteProduct(id);
        log.info("Product with id {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Received request to get product with id {}", id);
        ProductResponse product = productService.getProductById(id);
        log.info("Product retrieved successfully: {}", product);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    public ResponseEntity<java.util.List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.info("Received request to search products with keyword: {}", keyword);
        java.util.List<ProductResponse> products = productService.getAllProductsBykeyword(keyword);
        log.info("Products retrieved successfully: {}", products);
        return ResponseEntity.ok(products);
    }
}
