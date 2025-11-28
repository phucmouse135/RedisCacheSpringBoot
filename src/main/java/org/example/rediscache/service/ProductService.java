package org.example.rediscache.service;

import org.example.rediscache.payload.dto.ProductResponse;
import org.example.rediscache.payload.request.ProductRequest;

import java.util.List;

public interface ProductService {
    ProductResponse getProductById(Long id);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    List<ProductResponse> getAllProductsBykeyword(String keyword);


    ProductResponse getProductBySku(String sku);

}
