package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Product;
import com.marketplace.marketplace_app_backend.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return repository.findByCategoryCategoryId(categoryId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<Product> getProductsBySeller(@PathVariable Long sellerId) {
        return repository.findBySellerId(sellerId);
    }

    @GetMapping("/status/{status}")
    public List<Product> getProductsByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return repository.findByProductNameContainingIgnoreCase(name);
    }

    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return repository.findByPriceBetween(minPrice, maxPrice);
    }

    @GetMapping("/in-stock")
    public List<Product> getProductsInStock() {
        return repository.findByStockGreaterThan(0);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return repository.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return repository.findById(id)
                .map(product -> {
                    product.setProductName(updatedProduct.getProductName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setCategory(updatedProduct.getCategory());
                    product.setImages(updatedProduct.getImages());
                    product.setStock(updatedProduct.getStock());
                    product.setStatus(updatedProduct.getStatus());
                    product.setSeller(updatedProduct.getSeller());
                    return repository.save(product);
                })
                .orElseGet(() -> {
                    updatedProduct.setId(id);
                    return repository.save(updatedProduct);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        repository.deleteById(id);
    }
}