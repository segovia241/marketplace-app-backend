package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, 
                          BasicUserRepository basicUserRepository,
                          RoleRepository roleRepository,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ---------------------- GET POR USERNAME (DATOS PÚBLICOS) ----------------------
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Crear un nuevo objeto User solo con los datos públicos
        User publicUser = new User();
        publicUser.setId(user.getId());
        publicUser.setUsername(user.getUsername());
        publicUser.setDisplayName(user.getDisplayName());
        publicUser.setRating(user.getRating());
        publicUser.setVerificationStatus(user.getVerificationStatus());
        publicUser.setProfilePhotoUrl(user.getProfilePhotoUrl()); // <- Foto de perfil pública

        return publicUser;
    }

    // ---------------------- POST CREAR USUARIO ----------------------
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // ---------------------- GET USUARIO ACTUAL ----------------------
    @GetMapping("/me")
    public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.extractToken(authHeader);
        return jwtUtil.getUserFromToken(token);
    }

    // ---------------------- PUT ACTUALIZAR USUARIO ACTUAL ----------------------
    @PutMapping("/me")
    public User updateCurrentUser(@RequestHeader("Authorization") String authHeader,
                                  @RequestBody User updatedUser) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        if (updatedUser.getUsername() != null) currentUser.setUsername(updatedUser.getUsername());
        if (updatedUser.getDisplayName() != null) currentUser.setDisplayName(updatedUser.getDisplayName());
        if (updatedUser.getPhone() != null) currentUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getDni() != null) currentUser.setDni(updatedUser.getDni());
        if (updatedUser.getDniPhoto() != null) currentUser.setDniPhoto(updatedUser.getDniPhoto());
        if (updatedUser.getDniSelfie() != null) currentUser.setDniSelfie(updatedUser.getDniSelfie());
        if (updatedUser.getRuc() != null) currentUser.setRuc(updatedUser.getRuc());
        if (updatedUser.getProfilePhotoUrl() != null) currentUser.setProfilePhotoUrl(updatedUser.getProfilePhotoUrl()); // <- actualizar foto de perfil

        // 🔒 No permitir cambiar el rol desde aquí
        return userRepository.save(currentUser);
    }

}
