package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.BasicUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {
    
    // Buscar usuario por email (único)
    Optional<BasicUser> findByEmail(String email);
    
    // Verificar si existe un usuario con el email
    boolean existsByEmail(String email);
    
    // Buscar usuarios creados después de una fecha específica
    List<BasicUser> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    // Buscar usuarios creados antes de una fecha específica
    List<BasicUser> findByCreatedAtBefore(java.time.LocalDateTime date);
    
    // Buscar usuarios por rango de fechas de creación
    List<BasicUser> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    // Contar usuarios por fecha de creación
    Long countByCreatedAtAfter(java.time.LocalDateTime date);
    
    // Buscar usuarios que contengan cierto texto en el email (búsqueda parcial)
    List<BasicUser> findByEmailContainingIgnoreCase(String email);
    
    // Buscar usuarios cuyo email comience con cierto texto
    List<BasicUser> findByEmailStartingWith(String prefix);
    
    // Buscar usuarios cuyo email termine con cierto texto
    List<BasicUser> findByEmailEndingWith(String suffix);
    
    // Consulta personalizada para buscar usuarios con emails de un dominio específico
    @Query("SELECT bu FROM BasicUser bu WHERE bu.email LIKE %:domain")
    List<BasicUser> findByEmailDomain(@Param("domain") String domain);
    
    // Consulta personalizada para obtener usuarios ordenados por fecha de creación (más recientes primero)
    @Query("SELECT bu FROM BasicUser bu ORDER BY bu.createdAt DESC")
    List<BasicUser> findAllOrderByCreatedAtDesc();
    
    // Consulta personalizada para obtener usuarios ordenados por fecha de creación (más antiguos primero)
    @Query("SELECT bu FROM BasicUser bu ORDER BY bu.createdAt ASC")
    List<BasicUser> findAllOrderByCreatedAtAsc();
    
    // Consulta personalizada para contar usuarios por mes y año
    @Query("SELECT COUNT(bu) FROM BasicUser bu WHERE YEAR(bu.createdAt) = :year AND MONTH(bu.createdAt) = :month")
    Long countByMonthAndYear(@Param("month") int month, @Param("year") int year);
    
    // Consulta personalizada para obtener los últimos N usuarios registrados
    @Query(value = "SELECT * FROM basic_users ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<BasicUser> findLatestUsers(@Param("limit") int limit);
    
    // Consulta personalizada para verificar si existe algún usuario con el mismo email (excluyendo un ID específico)
    @Query("SELECT CASE WHEN COUNT(bu) > 0 THEN true ELSE false END FROM BasicUser bu WHERE bu.email = :email AND bu.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    // Consulta personalizada para buscar usuarios y sus detalles completos (si necesitas joins en el futuro)
    // @Query("SELECT bu, u FROM BasicUser bu LEFT JOIN User u ON bu.id = u.basicUser.id WHERE bu.id = :id")
    // Optional<Object[]> findBasicUserWithDetails(@Param("id") Long id);
}