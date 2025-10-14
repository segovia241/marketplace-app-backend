package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.model.Role;
import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.model.UserRole;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import com.marketplace.marketplace_app_backend.repository.UserRoleRepository;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;
import com.marketplace.marketplace_app_backend.services.UserRoleService;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRoleService userRoleService;
    private final BasicUserRepository basicUserRepository;
    private final JwtUtil jwtUtil;

    // ✅ Inyección de dependencias por constructor CORREGIDO
    public UserController(UserRepository userRepository,
                          BasicUserRepository basicUserRepository,
                          RoleRepository roleRepository,
                          UserRoleService userRoleService,
                          UserRoleRepository userRoleRepository,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.basicUserRepository = basicUserRepository;
        this.roleRepository = roleRepository;
        this.userRoleService = userRoleService;
        this.userRoleRepository = userRoleRepository;
        this.jwtUtil = jwtUtil;
    }

    // ---------------------- CREAR USUARIO CON VALIDACIONES ----------------------
    @PostMapping
    @Transactional
    public User createUser(@RequestBody User user) {
        System.out.println("🚀 [CREATE USER] Iniciando creación de usuario...");

        try {
            // --- 1️⃣ Validar username único ---
            List<User> allUsers = userRepository.findAll();
            boolean usernameExists = allUsers.stream()
                    .anyMatch(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(user.getUsername()));

            if (usernameExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ El nombre de usuario ya existe");
            }

            // --- 2️⃣ Validar BasicUser y email ---
            if (user.getBasicUser() == null || 
                user.getBasicUser().getEmail() == null || 
                user.getBasicUser().getEmail().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "❌ Debe proporcionar un email válido para BasicUser");
            }

            boolean emailExists = allUsers.stream()
                    .anyMatch(u -> u.getBasicUser() != null &&
                                u.getBasicUser().getEmail() != null &&
                                u.getBasicUser().getEmail().equalsIgnoreCase(user.getBasicUser().getEmail()));

            if (emailExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "❌ El email ya está registrado");
            }

            // --- 3️⃣ Guardar primero el BasicUser ---
            BasicUser basicUser = user.getBasicUser();
            BasicUser savedBasicUser = basicUserRepository.save(basicUser);
            System.out.println("✅ [CREATE USER] BasicUser guardado correctamente con ID: " + savedBasicUser.getId());

            // --- 4️⃣ Asociar BasicUser al User y guardar ---
            user.setBasicUser(savedBasicUser);
            User savedUser = userRepository.save(user);
            System.out.println("✅ [CREATE USER] Usuario guardado correctamente con ID: " + savedUser.getId());

            // --- 5️⃣ Asignar rol por defecto ---
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "❌ Rol 'USER' no encontrado en la base de datos"));

            UserRole userRole = new UserRole();
            userRole.setBasicUser(savedBasicUser);
            userRole.setRole(defaultRole);
            
            // 👇 GUARDAR Y VERIFICAR EXPLÍCITAMENTE
            UserRole savedUserRole = userRoleRepository.save(userRole);
            System.out.println("✅ [CREATE USER] Relación UserRole creada correctamente con ID: " + savedUserRole.getId() + " y rol: " + defaultRole.getName());

            // 👇 OPCIONAL: Verificar que realmente se guardó
            boolean roleExists = userRoleRepository.existsByBasicUserIdAndRoleId(savedBasicUser.getId(), defaultRole.getId());
            if (roleExists) {
                System.out.println("✅ [CREATE USER] Verificación: UserRole confirmado en base de datos");
            } else {
                System.out.println("⚠️ [CREATE USER] Advertencia: UserRole no se pudo verificar en base de datos");
            }

            return savedUser;

        } catch (ResponseStatusException ex) {
            System.err.println("⚠️ [VALIDATION ERROR] " + ex.getReason());
            throw ex;

        } catch (Exception e) {
            System.err.println("🔥 [CREATE USER ERROR] " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno al crear usuario", e);
        }
    }

    // ---------------------- 🔓 OBTENER TODOS LOS USUARIOS (SIN AUTENTICACIÓN) ----------------------
    @GetMapping
    public List<User> getAllUsers() {
        System.out.println("📢 [GET ALL USERS] Solicitud recibida para obtener todos los usuarios");

        try {
            List<User> users = userRepository.findAll();
            System.out.println("✅ [GET ALL USERS] Total de usuarios encontrados: " + users.size());
            return users;
        } catch (Exception e) {
            System.err.println("🔥 [GET ALL USERS ERROR] Error al obtener usuarios: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuarios", e);
        }
    }

    // ---------------------- OBTENER ROL DEL USUARIO ACTUAL ----------------------
    @GetMapping("/me/role")
    public String getCurrentUserRole(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = jwtUtil.extractToken(authHeader);
            User currentUser = jwtUtil.getUserFromToken(token);
            
            if (currentUser == null || currentUser.getBasicUser() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
            }
            
            return userRoleService.getUserRoleName(currentUser.getBasicUser().getId());
        } catch (Exception e) {
            System.err.println("🔥 [GET CURRENT USER ROLE ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener rol del usuario", e);
        }
    }

    // ---------------------- OBTENER ROL DE CUALQUIER USUARIO POR ID ----------------------
    @GetMapping("/{userId}/role")
    public String getUserRole(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            
            if (user.getBasicUser() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Usuario sin BasicUser asociado");
            }
            
            return userRoleService.getUserRoleName(user.getBasicUser().getId());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            System.err.println("🔥 [GET USER ROLE ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener rol del usuario", e);
        }
    }

    // ---------------------- OBTENER ROL POR USERNAME ----------------------
    @GetMapping("/username/{username}/role")
    public String getUserRoleByUsername(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
            
            if (user.getBasicUser() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Usuario sin BasicUser asociado");
            }
            
            return userRoleService.getUserRoleName(user.getBasicUser().getId());
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            System.err.println("🔥 [GET USER ROLE BY USERNAME ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener rol del usuario", e);
        }
    }

    // ---------------------- OBTENER USUARIO POR USERNAME ----------------------
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // Retornar solo información pública del usuario
            User publicUser = new User();
            publicUser.setId(user.getId());
            publicUser.setUsername(user.getUsername());
            publicUser.setDisplayName(user.getDisplayName());
            publicUser.setRating(user.getRating());
            publicUser.setVerificationStatus(user.getVerificationStatus());
            publicUser.setProfilePhotoUrl(user.getProfilePhotoUrl());

            return publicUser;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            System.err.println("🔥 [GET USER BY USERNAME ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuario", e);
        }
    }

    // ---------------------- OBTENER USUARIO ACTUAL ----------------------
    @GetMapping("/me")
    public Object getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extrae el token del header
            String token = jwtUtil.extractToken(authHeader);

            try {
                // Intenta obtener el usuario completo
                User currentUser = jwtUtil.getUserFromToken(token);
                if (currentUser == null) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
                }
                return currentUser;
            } catch (ResponseStatusException e) {
                // ⚠️ Si falla, intenta retornar solo el email desde el token
                System.err.println("⚠️ No se encontró el usuario, intentando extraer email...");
                String email = jwtUtil.extractEmail(token);

                return new Object() {
                    public final String message = "Usuario no encontrado, pero token válido";
                    public final String emailFromToken = email;
                };
            }

        } catch (Exception e) {
            System.err.println("🔥 [GET CURRENT USER ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener usuario actual", e);
        }
    }

    // ---------------------- ACTUALIZAR USUARIO ACTUAL ----------------------
    @PutMapping("/me")
    public User updateCurrentUser(@RequestHeader("Authorization") String authHeader,
                                  @RequestBody User updatedUser) {
        try {
            String token = jwtUtil.extractToken(authHeader);
            User currentUser = jwtUtil.getUserFromToken(token);

            if (currentUser == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
            }

            // Validar que el username no esté en uso por otro usuario
            if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(currentUser.getUsername())) {
                boolean usernameExists = userRepository.findByUsername(updatedUser.getUsername())
                        .isPresent();
                if (usernameExists) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está en uso");
                }
                currentUser.setUsername(updatedUser.getUsername());
            }

            // Actualizar campos permitidos
            if (updatedUser.getDisplayName() != null) currentUser.setDisplayName(updatedUser.getDisplayName());
            if (updatedUser.getPhone() != null) currentUser.setPhone(updatedUser.getPhone());
            if (updatedUser.getDni() != null) currentUser.setDni(updatedUser.getDni());
            if (updatedUser.getDniPhoto() != null) currentUser.setDniPhoto(updatedUser.getDniPhoto());
            if (updatedUser.getDniSelfie() != null) currentUser.setDniSelfie(updatedUser.getDniSelfie());
            if (updatedUser.getRuc() != null) currentUser.setRuc(updatedUser.getRuc());
            if (updatedUser.getProfilePhotoUrl() != null) currentUser.setProfilePhotoUrl(updatedUser.getProfilePhotoUrl());

            User savedUser = userRepository.save(currentUser);
            System.out.println("✅ [UPDATE USER] Usuario actualizado correctamente con ID: " + savedUser.getId());
            
            return savedUser;

        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            System.err.println("🔥 [UPDATE CURRENT USER ERROR] " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar usuario", e);
        }
    }
}