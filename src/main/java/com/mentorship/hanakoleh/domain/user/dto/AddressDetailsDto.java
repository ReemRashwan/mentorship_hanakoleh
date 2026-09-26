package com.mentorship.hanakoleh.domain.user.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record AddressDetailsDto(
        Long id,
        String streetAddress,
        String buildingNumber,
        String floor,
        String apartmentNumber,
        String landmark,
        String districtName,
        Boolean isDefault,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}