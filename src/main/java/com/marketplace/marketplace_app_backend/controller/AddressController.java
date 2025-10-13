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
        
        // Si esta dirección es la residencia, quitar el estado de residencia de las demás
        if (Boolean.TRUE.equals(address.getIsResidence())) {
            removeResidenceFromOtherAddresses(currentUser.getId());
        }
        
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
                    // Si esta dirección se está estableciendo como residencia, quitar el estado de las demás
                    if (Boolean.TRUE.equals(updatedAddress.getIsResidence()) && 
                        !Boolean.TRUE.equals(addr.getIsResidence())) {
                        removeResidenceFromOtherAddresses(currentUser.getId());
                    }
                    
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
        
        // Si se eliminó la dirección que era residencia, podrías opcionalmente establecer otra como residencia
        // Esto es opcional, dependiendo de tu lógica de negocio
        if (Boolean.TRUE.equals(address.getIsResidence())) {
            // Opcional: establecer la primera dirección restante como residencia
            List<Address> remainingAddresses = addressRepository.findByUserId(currentUser.getId());
            if (!remainingAddresses.isEmpty()) {
                Address newResidence = remainingAddresses.get(0);
                newResidence.setIsResidence(true);
                addressRepository.save(newResidence);
            }
        }
    }
    
    /**
     * Método auxiliar para quitar el estado de residencia de todas las direcciones del usuario
     * excepto potencialmente la que se está creando/actualizando
     */
    private void removeResidenceFromOtherAddresses(Long userId) {
        List<Address> userAddresses = addressRepository.findByUserId(userId);
        for (Address addr : userAddresses) {
            if (Boolean.TRUE.equals(addr.getIsResidence())) {
                addr.setIsResidence(false);
                addressRepository.save(addr);
            }
        }
    }
}