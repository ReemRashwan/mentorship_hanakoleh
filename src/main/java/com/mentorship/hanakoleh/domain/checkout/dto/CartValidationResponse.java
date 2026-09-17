package com.mentorship.hanakoleh.domain.checkout.dto;

import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import java.util.List;

/**
 * Result of validating a cart for checkout (issue 01CART-checkout-cart-load-validate).
 * Re-pricing / subtotal is added by the cart-repricing issue.
 */
public record CartValidationResponse(
        Integer cartId,
        Integer customerId,
        Integer restaurantId,
        CartStatus status,
        int totalItems,
        List<CartItemSummary> items) {

    public record CartItemSummary(
            Integer cartItemId,
            Integer menuItemId,
            String menuItemName,
            int quantity) {
    }
}
