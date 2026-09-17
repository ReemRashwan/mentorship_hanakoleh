package com.mentorship.hanakoleh.domain.checkout.dto;

import java.math.BigDecimal;

public record DeliveryAddressResponse(
        Long addressId,
        Integer customerId,
        String label,
        String formattedAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isDefault) {
}