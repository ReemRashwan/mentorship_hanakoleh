package com.mentorship.hanakoleh.domain.checkout.dto;

public record DeliveryAddressResponse(
        Long addressId,
        Integer customerId,
        String streetAddress,
        String buildingNumber,
        String floor,
        String apartmentNumber,
        String landmark,
        String districtName,
        boolean isDefault) {
}