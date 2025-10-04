package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Cart;
import com.marketplace.marketplace_app_backend.repository.CartRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartRepository repository;

    public CartController(CartRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cart> getAllCarts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Cart> getCartById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/user/{userId}")
    public Optional<Cart> getCartByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping
    public Cart createCart(@RequestBody Cart cart) {
        return repository.save(cart);
    }

    @PutMapping("/{id}")
    public Cart updateCart(@PathVariable Long id, @RequestBody Cart updatedCart) {
        return repository.findById(id)
                .map(cart -> {
                    cart.setUser(updatedCart.getUser());
                    return repository.save(cart);
                })
                .orElseGet(() -> {
                    updatedCart.setId(id);
                    return repository.save(updatedCart);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Long id) {
        repository.deleteById(id);
    }
}