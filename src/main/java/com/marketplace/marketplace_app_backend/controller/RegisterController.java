package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final BasicUserRepository basicUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(BasicUserRepository basicUserRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.basicUserRepository = basicUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public User registerUser(@RequestBody RegisterRequest request) {

        // 1️⃣ Verificar que el email no exista
        Optional<BasicUser> existing = basicUserRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        // 2️⃣ Crear el BasicUser
        BasicUser basicUser = new BasicUser();
        basicUser.setEmail(request.getEmail());
        basicUser.setPassword(passwordEncoder.encode(request.getPassword()));
        BasicUser savedBasicUser;
        try {
            savedBasicUser = basicUserRepository.save(basicUser);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error al guardar el usuario básico. Posible duplicado.");
        }

        // 3️⃣ Crear el User asociado
        User user = new User();
        user.setBasicUser(savedBasicUser);

        // Generar o usar username
        String username = request.getUsername() != null ? request.getUsername() : generateUniqueUsername(savedBasicUser.getEmail());
        user.setUsername(username);

        // Generar displayName si no se proporciona
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : generateDisplayName(savedBasicUser.getEmail()));

        user.setPhone(request.getPhone()); // opcional
        user.setVerificationStatus(false); // siempre false al inicio
        user.setRating(0.0); // valor inicial
        user.setDni(request.getDni());
        user.setDniPhoto(request.getDniPhoto());
        user.setDniSelfie(request.getDniSelfie());
        user.setRuc(request.getRuc());

        return userRepository.save(user);
    }

    // Genera un username único
    private String generateUniqueUsername(String email) {
        String prefix = email.split("@")[0];
        String username = prefix;
        int attempt = 0;

        while (userRepository.findByUsername(username).isPresent()) {
            attempt++;
            username = prefix + System.currentTimeMillis() % 10000 + attempt;
        }
        return username;
    }

    // Genera un displayName simple
    private String generateDisplayName(String email) {
        String prefix = email.split("@")[0];
        return prefix.substring(0, 1).toUpperCase() + prefix.substring(1);
    }

    // DTO para recibir los datos combinados
    public static class RegisterRequest {
        private String email;
        private String password;

        // Datos opcionales de User
        private String username;
        private String displayName;
        private String phone;
        private String dni;
        private String dniPhoto;
        private String dniSelfie;
        private String ruc;

        // Getters y setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getDni() { return dni; }
        public void setDni(String dni) { this.dni = dni; }
        public String getDniPhoto() { return dniPhoto; }
        public void setDniPhoto(String dniPhoto) { this.dniPhoto = dniPhoto; }
        public String getDniSelfie() { return dniSelfie; }
        public void setDniSelfie(String dniSelfie) { this.dniSelfie = dniSelfie; }
        public String getRuc() { return ruc; }
        public void setRuc(String ruc) { this.ruc = ruc; }
    }
}
