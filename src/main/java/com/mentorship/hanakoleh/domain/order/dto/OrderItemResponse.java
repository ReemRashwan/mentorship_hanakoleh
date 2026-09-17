package com.mentorship.hanakoleh.domain.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record OrderItemResponse(
        Long orderItemId,
        Integer menuItemId,
        String name,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        List<Map<String, Object>> options,
        String specialInstructions
) {
}
