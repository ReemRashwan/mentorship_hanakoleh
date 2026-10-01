package com.mentorship.hanakoleh.domain.checkout.pricing;

import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.checkout.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CartPricingCalculator {

    public CartPricingResponse reprice(Cart cart) {
        List<CartPricingResponse.PricedItem> items = new ArrayList<>();
        BigDecimal currentSubtotal = BigDecimal.ZERO;
        BigDecimal previousSubtotal = BigDecimal.ZERO;
        boolean anyPriceChanged = false;

        for (CartItem item : cart.getItems()) {
            int quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Cart item " + item.getId() + " has a non-positive quantity: " + quantity);
            }

            MenuItem menuItem = item.getMenuItem();

            // Current price is read straight from the menu item (source of truth).
            BigDecimal currentUnitPrice = MoneyUtils.scale(menuItem.getPrice());
            // Previous price is the snapshot captured on the cart item when it was added.
            BigDecimal previousUnitPrice = MoneyUtils.scale(item.getPrice());
            BigDecimal unitPriceDifference = MoneyUtils.scale(currentUnitPrice.subtract(previousUnitPrice));

            BigDecimal currentLineSubtotal = MoneyUtils.lineTotal(menuItem.getPrice(), quantity);
            BigDecimal previousLineSubtotal = MoneyUtils.lineTotal(item.getPrice(), quantity);
            BigDecimal lineDifference = MoneyUtils.scale(currentLineSubtotal.subtract(previousLineSubtotal));

            boolean priceChanged = unitPriceDifference.signum() != 0;
            anyPriceChanged = anyPriceChanged || priceChanged;

            currentSubtotal = currentSubtotal.add(currentLineSubtotal);
            previousSubtotal = previousSubtotal.add(previousLineSubtotal);

            items.add(new CartPricingResponse.PricedItem(
                    item.getId(), menuItem.getId(), menuItem.getName(), quantity,
                    currentUnitPrice, previousUnitPrice, unitPriceDifference,
                    currentLineSubtotal, previousLineSubtotal, lineDifference, priceChanged));
        }

        BigDecimal subtotal = MoneyUtils.scale(currentSubtotal);
        BigDecimal prevSubtotal = MoneyUtils.scale(previousSubtotal);
        BigDecimal subtotalDifference = MoneyUtils.scale(subtotal.subtract(prevSubtotal));

        return new CartPricingResponse(
                cart.getId(),
                cart.getRestaurant() == null ? null : cart.getRestaurant().getId(),
                subtotal,
                prevSubtotal,
                subtotalDifference,
                anyPriceChanged,
                items);
    }
}
