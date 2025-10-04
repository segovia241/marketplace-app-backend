package com.marketplace.marketplace_app_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String country = "Peru";

    @Column(name = "department_region", nullable = false)
    private String departmentRegion;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String district;

    @Column(name = "urbanization_street_number", nullable = false)
    private String urbanizationStreetNumber;

    @Column(name = "interior_floor_apartment")
    private String interiorFloorApartment;

    @Column(name = "additional_reference")
    private String additionalReference;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "is_residence")
    private Boolean isResidence = false;

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDepartmentRegion() { return departmentRegion; }
    public void setDepartmentRegion(String departmentRegion) { this.departmentRegion = departmentRegion; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getUrbanizationStreetNumber() { return urbanizationStreetNumber; }
    public void setUrbanizationStreetNumber(String urbanizationStreetNumber) { this.urbanizationStreetNumber = urbanizationStreetNumber; }

    public String getInteriorFloorApartment() { return interiorFloorApartment; }
    public void setInteriorFloorApartment(String interiorFloorApartment) { this.interiorFloorApartment = interiorFloorApartment; }

    public String getAdditionalReference() { return additionalReference; }
    public void setAdditionalReference(String additionalReference) { this.additionalReference = additionalReference; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Boolean getIsResidence() { return isResidence; }
    public void setIsResidence(Boolean isResidence) { this.isResidence = isResidence; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}