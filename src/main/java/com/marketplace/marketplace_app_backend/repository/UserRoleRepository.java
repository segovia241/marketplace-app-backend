package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByBasicUserId(Long basicUserId);
    List<UserRole> findByRoleId(Long roleId);
    boolean existsByBasicUserIdAndRoleId(Long basicUserId, Long roleId);
}