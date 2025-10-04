package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.OrderProduct;
import com.marketplace.marketplace_app_backend.repository.OrderProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order-products")
public class OrderProductController {

    private final OrderProductRepository repository;

    public OrderProductController(OrderProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<OrderProduct> getAllOrderProducts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<OrderProduct> getOrderProductById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderProduct> getOrderProductsByOrder(@PathVariable Long orderId) {
        return repository.findByOrderId(orderId);
    }

    @GetMapping("/product/{productId}")
    public List<OrderProduct> getOrderProductsByProduct(@PathVariable Long productId) {
        return repository.findByProductId(productId);
    }

    @PostMapping
    public OrderProduct createOrderProduct(@RequestBody OrderProduct orderProduct) {
        return repository.save(orderProduct);
    }

    @PutMapping("/{id}")
    public OrderProduct updateOrderProduct(@PathVariable Long id, @RequestBody OrderProduct updatedOrderProduct) {
        return repository.findById(id)
                .map(orderProduct -> {
                    orderProduct.setOrder(updatedOrderProduct.getOrder());
                    orderProduct.setProduct(updatedOrderProduct.getProduct());
                    orderProduct.setQuantity(updatedOrderProduct.getQuantity());
                    return repository.save(orderProduct);
                })
                .orElseGet(() -> {
                    updatedOrderProduct.setId(id);
                    return repository.save(updatedOrderProduct);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteOrderProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }
}