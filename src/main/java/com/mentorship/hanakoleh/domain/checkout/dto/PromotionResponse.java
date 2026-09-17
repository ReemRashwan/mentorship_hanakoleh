package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.order.model.PromotionDiscountType;
import java.math.BigDecimal;

public record PromotionResponse(
        String code,
        PromotionDiscountType discountType,
        BigDecimal discountValue,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAfterDiscount) {
}