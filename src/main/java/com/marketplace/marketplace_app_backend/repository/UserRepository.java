package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByBasicUserId(Long basicUserId);
    Optional<User> findByDni(String dni);
    Optional<User> findByRuc(String ruc);
    List<User> findByVerificationStatus(Boolean verificationStatus);
}