package com.mentorship.hanakoleh.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AddPaymentMethodRequest(
        @NotBlank
        @Size(max = 255)
        String providerToken,
        @NotBlank
        @Size(max = 50)
        String brand,
        @NotBlank
        @Size(max = 4)
        String last4,
        @NotNull
        Integer expiryMonth,
        @NotNull
        Integer expiryYear,
        Boolean isDefault
) {
}
