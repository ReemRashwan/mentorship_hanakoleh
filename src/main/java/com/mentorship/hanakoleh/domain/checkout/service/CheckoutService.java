package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.domain.cart.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.checkout.exception.CartNotActiveException;
import com.mentorship.hanakoleh.domain.checkout.exception.EmptyCartException;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import com.mentorship.hanakoleh.domain.restaurant.validation.MenuItemOrderabilityValidator;
import com.mentorship.hanakoleh.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;
    private final CartPricingCalculator cartPricingCalculator;

    public CheckoutService(CartRepository cartRepository,
                           MenuItemOrderabilityValidator menuItemOrderabilityValidator,
                           CartPricingCalculator cartPricingCalculator) {
        this.cartRepository = cartRepository;
        this.menuItemOrderabilityValidator = menuItemOrderabilityValidator;
        this.cartPricingCalculator = cartPricingCalculator;
    }

    /**
     * <p>First capability. load the customer's cart and validate
     * it is ready for checkout - the cart exists, is ACTIVE, is not
     * empty, and every line's menu item is still orderable.
     */
    @Transactional(readOnly = true)
    public Cart loadAndValidateCart(Integer customerId) {
        Cart cart = cartRepository.findByCustomerIdForCheckout(customerId)
                .orElseThrow(() -> new CartNotFoundException(ErrorCode.NO_ACTIVE_CART.getMessage()));

        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new CartNotActiveException(ErrorCode.CART_NOT_ACTIVE.format(cart.getStatus()));
        }

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException(ErrorCode.CART_EMPTY.getMessage());
        }

        for (CartItem item : cart.getItems()) {
            menuItemOrderabilityValidator.validateOrderable(item.getMenuItem(), item.getQuantity());
        }

        return cart;
    }

    /**
     * Loads and validates the cart, then re-prices it from live menu prices
     */
    @Transactional(readOnly = true)
    public CartPricingResponse repriceCart(Integer customerId) {
        Cart cart = loadAndValidateCart(customerId);
        return this.cartPricingCalculator.reprice(cart);
    }
}