package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.Product;
import com.marketplace.marketplace_app_backend.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryCategoryId(Long categoryId);
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByStockGreaterThan(Integer stock);
    List<Product> findByStatusAndStockGreaterThan(ProductStatus status, Integer stock);
}
