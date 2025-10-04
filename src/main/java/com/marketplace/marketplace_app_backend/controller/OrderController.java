package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Order;
import com.marketplace.marketplace_app_backend.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Order> getOrderById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @GetMapping("/status/{paymentStatus}")
    public List<Order> getOrdersByPaymentStatus(@PathVariable String paymentStatus) {
        return repository.findByPaymentStatus(paymentStatus);
    }

    @GetMapping("/date-range")
    public List<Order> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return repository.findByDateBetween(start, end);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return repository.save(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        return repository.findById(id)
                .map(order -> {
                    order.setUser(updatedOrder.getUser());
                    order.setShippingAddress(updatedOrder.getShippingAddress());
                    order.setPaymentMethod(updatedOrder.getPaymentMethod());
                    order.setTotalCost(updatedOrder.getTotalCost());
                    order.setDate(updatedOrder.getDate());
                    order.setPaymentStatus(updatedOrder.getPaymentStatus());
                    return repository.save(order);
                })
                .orElseGet(() -> {
                    updatedOrder.setId(id);
                    return repository.save(updatedOrder);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        repository.deleteById(id);
    }
}