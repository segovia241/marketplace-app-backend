package com.marketplace.marketplace_app_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "order_delivery")
public class OrderDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "pickup_date")
    private LocalDateTime pickupDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    @Column(name = "time_range_start")
    private LocalTime timeRangeStart;

    @Column(name = "time_range_end")
    private LocalTime timeRangeEnd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public LocalDateTime getPickupDate() { return pickupDate; }
    public void setPickupDate(LocalDateTime pickupDate) { this.pickupDate = pickupDate; }

    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDateTime deliveryDate) { this.deliveryDate = deliveryDate; }

    public LocalTime getTimeRangeStart() { return timeRangeStart; }
    public void setTimeRangeStart(LocalTime timeRangeStart) { this.timeRangeStart = timeRangeStart; }

    public LocalTime getTimeRangeEnd() { return timeRangeEnd; }
    public void setTimeRangeEnd(LocalTime timeRangeEnd) { this.timeRangeEnd = timeRangeEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}