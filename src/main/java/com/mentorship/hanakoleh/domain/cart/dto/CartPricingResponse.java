package com.mentorship.hanakoleh.domain.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartPricingResponse(
        Integer cartId,
        Integer restaurantId,
        BigDecimal subtotal,
        boolean priceChanged,
        List<PricedItem> items) {

    public record PricedItem(
            Integer cartItemId,
            Integer menuItemId,
            String menuItemName,
            int quantity,
            BigDecimal unitPrice,         // current live menu price
            BigDecimal lineSubtotal,      // unitPrice * quantity
            boolean priceChanged,         // live price differs from the stored snapshot
            BigDecimal previousUnitPrice  // the price stored on the cart item
    ) {
    }
}