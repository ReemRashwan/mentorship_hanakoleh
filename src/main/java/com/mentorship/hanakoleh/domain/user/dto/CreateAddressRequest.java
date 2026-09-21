package com.mentorship.hanakoleh.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    private String streetAddress;

    @Size(max = 20, message = "Building number must not exceed 20 characters")
    private String buildingNumber;

    @Size(max = 20, message = "Floor must not exceed 20 characters")
    private String floor;

    @Size(max = 20, message = "Apartment number must not exceed 20 characters")
    private String apartmentNumber;

    @Size(max = 255, message = "Landmark must not exceed 255 characters")
    private String landmark;

    @Size(max = 100, message = "District name must not exceed 100 characters")
    private String districtName;

    private Boolean isDefault;
}
