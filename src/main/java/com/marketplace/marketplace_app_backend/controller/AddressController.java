package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Address;
import com.marketplace.marketplace_app_backend.repository.AddressRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressRepository repository;

    public AddressController(AddressRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Address> getAllAddresses() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Address> getAddressById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Address> getAddressesByUser(@PathVariable Long userId) {
        return repository.findByUserId(userId);
    }

    @GetMapping("/user/{userId}/residence")
    public Optional<Address> getResidenceAddressByUser(@PathVariable Long userId) {
        return repository.findByUserIdAndIsResidenceTrue(userId);
    }

    @GetMapping("/location")
    public List<Address> getAddressesByLocation(
            @RequestParam String country,
            @RequestParam String departmentRegion,
            @RequestParam String province,
            @RequestParam String district) {
        return repository.findByCountryAndDepartmentRegionAndProvinceAndDistrict(
            country, departmentRegion, province, district);
    }

    @PostMapping
    public Address createAddress(@RequestBody Address address) {
        return repository.save(address);
    }

    @PutMapping("/{id}")
    public Address updateAddress(@PathVariable Long id, @RequestBody Address updatedAddress) {
        return repository.findById(id)
                .map(address -> {
                    address.setUser(updatedAddress.getUser());
                    address.setCountry(updatedAddress.getCountry());
                    address.setDepartmentRegion(updatedAddress.getDepartmentRegion());
                    address.setProvince(updatedAddress.getProvince());
                    address.setDistrict(updatedAddress.getDistrict());
                    address.setUrbanizationStreetNumber(updatedAddress.getUrbanizationStreetNumber());
                    address.setInteriorFloorApartment(updatedAddress.getInteriorFloorApartment());
                    address.setAdditionalReference(updatedAddress.getAdditionalReference());
                    address.setPostalCode(updatedAddress.getPostalCode());
                    address.setIsResidence(updatedAddress.getIsResidence());
                    return repository.save(address);
                })
                .orElseGet(() -> {
                    updatedAddress.setId(id);
                    return repository.save(updatedAddress);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable Long id) {
        repository.deleteById(id);
    }
}