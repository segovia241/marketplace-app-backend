package com.marketplace.marketplace_app_backend.repository;

import com.marketplace.marketplace_app_backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    Optional<Address> findByUserIdAndIsResidenceTrue(Long userId);
    List<Address> findByCountryAndDepartmentRegionAndProvinceAndDistrict(
        String country, String departmentRegion, String province, String district);
}