package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    List<CartProduct> findByCartId(Long cartId);
    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteByCartIdAndProductId(Long cartId, Long productId);
    Integer countByCartId(Long cartId);
}