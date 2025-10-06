package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Product;
import com.marketplace.marketplace_app_backend.model.ProductStatus;
import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.ProductRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;
    private final JwtUtil jwtUtil;

    public ProductController(ProductRepository repository, JwtUtil jwtUtil) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    // ---------------------- TODOS LOS PRODUCTOS (TEST) ----------------------
    @GetMapping
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // ---------------------- PRODUCTOS ACTIVOS CON STOCK ----------------------
    @GetMapping("/active-in-stock")
    public List<Product> getActiveProductsInStock() {
        return repository.findByStatusAndStockGreaterThan(ProductStatus.ACTIVE, 0);
    }

    // ---------------------- CREAR NUEVO PRODUCTO ----------------------
    @PostMapping
    public Product createProduct(@RequestHeader("Authorization") String authHeader,
                                 @RequestBody Product newProduct) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        // Asignar el usuario autenticado como vendedor
        newProduct.setSeller(currentUser);
        
        // Establecer estado por defecto si no se proporciona
        if (newProduct.getStatus() == null) {
            newProduct.setStatus(ProductStatus.ACTIVE);
        }
        
        // Validar stock mínimo
        if (newProduct.getStock() == null || newProduct.getStock() < 0) {
            newProduct.setStock(1);
        }

        return repository.save(newProduct);
    }

    // ---------------------- PRODUCTOS DEL USUARIO ACTUAL ----------------------
    @GetMapping("/me")
    public List<Product> getMyProducts(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);
        return repository.findBySellerId(currentUser.getId());
    }

    // ---------------------- ACTUALIZAR PRODUCTO DEL USUARIO ACTUAL ----------------------
    @PutMapping("/me/{productId}")
    public Product updateMyProduct(@RequestHeader("Authorization") String authHeader,
                                   @PathVariable Long productId,
                                   @RequestBody Product updatedProduct) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes editar productos de otros usuarios");
        }

        // 🔒 No se puede cambiar id ni seller
        product.setProductName(updatedProduct.getProductName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setImages(updatedProduct.getImages());
        product.setStock(updatedProduct.getStock());
        product.setStatus(updatedProduct.getStatus());

        return repository.save(product);
    }

    // ---------------------- ELIMINAR PRODUCTO DEL USUARIO ACTUAL ----------------------
    @DeleteMapping("/me/{productId}")
    public void deleteMyProduct(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Long productId) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar productos de otros usuarios");
        }

        repository.delete(product);
    }
}