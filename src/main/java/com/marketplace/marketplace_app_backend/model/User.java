package com.marketplace.marketplace_app_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "basic_user_id", unique = true, nullable = false)
    private BasicUser basicUser;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private String phone;

    @Column(name = "verification_status")
    private Boolean verificationStatus = false;

    @Column
    private Double rating;

    private String dni;

    @Column(name = "dni_photo")
    private String dniPhoto;

    @Column(name = "dni_selfie")
    private String dniSelfie;

    private String ruc;

    // Nueva columna para la URL de la foto de perfil
    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BasicUser getBasicUser() { return basicUser; }
    public void setBasicUser(BasicUser basicUser) { this.basicUser = basicUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(Boolean verificationStatus) { this.verificationStatus = verificationStatus; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getDniPhoto() { return dniPhoto; }
    public void setDniPhoto(String dniPhoto) { this.dniPhoto = dniPhoto; }

    public String getDniSelfie() { return dniSelfie; }
    public void setDniSelfie(String dniSelfie) { this.dniSelfie = dniSelfie; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
