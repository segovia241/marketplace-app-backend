package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final BasicUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginController(BasicUserRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Map<String, String> login(@RequestBody BasicUser user) {
        Optional<BasicUser> existingUser = repository.findByEmail(user.getEmail());
        if (existingUser.isPresent() && passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
            String token = jwtUtil.generateToken(existingUser.get().getEmail());
            return Collections.singletonMap("token", token); // Devuelve JSON {"token": "..."}
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    

}
