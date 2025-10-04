package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.OrderDelivery;
import com.marketplace.marketplace_app_backend.repository.OrderDeliveryRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-deliveries")
public class OrderDeliveryController {

    private final OrderDeliveryRepository repository;

    public OrderDeliveryController(OrderDeliveryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<OrderDelivery> getAllOrderDeliveries() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<OrderDelivery> getOrderDeliveryById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public Optional<OrderDelivery> getOrderDeliveryByOrder(@PathVariable Long orderId) {
        return repository.findByOrderId(orderId);
    }

    @GetMapping("/pickup-date-range")
    public List<OrderDelivery> getOrderDeliveriesByPickupDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return repository.findByPickupDateBetween(start, end);
    }

    @GetMapping("/delivery-date-range")
    public List<OrderDelivery> getOrderDeliveriesByDeliveryDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return repository.findByDeliveryDateBetween(start, end);
    }

    @PostMapping
    public OrderDelivery createOrderDelivery(@RequestBody OrderDelivery orderDelivery) {
        return repository.save(orderDelivery);
    }

    @PutMapping("/{id}")
    public OrderDelivery updateOrderDelivery(@PathVariable Long id, @RequestBody OrderDelivery updatedOrderDelivery) {
        return repository.findById(id)
                .map(orderDelivery -> {
                    orderDelivery.setOrder(updatedOrderDelivery.getOrder());
                    orderDelivery.setPickupDate(updatedOrderDelivery.getPickupDate());
                    orderDelivery.setDeliveryDate(updatedOrderDelivery.getDeliveryDate());
                    orderDelivery.setTimeRangeStart(updatedOrderDelivery.getTimeRangeStart());
                    orderDelivery.setTimeRangeEnd(updatedOrderDelivery.getTimeRangeEnd());
                    return repository.save(orderDelivery);
                })
                .orElseGet(() -> {
                    updatedOrderDelivery.setId(id);
                    return repository.save(updatedOrderDelivery);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteOrderDelivery(@PathVariable Long id) {
        repository.deleteById(id);
    }
}