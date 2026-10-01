package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.config.AppConstants;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.checkout.delivery.GeoDistanceCalculator;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryOptionResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.OrderTotalsResponse;
import com.mentorship.hanakoleh.domain.checkout.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.exception.CartNotActiveException;
import com.mentorship.hanakoleh.domain.checkout.exception.DeliveryOptionNotAvailableException;
import com.mentorship.hanakoleh.domain.checkout.exception.EmptyCartException;
import com.mentorship.hanakoleh.domain.checkout.exception.InvalidDeliveryAddressException;
import com.mentorship.hanakoleh.domain.checkout.exception.OutOfDeliveryZoneException;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import com.mentorship.hanakoleh.domain.checkout.pricing.OrderTotalsCalculator;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantDeliveryOption;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantDeliveryOptionRepository;
import com.mentorship.hanakoleh.domain.restaurant.validation.MenuItemOrderabilityValidator;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.repository.AddressRepository;
import com.mentorship.hanakoleh.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;
    private final CartPricingCalculator cartPricingCalculator;
    private final AddressRepository addressRepository;
    private final RestaurantDeliveryOptionRepository deliveryOptionRepository;
    private final GeoDistanceCalculator geoDistanceCalculator;
    private final OrderTotalsCalculator orderTotalsCalculator;
    private final PromotionService promotionService;

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

    @Transactional(readOnly = true)
    public Address resolveDeliveryAddressEntity(Integer customerId, Long addressId) {
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
        return address;
    }

    @Transactional(readOnly = true)
    public DeliveryOptionResponse resolveDeliveryOption(Integer customerId,
                                                        OrderDeliveryOption option,
                                                        Long addressId) {
        Cart cart = loadAndValidateCart(customerId);
        RestaurantDeliveryOption config = deliveryOptionRepository
                .findActiveWithRestaurant(cart.getRestaurant().getId(), option)
                .orElseThrow(() -> new DeliveryOptionNotAvailableException(
                        ErrorCode.DELIVERY_OPTION_NOT_AVAILABLE.format(option)));

        Restaurant restaurant = config.getRestaurant();
        int estimatedMinutes = restaurant.getAvgPreparationTimeInMins() + config.getTimeModifierMins();
        BigDecimal fee = MoneyUtils.scale(config.getAdditionalFee());

        if (option != OrderDeliveryOption.DELIVERY) {
            return new DeliveryOptionResponse(config.getId(), option, true, fee, estimatedMinutes, null);
        }

        Address address = resolveDeliveryAddressEntity(customerId, addressId);
        BigDecimal distance = distanceKm(address, restaurant);
        if (distance.compareTo(restaurant.getDeliveryRadiusKm()) > 0) {
            throw new OutOfDeliveryZoneException(
                    ErrorCode.OUT_OF_DELIVERY_ZONE.format(distance, restaurant.getDeliveryRadiusKm()));
        }
        return new DeliveryOptionResponse(config.getId(), option, false, fee, estimatedMinutes, distance);
    }

    @Transactional(readOnly = true)
    public OrderTotalsResponse computeTotals(Integer customerId, OrderDeliveryOption option,
                                             Long addressId, String promoCode, BigDecimal riderTip) {
        BigDecimal tip = MoneyUtils.normalizeTip(riderTip);
        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();
        DeliveryOptionResponse delivery = resolveDeliveryOption(customerId, option, addressId);

        BigDecimal discount = previewDiscount(subtotal, promoCode, customerId);
        BigDecimal total = orderTotalsCalculator.total(
                subtotal, delivery.deliveryFee(), AppConstants.SERVICE_FEE, tip, AppConstants.TAX_AMOUNT, discount);
        OffsetDateTime eta = OffsetDateTime.now().plusMinutes(delivery.estimatedMinutes());

        return new OrderTotalsResponse(option, AppConstants.CURRENCY, subtotal, delivery.deliveryFee(),
                AppConstants.SERVICE_FEE, tip, AppConstants.TAX_AMOUNT, discount, total,
                delivery.estimatedMinutes(), eta);
    }

    private BigDecimal previewDiscount(BigDecimal subtotal, String code, Integer customerId) {
        if (code == null || code.isBlank()) {
            return MoneyUtils.zero();
        }
        return promotionService.computeDiscount(
                promotionService.validate(code, subtotal, customerId), subtotal);
    }

    private BigDecimal distanceKm(Address address, Restaurant restaurant) {
        double d = geoDistanceCalculator.distanceKm(
                address.getLatitude(), address.getLongitude(),
                restaurant.getLatitude(), restaurant.getLongitude());
        return MoneyUtils.scale(BigDecimal.valueOf(d));
    }
}
