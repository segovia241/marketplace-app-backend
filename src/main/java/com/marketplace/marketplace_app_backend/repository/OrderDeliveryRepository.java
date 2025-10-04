package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.OrderDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {
    Optional<OrderDelivery> findByOrderId(Long orderId);
    List<OrderDelivery> findByPickupDateBetween(LocalDateTime start, LocalDateTime end);
    List<OrderDelivery> findByDeliveryDateBetween(LocalDateTime start, LocalDateTime end);
}