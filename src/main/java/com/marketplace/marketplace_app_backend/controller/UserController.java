package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/username/{username}")
    public Optional<User> getUserByUsername(@PathVariable String username) {
        return repository.findByUsername(username);
    }

    @GetMapping("/basic-user/{basicUserId}")
    public Optional<User> getUserByBasicUserId(@PathVariable Long basicUserId) {
        return repository.findByBasicUserId(basicUserId);
    }

    @GetMapping("/verified/{status}")
    public List<User> getUsersByVerificationStatus(@PathVariable Boolean status) {
        return repository.findByVerificationStatus(status);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return repository.findById(id)
                .map(user -> {
                    user.setUsername(updatedUser.getUsername());
                    user.setDisplayName(updatedUser.getDisplayName());
                    user.setPhone(updatedUser.getPhone());
                    user.setVerificationStatus(updatedUser.getVerificationStatus());
                    user.setRating(updatedUser.getRating());
                    user.setDni(updatedUser.getDni());
                    user.setDniPhoto(updatedUser.getDniPhoto());
                    user.setDniSelfie(updatedUser.getDniSelfie());
                    user.setRuc(updatedUser.getRuc());
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