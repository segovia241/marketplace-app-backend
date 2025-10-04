package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

    // ---------------------- GET TODOS ----------------------
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ---------------------- GET POR ID ----------------------
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
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

        return publicUser;
    }



    // ---------------------- GET POR BASIC USER ----------------------
    @GetMapping("/basic-user/{basicUserId}")
    public Optional<User> getUserByBasicUserId(@PathVariable Long basicUserId) {
        return userRepository.findByBasicUserId(basicUserId);
    }

    // ---------------------- GET POR VERIFICACIÓN ----------------------
    @GetMapping("/verified/{status}")
    public List<User> getUsersByVerificationStatus(@PathVariable Boolean status) {
        return userRepository.findByVerificationStatus(status);
    }

    // ---------------------- POST CREAR USUARIO ----------------------
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // ---------------------- PUT ACTUALIZAR USUARIO ----------------------
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
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

                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // ---------------------- DELETE ELIMINAR USUARIO ----------------------
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
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

        // 🔒 No permitir cambiar el rol desde aquí
        return userRepository.save(currentUser);
    }

}
