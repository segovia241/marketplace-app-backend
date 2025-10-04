package com.marketplace.marketplace_app_backend.controller;

import com.marketplace.marketplace_app_backend.model.Address;
import com.marketplace.marketplace_app_backend.model.User;
import com.marketplace.marketplace_app_backend.repository.AddressRepository;
import com.marketplace.marketplace_app_backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;

    public AddressController(AddressRepository addressRepository, JwtUtil jwtUtil) {
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public List<Address> getMyAddresses(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);
        return addressRepository.findByUserId(currentUser.getId());
    }

    @PostMapping("/me")
    public Address createMyAddress(@RequestHeader("Authorization") String authHeader,
                                   @RequestBody Address address) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);
        address.setUser(currentUser);
        return addressRepository.save(address);
    }

    @PutMapping("/me/{id}")
    public Address updateMyAddress(@RequestHeader("Authorization") String authHeader,
                                   @PathVariable Long id,
                                   @RequestBody Address updatedAddress) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        return addressRepository.findById(id)
                .filter(addr -> addr.getUser().getId().equals(currentUser.getId()))
                .map(addr -> {
                    addr.setCountry(updatedAddress.getCountry());
                    addr.setDepartmentRegion(updatedAddress.getDepartmentRegion());
                    addr.setProvince(updatedAddress.getProvince());
                    addr.setDistrict(updatedAddress.getDistrict());
                    addr.setUrbanizationStreetNumber(updatedAddress.getUrbanizationStreetNumber());
                    addr.setInteriorFloorApartment(updatedAddress.getInteriorFloorApartment());
                    addr.setAdditionalReference(updatedAddress.getAdditionalReference());
                    addr.setPostalCode(updatedAddress.getPostalCode());
                    addr.setIsResidence(updatedAddress.getIsResidence());
                    return addressRepository.save(addr);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada o no pertenece al usuario"));
    }

    @DeleteMapping("/me/{id}")
    public void deleteMyAddress(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Long id) {
        String token = jwtUtil.extractToken(authHeader);
        User currentUser = jwtUtil.getUserFromToken(token);

        Address address = addressRepository.findById(id)
                .filter(addr -> addr.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada o no pertenece al usuario"));

        addressRepository.delete(address);
    }
}
