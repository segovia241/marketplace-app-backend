package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Role;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository repository;

    public RoleController(RoleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Role> getAllRoles() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Role> getRoleById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Role> getRoleByName(@PathVariable String name) {
        return repository.findByName(name);
    }

    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return repository.save(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return repository.findById(id)
                .map(role -> {
                    role.setName(updatedRole.getName());
                    return repository.save(role);
                })
                .orElseGet(() -> {
                    updatedRole.setId(id);
                    return repository.save(updatedRole);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        repository.deleteById(id);
    }
}