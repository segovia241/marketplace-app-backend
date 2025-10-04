package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrderId(Long orderId);
    List<OrderProduct> findByProductId(Long productId);
}