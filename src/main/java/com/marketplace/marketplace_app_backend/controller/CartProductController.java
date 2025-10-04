package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.CartProduct;
import com.marketplace.marketplace_app_backend.repository.CartProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart-products")
public class CartProductController {

    private final CartProductRepository repository;

    public CartProductController(CartProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<CartProduct> getAllCartProducts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<CartProduct> getCartProductById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/cart/{cartId}")
    public List<CartProduct> getCartProductsByCart(@PathVariable Long cartId) {
        return repository.findByCartId(cartId);
    }

    @GetMapping("/cart/{cartId}/count")
    public Integer getCartItemsCount(@PathVariable Long cartId) {
        return repository.countByCartId(cartId);
    }

    @GetMapping("/cart/{cartId}/product/{productId}")
    public Optional<CartProduct> getCartProductByCartAndProduct(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        return repository.findByCartIdAndProductId(cartId, productId);
    }

    @PostMapping
    public CartProduct createCartProduct(@RequestBody CartProduct cartProduct) {
        return repository.save(cartProduct);
    }

    @PutMapping("/{id}")
    public CartProduct updateCartProduct(@PathVariable Long id, @RequestBody CartProduct updatedCartProduct) {
        return repository.findById(id)
                .map(cartProduct -> {
                    cartProduct.setCart(updatedCartProduct.getCart());
                    cartProduct.setProduct(updatedCartProduct.getProduct());
                    cartProduct.setQuantity(updatedCartProduct.getQuantity());
                    return repository.save(cartProduct);
                })
                .orElseGet(() -> {
                    updatedCartProduct.setId(id);
                    return repository.save(updatedCartProduct);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteCartProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public void deleteCartProductByCartAndProduct(
            @PathVariable Long cartId,
            @PathVariable Long productId) {
        repository.deleteByCartIdAndProductId(cartId, productId);
    }
}