package com.mentorship.hanakoleh.domain.user.controller;

import com.mentorship.hanakoleh.domain.user.dto.AddressDetailsDto;
import com.mentorship.hanakoleh.domain.user.dto.CreateAddressRequest;
import com.mentorship.hanakoleh.domain.user.dto.GetCustomerAddressesResponse;
import com.mentorship.hanakoleh.domain.user.dto.UpdateAddressRequest;
import com.mentorship.hanakoleh.domain.user.mapper.AddressMapper;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.service.AddressService;
import com.mentorship.hanakoleh.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/me/addresses")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing authenticated user's addresses")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;
    private final AddressMapper addressMapper;

    private static URI getNewResourceLocation(String pathPlaceholder, Object newResourceId) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(pathPlaceholder)
                .buildAndExpand(newResourceId)
                .toUri();
    }

    @PostMapping
    @Operation(summary = "Create a new address for the authenticated user")
    public ResponseEntity<AddressDetailsDto> createAddress(@AuthenticationPrincipal UserPrincipal currentUser,
                                                           @Valid @RequestBody CreateAddressRequest request) {
        Integer customerId = currentUser.getId();
        Address createdAddress = addressService.createAddress(customerId, request);
        AddressDetailsDto dto = addressMapper.toDetailsDto(createdAddress);

        URI location = getNewResourceLocation("/{addressId}", createdAddress.getId());
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<AddressDetailsDto> getAddressById(@AuthenticationPrincipal UserPrincipal currentUser,
                                                            @PathVariable Long addressId) {
        Integer customerId = currentUser.getId();
        Address address = addressService.getAddressOrThrow(addressId, customerId);
        AddressDetailsDto response = addressMapper.toDetailsDto(address);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieve all addresses for the authenticated user")
    public ResponseEntity<GetCustomerAddressesResponse> getAllAddresses(@AuthenticationPrincipal UserPrincipal currentUser) {
        Integer customerId = currentUser.getId();
        List<Address> addresses = addressService.getAllAddressesByCustomerId(customerId);
        GetCustomerAddressesResponse response = addressMapper.toGetCustomerAddressesResponse(addresses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<AddressDetailsDto> updateAddress(@AuthenticationPrincipal UserPrincipal currentUser,
                                                           @PathVariable Long addressId, @Valid @RequestBody UpdateAddressRequest request) {
        Integer customerId = currentUser.getId();
        Address updatedAddress = addressService.updateAddress(addressId, customerId, request);
        AddressDetailsDto dto = addressMapper.toDetailsDto(updatedAddress);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address", description = "Delete an address by its ID")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserPrincipal currentUser,
                                              @PathVariable Long addressId) {
        Integer customerId = currentUser.getId();
        addressService.deleteAddress(addressId, customerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    @Operation(summary = "Set default address for the authenticated user")
    public ResponseEntity<Void> setDefaultAddress(@AuthenticationPrincipal UserPrincipal currentUser,
                                                  @PathVariable Long addressId) {
        Integer customerId = currentUser.getId();
        addressService.setDefaultAddress(addressId, customerId);
        return ResponseEntity.noContent().build();
    }

}
