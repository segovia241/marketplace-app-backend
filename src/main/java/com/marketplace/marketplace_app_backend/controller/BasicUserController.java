package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/basic_users")
public class BasicUserController {

    private final BasicUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public BasicUserController(BasicUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<BasicUser> getAllUsers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<BasicUser> getUserById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PutMapping("/{id}")
    public BasicUser updateUser(@PathVariable Long id, @RequestBody BasicUser updatedUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    return repository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
