package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final BasicUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(BasicUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public BasicUser registerUser(@RequestBody BasicUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }
}
