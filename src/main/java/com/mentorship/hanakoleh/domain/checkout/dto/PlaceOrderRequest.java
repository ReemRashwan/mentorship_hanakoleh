package com.mentorship.hanakoleh.domain.checkout.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PlaceOrderRequest(
        @NotBlank String deliveryOption,
        Long addressId,
        String promoCode,
        @DecimalMin("0.00") BigDecimal riderTip,
        @NotBlank String paymentMethod,
        @Size(max = 1000) String deliveryInstructions) {
}
