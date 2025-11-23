package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.Product;
import com.marketplace.marketplace_app_backend.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryCategoryId(Long categoryId);
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByStatus(ProductStatus status);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByStockGreaterThan(Integer stock);
    List<Product> findByStatusAndStockGreaterThan(ProductStatus status, Integer stock);

    // 1️⃣ Últimos productos (los más recientes)
    List<Product> findTop10ByOrderByCreatedAtDesc();

    // 2️⃣ Recomendados (aleatorios)
    @Query(value = "SELECT * FROM products ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Product> findRandomProducts();

    // 3️⃣ Stock bajo (<3)
    @Query(value = "SELECT * FROM products WHERE stock < 3 ORDER BY stock ASC LIMIT 10", nativeQuery = true)
    List<Product> findLowStock();
}
