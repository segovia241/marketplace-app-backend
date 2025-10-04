package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByPaymentStatus(String paymentStatus);
    List<Order> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}