package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.config.AppConstants;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.checkout.delivery.GeoDistanceCalculator;
import com.mentorship.hanakoleh.domain.checkout.dto.*;
import com.mentorship.hanakoleh.domain.checkout.exception.*;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import com.mentorship.hanakoleh.domain.checkout.pricing.OrderTotalsCalculator;
import com.mentorship.hanakoleh.domain.checkout.promotion.PromotionDiscountCalculator;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.order.repository.PromotionRepository;
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
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;
    private final CartPricingCalculator cartPricingCalculator;
    private final AddressRepository addressRepository;
    private final RestaurantDeliveryOptionRepository deliveryOptionRepository;
    private final GeoDistanceCalculator geoDistanceCalculator;
    private final PromotionRepository promotionRepository;
    private final PromotionDiscountCalculator promotionDiscountCalculator;
    private final OrderTotalsCalculator orderTotalsCalculator;

    // --- Issue #1: load & validate ------------------------------------------------
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

    // --- Issue #2: re-pricing -----------------------------------------------------

    @Transactional(readOnly = true)
    public CartPricingResponse repriceCart(Integer customerId) {
        Cart cart = loadAndValidateCart(customerId);
        return cartPricingCalculator.reprice(cart);
    }

    // --- Issue #3: delivery address -----------------------------------------------

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
                address.getId(), customerId, address.getLabel(), formatAddress(address),
                address.getLatitude(), address.getLongitude(),
                Boolean.TRUE.equals(address.getIsDefault()));
    }

    // --- Issue #4: delivery option (fee, ETA, zone) -------------------------------

    @Transactional(readOnly = true)
    public DeliveryOptionResponse resolveDeliveryOption(Integer customerId,
                                                        OrderDeliveryOption option,
                                                        Long addressId) {
        Cart cart = loadAndValidateCart(customerId);
        Integer restaurantId = cart.getRestaurant().getId();

        RestaurantDeliveryOption config = this.deliveryOptionRepository
                .findActiveWithRestaurant(restaurantId, option)
                .orElseThrow(() -> new DeliveryOptionNotAvailableException(
                        ErrorCode.DELIVERY_OPTION_NOT_AVAILABLE.format(option)));

        Restaurant restaurant = config.getRestaurant();
        int estimatedMinutes = restaurant.getAvgPreparationTimeInMins() + config.getTimeModifierMins();
        BigDecimal fee = config.getAdditionalFee().setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);

        // Only DELIVERY is delivered; TAKEAWAY / IN_RESTAURANT are collected in person.
        if (option != OrderDeliveryOption.DELIVERY) {
            return new DeliveryOptionResponse(config.getId(), option, true, fee, estimatedMinutes, null);
        }

        DeliveryAddressResponse address = resolveDeliveryAddress(customerId, addressId);
        double distanceKm = this.geoDistanceCalculator.distanceKm(
                address.latitude(), address.longitude(),
                restaurant.getLatitude(), restaurant.getLongitude());

        BigDecimal distance = BigDecimal.valueOf(distanceKm).setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
        if (distance.compareTo(restaurant.getDeliveryRadiusKm()) > 0) {
            throw new OutOfDeliveryZoneException(
                    ErrorCode.OUT_OF_DELIVERY_ZONE.format(distance, restaurant.getDeliveryRadiusKm()));
        }

        return new DeliveryOptionResponse(config.getId(), option, false, fee, estimatedMinutes, distance);
    }

    // --- Issue #5: promotions -----------------------------------------------------

    @Transactional(readOnly = true)
    public PromotionResponse applyPromotion(Integer customerId, String code) {
        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();

        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new PromotionNotFoundException(
                        ErrorCode.PROMOTION_NOT_FOUND.format(code)));

        BigDecimal discount = discountForCode(subtotal, code);
        return new PromotionResponse(
                promotion.getCode(), promotion.getDiscountType(), promotion.getDiscountValue(),
                subtotal, discount, subtotal.subtract(discount));
    }

    // --- Issue #6: totals & ETA ---------------------------------------------------

    @Transactional(readOnly = true)
    public OrderTotalsResponse computeTotals(Integer customerId,
                                             OrderDeliveryOption option,
                                             Long addressId,
                                             String promoCode,
                                             BigDecimal riderTip) {
        BigDecimal tip = (riderTip == null) ? BigDecimal.ZERO : riderTip;
        if (tip.signum() < 0) {
            throw new IllegalArgumentException(ErrorCode.RIDER_TIP_NEGATIVE.getMessage());
        }
        tip = tip.setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);

        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();

        DeliveryOptionResponse delivery = resolveDeliveryOption(customerId, option, addressId);
        BigDecimal deliveryFee = delivery.deliveryFee();

        BigDecimal discount = BigDecimal.ZERO.setScale(AppConstants.MONEY_SCALE);
        if (promoCode != null && !promoCode.isBlank()) {
            discount = applyPromotion(customerId, promoCode).discountAmount();
        }

        BigDecimal total = orderTotalsCalculator.total(
                subtotal, deliveryFee, AppConstants.SERVICE_FEE, tip, AppConstants.TAX_AMOUNT, discount);

        int estimatedMinutes = delivery.estimatedMinutes();
        OffsetDateTime eta = OffsetDateTime.now().plusMinutes(estimatedMinutes);

        return new OrderTotalsResponse(
                option, AppConstants.CURRENCY, subtotal, deliveryFee, AppConstants.SERVICE_FEE, tip,
                AppConstants.TAX_AMOUNT, discount, total, estimatedMinutes, eta);
    }

    // --- helpers ------------------------------------------------------------------

    private BigDecimal discountForCode(BigDecimal subtotal, String code) {
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new PromotionNotFoundException(
                        ErrorCode.PROMOTION_NOT_FOUND.format(code)));

        OffsetDateTime now = OffsetDateTime.now();
        boolean active = Boolean.TRUE.equals(promotion.getIsActive())
                && !now.isBefore(promotion.getStartsAt())
                && !now.isAfter(promotion.getEndsAt());
        if (!active) {
            throw new PromotionNotApplicableException(ErrorCode.PROMOTION_NOT_ACTIVE.format(code));
        }

        if (subtotal.compareTo(promotion.getMinOrderAmount()) < 0) {
            throw new PromotionNotApplicableException(
                    ErrorCode.PROMOTION_BELOW_MIN_ORDER.format(code, promotion.getMinOrderAmount()));
        }

        if (promotion.getUsageLimitTotal() != null
                && promotion.getUsageCountTotal() >= promotion.getUsageLimitTotal()) {
            throw new PromotionNotApplicableException(ErrorCode.PROMOTION_USAGE_EXHAUSTED.format(code));
        }
        return promotionDiscountCalculator.discountFor(promotion, subtotal);
    }

    private String formatAddress(Address a) {
        return Stream.of(a.getStreet(), a.getDistrict(), a.getCity(), a.getGovernorate())
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(", "));
    }
}