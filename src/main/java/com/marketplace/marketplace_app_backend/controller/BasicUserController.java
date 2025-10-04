package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/basic_users")
public class BasicUserController {

    private final BasicUserRepository repository;

    public BasicUserController(BasicUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<BasicUser> getAllUsers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<BasicUser> getUserById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping
    public BasicUser createUser(@RequestBody BasicUser user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public BasicUser updateUser(@PathVariable Long id, @RequestBody BasicUser updatedUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setEmail(updatedUser.getEmail());
                    user.setPassword(updatedUser.getPassword());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    updatedUser.setId(id);
                    return repository.save(updatedUser);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
