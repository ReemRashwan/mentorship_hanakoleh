package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PlaceOrderRequest(
        @NotNull OrderDeliveryOption deliveryOption,
        Long addressId,
        String promoCode,
        @DecimalMin("0.00") BigDecimal riderTip,
        @NotNull OrderPaymentMethod paymentMethod,
        @Size(max = 1000) String deliveryInstructions) {
}