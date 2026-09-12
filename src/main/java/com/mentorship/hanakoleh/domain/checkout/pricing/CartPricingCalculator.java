package com.mentorship.hanakoleh.domain.checkout.pricing;

import com.mentorship.hanakoleh.domain.checkout.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import org.springframework.stereotype.Component;
import com.mentorship.hanakoleh.config.AppConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/**
 * Re-prices a cart from live menu prices and computes the subtotal
 */
@Component
public class CartPricingCalculator {


    public CartPricingResponse reprice(Cart cart) {
        List<CartPricingResponse.PricedItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        boolean anyPriceChanged = false;

        for (CartItem item : cart.getItems()) {
            int quantity = item.getQuantity();
            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Cart item " + item.getId() + " has a non-positive quantity: " + quantity);
            }

            MenuItem menuItem = item.getMenuItem();
            BigDecimal unitPrice = scaled(menuItem.getPrice());
            BigDecimal previousUnitPrice = scaled(item.getPrice());
            BigDecimal lineSubtotal = scaled(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            boolean priceChanged = unitPrice.compareTo(previousUnitPrice) != 0;

            anyPriceChanged = anyPriceChanged || priceChanged;
            subtotal = subtotal.add(lineSubtotal);

            items.add(new CartPricingResponse.PricedItem(
                    item.getId(), menuItem.getId(), menuItem.getName(), quantity,
                    unitPrice, lineSubtotal, priceChanged, previousUnitPrice));
        }

        return new CartPricingResponse(
                cart.getId(),
                cart.getRestaurant() == null ? null : cart.getRestaurant().getId(),
                scaled(subtotal),
                anyPriceChanged,
                items);
    }

    private BigDecimal scaled(BigDecimal value) {
        return value.setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
    }
}