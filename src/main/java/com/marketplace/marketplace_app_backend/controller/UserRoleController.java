package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.UserRole;
import com.marketplace.marketplace_app_backend.repository.UserRoleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-roles")
public class UserRoleController {

    private final UserRoleRepository repository;

    public UserRoleController(UserRoleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UserRole> getAllUserRoles() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<UserRole> getUserRoleById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/user/{basicUserId}")
    public List<UserRole> getUserRolesByUser(@PathVariable Long basicUserId) {
        return repository.findByBasicUserId(basicUserId);
    }

    @GetMapping("/role/{roleId}")
    public List<UserRole> getUserRolesByRole(@PathVariable Long roleId) {
        return repository.findByRoleId(roleId);
    }

    @GetMapping("/exists")
    public boolean checkUserRoleExists(
            @RequestParam Long basicUserId,
            @RequestParam Long roleId) {
        return repository.existsByBasicUserIdAndRoleId(basicUserId, roleId);
    }

    @PostMapping
    public UserRole createUserRole(@RequestBody UserRole userRole) {
        return repository.save(userRole);
    }

    @PutMapping("/{id}")
    public UserRole updateUserRole(@PathVariable Long id, @RequestBody UserRole updatedUserRole) {
        return repository.findById(id)
                .map(userRole -> {
                    userRole.setBasicUser(updatedUserRole.getBasicUser());
                    userRole.setRole(updatedUserRole.getRole());
                    return repository.save(userRole);
                })
                .orElseGet(() -> {
                    updatedUserRole.setId(id);
                    return repository.save(updatedUserRole);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteUserRole(@PathVariable Long id) {
        repository.deleteById(id);
    }
}