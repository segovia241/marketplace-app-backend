package com.marketplace.marketplace_app_backend.services;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import com.marketplace.marketplace_app_backend.model.Role;
import com.marketplace.marketplace_app_backend.model.UserRole;
import com.marketplace.marketplace_app_backend.repository.RoleRepository;
import com.marketplace.marketplace_app_backend.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    // Asignar rol automáticamente al crear usuario (por defecto "USER")
    public void assignDefaultRole(BasicUser basicUser) {
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol por defecto 'USER' no encontrado"));
        
        assignRoleToUser(basicUser, defaultRole);
    }

    // Asignar un rol específico a un usuario
    public void assignRoleToUser(BasicUser basicUser, Role role) {
        // Verificar si el usuario ya tiene este rol
        if (!userRoleRepository.existsByBasicUserIdAndRoleId(basicUser.getId(), role.getId())) {
            UserRole userRole = new UserRole();
            userRole.setBasicUser(basicUser);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    // Asignar rol por nombre de rol
    public void assignRoleToUser(BasicUser basicUser, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rol '" + roleName + "' no encontrado"));
        assignRoleToUser(basicUser, role);
    }

    // Obtener el rol principal de un usuario (primer rol encontrado)
    public Role getUserRole(Long basicUserId) {
        return userRoleRepository.findByBasicUserId(basicUserId)
                .stream()
                .findFirst()
                .map(UserRole::getRole)
                .orElseThrow(() -> new RuntimeException("Usuario no tiene roles asignados"));
    }

    // Obtener el nombre del rol principal de un usuario
    public String getUserRoleName(Long basicUserId) {
        return getUserRole(basicUserId).getName();
    }

    // Verificar si un usuario tiene un rol específico
    public boolean hasRole(Long basicUserId, String roleName) {
        return userRoleRepository.findByBasicUserId(basicUserId)
                .stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
    }

    // Crear y guardar un UserRole directamente (como el POST del controller)
    public UserRole createUserRole(UserRole userRole) {
        System.out.println("====================================================================");
        System.out.println("🚀 [INICIO DEL MÉTODO createUserRole] — INICIANDO PROCESO DE CREACIÓN DE USER ROLE");
        System.out.println("====================================================================");

        try {
            // Validar entrada
            if (userRole == null) {
                System.out.println("❌ [ERROR GRAVE] — EL OBJETO userRole ES NULO. NO SE PUEDE CONTINUAR.");
                throw new IllegalArgumentException("El parámetro userRole no puede ser nulo");
            }

            if (userRole.getBasicUser() == null) {
                System.out.println("❌ [ERROR] — EL CAMPO basicUser ES NULO EN EL OBJETO UserRole");
                throw new IllegalArgumentException("El UserRole debe incluir un usuario válido");
            }

            if (userRole.getRole() == null) {
                System.out.println("❌ [ERROR] — EL CAMPO role ES NULO EN EL OBJETO UserRole");
                throw new IllegalArgumentException("El UserRole debe incluir un rol válido");
            }

            System.out.println("✅ [OK] — Validaciones iniciales completadas correctamente");
            System.out.println("🧩 Datos del UserRole recibido:");
            System.out.println("   → BasicUser ID: " + userRole.getBasicUser().getId());
            System.out.println("   → Role ID: " + userRole.getRole().getId());
            System.out.println("   → Role Name: " + userRole.getRole().getName());

            // Verificar si ya existe la relación
            System.out.println("🔍 [CHECK] — Verificando si ya existe relación entre usuario y rol...");
            boolean exists = userRoleRepository.existsByBasicUserIdAndRoleId(
                    userRole.getBasicUser().getId(),
                    userRole.getRole().getId()
            );

            if (exists) {
                System.out.println("⚠️ [AVISO IMPORTANTE] — EL USUARIO YA TIENE ASIGNADO ESTE ROL. NO SE CREARÁ UNO NUEVO.");
                throw new RuntimeException("El usuario ya tiene asignado este rol");
            }

            // Guardar la relación
            System.out.println("💾 [GUARDANDO] — Intentando guardar el nuevo UserRole en la base de datos...");
            UserRole savedUserRole = userRoleRepository.save(userRole);

            System.out.println("✅ [ÉXITO TOTAL] — EL USER ROLE SE GUARDÓ CORRECTAMENTE EN LA BASE DE DATOS");
            System.out.println("🆔 ID generado: " + savedUserRole.getId());
            System.out.println("👤 Usuario ID: " + savedUserRole.getBasicUser().getId());
            System.out.println("🎭 Rol ID: " + savedUserRole.getRole().getId());
            System.out.println("====================================================================");
            System.out.println("🏁 [FIN EXITOSO DEL MÉTODO createUserRole] — TODO SALIÓ BIEN 🎉");
            System.out.println("====================================================================");

            return savedUserRole;

        } catch (Exception e) {
            System.out.println("====================================================================");
            System.out.println("💥 [ERROR EN MÉTODO createUserRole] — OCURRIÓ UNA EXCEPCIÓN DURANTE EL PROCESO");
            System.out.println("💣 DETALLES DEL ERROR: " + e.getMessage());
            System.out.println("====================================================================");
            throw e;
        }
    }


}