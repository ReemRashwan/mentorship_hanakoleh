package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.checkout.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryAddressResponse;
import com.mentorship.hanakoleh.domain.checkout.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.exception.CartNotActiveException;
import com.mentorship.hanakoleh.domain.checkout.exception.EmptyCartException;
import com.mentorship.hanakoleh.domain.checkout.exception.InvalidDeliveryAddressException;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import com.mentorship.hanakoleh.domain.restaurant.validation.MenuItemOrderabilityValidator;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.repository.AddressRepository;
import com.mentorship.hanakoleh.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;
    private final CartPricingCalculator cartPricingCalculator;
    private final AddressRepository addressRepository;

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

    /**
     * Resolves the delivery address for checkout
     * uses the given address or, when none is given, the customer's
     * default, and validates it has a map location.
     */
    @Transactional(readOnly = true)
    public DeliveryAddressResponse resolveDeliveryAddress(Integer customerId, Long addressId) {
        Address address = (addressId != null)
                ? addressRepository.findByIdAndCustomerId(addressId, customerId)
                  .orElseThrow(() -> new AddressNotFoundException(
                          ErrorCode.ADDRESS_NOT_FOUND.format(addressId)))
                : addressRepository.findDefaultByCustomerId(customerId)
                  .orElseThrow(() -> new InvalidDeliveryAddressException(
                          ErrorCode.NO_DEFAULT_ADDRESS.getMessage()));

        if (address.getLatitude() == null || address.getLongitude() == null) {
            throw new InvalidDeliveryAddressException(ErrorCode.ADDRESS_MISSING_LOCATION.getMessage());
        }

        return new DeliveryAddressResponse(
                address.getId(),
                customerId,
                address.getLabel(),
                formatAddress(address),
                address.getLatitude(),
                address.getLongitude(),
                Boolean.TRUE.equals(address.getIsDefault()));
    }

    private String formatAddress(Address a) {
        return Stream.of(a.getStreet(), a.getDistrict(), a.getCity(), a.getGovernorate())
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(", "));
    }
}