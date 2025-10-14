package com.marketplace.marketplace_app_backend.config;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.model.Category;
import com.marketplace.marketplace_app_backend.model.Role;
import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.model.UserRole;
import com.marketplace.marketplace_app_backend.repository.BasicUserRepository;
import com.marketplace.marketplace_app_backend.repository.CategoryRepository;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import com.marketplace.marketplace_app_backend.repository.UserRepository;
import com.marketplace.marketplace_app_backend.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            CategoryRepository categoryRepository,
            RoleRepository roleRepository,
            BasicUserRepository basicUserRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // 1. Inicializar categorías
            initCategories(categoryRepository);
            
            // 2. Inicializar roles
            initRoles(roleRepository);
            
            // 3. Inicializar usuario admin
            initAdminUser(roleRepository, basicUserRepository, userRepository, userRoleRepository, passwordEncoder);
        };
    }

    private void initCategories(CategoryRepository categoryRepository) {
        String[] defaultCategories = {
            "Electrónica",
            "Ropa",
            "Hogar",
            "Juguetes",
            "Libros",
            "Deportes",
            "Otros"
        };

        for (String name : defaultCategories) {
            categoryRepository.findByName(name).orElseGet(() -> {
                Category category = new Category();
                category.setName(name);
                return categoryRepository.save(category);
            });
        }
        
        System.out.println("✅ Categorías por defecto inicializadas");
    }

    private void initRoles(RoleRepository roleRepository) {
        String[][] defaultRoles = {
            {"USER", "Usuario estándar"},
            {"ADMIN", "Administrador del sistema"}
        };

        for (String[] roleData : defaultRoles) {
            String name = roleData[0];
            
            roleRepository.findByName(name).orElseGet(() -> {
                Role role = new Role();
                role.setName(name);
                // Si tu modelo Role tiene campo description, agrégalo aquí
                return roleRepository.save(role);
            });
        }
        
        System.out.println("✅ Roles por defecto inicializados: USER, ADMIN");
    }

    private void initAdminUser(
            RoleRepository roleRepository,
            BasicUserRepository basicUserRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder) {
        
        // Verificar si ya existe el usuario admin
        if (basicUserRepository.findByEmail("admin@gmail.com").isEmpty()) {
            // 1. Crear BasicUser
            BasicUser basicUser = new BasicUser();
            basicUser.setEmail("admin@gmail.com");
            basicUser.setPassword(passwordEncoder.encode("admin"));
            BasicUser savedBasicUser = basicUserRepository.save(basicUser);

            // 2. Crear User (perfil extendido)
            User user = new User();
            user.setBasicUser(savedBasicUser);
            user.setUsername("admin");
            user.setDisplayName("Administrador");
            user.setVerificationStatus(true);
            user.setRating(5.0);

            // 3. Asignar rol ADMIN
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            UserRole userRole = new UserRole();
            userRole.setBasicUser(savedBasicUser);
            userRole.setRole(adminRole);
            userRoleRepository.save(userRole);

            System.out.println("✅ Usuario admin creado: admin@gmail.com / admin");
            System.out.println("   - Email: admin@gmail.com");
            System.out.println("   - Password: admin");
            System.out.println("   - Rol: ADMIN");
        } else {
            System.out.println("ℹ️  Usuario admin ya existe, omitiendo creación");
        }
    }
}