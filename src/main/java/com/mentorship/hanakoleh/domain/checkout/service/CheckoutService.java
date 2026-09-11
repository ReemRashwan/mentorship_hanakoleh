package com.mentorship.hanakoleh.domain.checkout.service;

import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.checkout.delivery.GeoDistanceCalculator;
import com.mentorship.hanakoleh.domain.checkout.dto.CartPricingResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryAddressResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryOptionResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.OrderResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.OrderTotalsResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.PlaceOrderRequest;
import com.mentorship.hanakoleh.domain.checkout.dto.PromotionResponse;
import com.mentorship.hanakoleh.domain.checkout.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.exception.CartNotActiveException;
import com.mentorship.hanakoleh.domain.checkout.exception.DeliveryOptionNotAvailableException;
import com.mentorship.hanakoleh.domain.checkout.exception.EmptyCartException;
import com.mentorship.hanakoleh.domain.checkout.exception.InvalidDeliveryAddressException;
import com.mentorship.hanakoleh.domain.checkout.exception.OutOfDeliveryZoneException;
import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotApplicableException;
import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import com.mentorship.hanakoleh.domain.checkout.pricing.OrderTotalsCalculator;
import com.mentorship.hanakoleh.domain.checkout.promotion.PromotionDiscountCalculator;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.order.repository.OrderItemRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.repository.PromotionRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantDeliveryOption;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantDeliveryOptionRepository;
import com.mentorship.hanakoleh.domain.restaurant.validation.MenuItemOrderabilityValidator;
import com.mentorship.hanakoleh.domain.user.model.Address;
import com.mentorship.hanakoleh.domain.user.repository.AddressRepository;
import com.mentorship.hanakoleh.exception.ErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckoutService {

    private static final int MONEY_SCALE = 2;
    private static final BigDecimal SERVICE_FEE = BigDecimal.ZERO.setScale(MONEY_SCALE);
    private static final BigDecimal TAX_AMOUNT = BigDecimal.ZERO.setScale(MONEY_SCALE);
    private static final String CURRENCY = "EGP";

    private final CartRepository cartRepository;
    private final MenuItemOrderabilityValidator menuItemOrderabilityValidator;
    private final CartPricingCalculator cartPricingCalculator;
    private final AddressRepository addressRepository;
    private final RestaurantDeliveryOptionRepository deliveryOptionRepository;
    private final GeoDistanceCalculator geoDistanceCalculator;
    private final PromotionRepository promotionRepository;
    private final PromotionDiscountCalculator promotionDiscountCalculator;
    private final OrderTotalsCalculator orderTotalsCalculator;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public CheckoutService(CartRepository cartRepository,
                           MenuItemOrderabilityValidator menuItemOrderabilityValidator,
                           CartPricingCalculator cartPricingCalculator,
                           AddressRepository addressRepository,
                           RestaurantDeliveryOptionRepository deliveryOptionRepository,
                           GeoDistanceCalculator geoDistanceCalculator,
                           PromotionRepository promotionRepository,
                           PromotionDiscountCalculator promotionDiscountCalculator,
                           OrderTotalsCalculator orderTotalsCalculator,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.menuItemOrderabilityValidator = menuItemOrderabilityValidator;
        this.cartPricingCalculator = cartPricingCalculator;
        this.addressRepository = addressRepository;
        this.deliveryOptionRepository = deliveryOptionRepository;
        this.geoDistanceCalculator = geoDistanceCalculator;
        this.promotionRepository = promotionRepository;
        this.promotionDiscountCalculator = promotionDiscountCalculator;
        this.orderTotalsCalculator = orderTotalsCalculator;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

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
        return cartPricingCalculator.reprice(loadAndValidateCart(customerId));
    }

    // --- Issue #3: delivery address -----------------------------------------------

    @Transactional(readOnly = true)
    public DeliveryAddressResponse resolveDeliveryAddress(Integer customerId, Long addressId) {
        Address a = resolveDeliveryAddressEntity(customerId, addressId);
        return new DeliveryAddressResponse(
                a.getId(), customerId, a.getLabel(), formatAddress(a),
                a.getLatitude(), a.getLongitude(), Boolean.TRUE.equals(a.getIsDefault()));
    }

    private Address resolveDeliveryAddressEntity(Integer customerId, Long addressId) {
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

    // --- Issue #4: delivery option ------------------------------------------------

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
        BigDecimal fee = config.getAdditionalFee().setScale(MONEY_SCALE, RoundingMode.HALF_UP);

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

    // --- Issue #5: promotions -----------------------------------------------------

    @Transactional(readOnly = true)
    public PromotionResponse applyPromotion(Integer customerId, String code) {
        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();
        Promotion promotion = requireValidPromotion(code, subtotal);
        BigDecimal discount = promotionDiscountCalculator.discountFor(promotion, subtotal);
        return new PromotionResponse(promotion.getCode(), promotion.getDiscountType(),
                promotion.getDiscountValue(), subtotal, discount, subtotal.subtract(discount));
    }

    // --- Issue #6: totals & ETA ---------------------------------------------------

    @Transactional(readOnly = true)
    public OrderTotalsResponse computeTotals(Integer customerId, OrderDeliveryOption option,
                                             Long addressId, String promoCode, BigDecimal riderTip) {
        BigDecimal tip = normalizeTip(riderTip);
        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();
        DeliveryOptionResponse delivery = resolveDeliveryOption(customerId, option, addressId);

        BigDecimal discount = discountFor(subtotal, promoCode);
        BigDecimal total = orderTotalsCalculator.total(
                subtotal, delivery.deliveryFee(), SERVICE_FEE, tip, TAX_AMOUNT, discount);
        OffsetDateTime eta = OffsetDateTime.now().plusMinutes(delivery.estimatedMinutes());

        return new OrderTotalsResponse(option, CURRENCY, subtotal, delivery.deliveryFee(), SERVICE_FEE,
                tip, TAX_AMOUNT, discount, total, delivery.estimatedMinutes(), eta);
    }

    // --- Issue #7: place order (persist) ------------------------------------------

    @Transactional
    public OrderResponse placeOrder(Integer customerId, PlaceOrderRequest request) {
        BigDecimal tip = normalizeTip(request.riderTip());
        Cart cart = loadAndValidateCart(customerId);
        BigDecimal subtotal = cartPricingCalculator.reprice(cart).subtotal();

        DeliveryOptionResponse delivery = resolveDeliveryOption(customerId, request.deliveryOption(), request.addressId());
        Address address = (request.deliveryOption() == OrderDeliveryOption.DELIVERY)
                ? resolveDeliveryAddressEntity(customerId, request.addressId())
                : null;

        Promotion promotion = null;
        BigDecimal discount = BigDecimal.ZERO.setScale(MONEY_SCALE);
        if (request.promoCode() != null && !request.promoCode().isBlank()) {
            promotion = requireValidPromotion(request.promoCode(), subtotal);
            discount = promotionDiscountCalculator.discountFor(promotion, subtotal);
        }

        BigDecimal total = orderTotalsCalculator.total(subtotal, delivery.deliveryFee(), SERVICE_FEE, tip, TAX_AMOUNT, discount);

        Order order = Order.builder()
                .idempotencyKey(UUID.randomUUID())
                .customer(cart.getCustomer())
                .restaurant(cart.getRestaurant())
                .address(address)
                .promotion(promotion)
                .deliveryOption(request.deliveryOption())
                .paymentMethod(request.paymentMethod())
                .currencyCode(CURRENCY)
                .subtotal(subtotal)
                .deliveryFees(delivery.deliveryFee())
                .serviceFees(SERVICE_FEE)
                .riderTips(tip)
                .discountAmount(discount)
                .taxAmount(TAX_AMOUNT)
                .totalAmount(total)
                .deliveryInstructions(request.deliveryInstructions())
                .deliveryAddressSnapshot(buildSnapshot(request.deliveryOption(), address))
                .estimatedDeliveryAt(OffsetDateTime.now().plusMinutes(delivery.estimatedMinutes()))
                .build();
        order = orderRepository.save(order);

        List<OrderItem> lines = buildOrderItems(order, cart);
        orderItemRepository.saveAll(lines);

        return toOrderResponse(order, lines);
    }

    // --- shared helpers -----------------------------------------------------------

    private List<OrderItem> buildOrderItems(Order order, Cart cart) {
        List<OrderItem> lines = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            MenuItem mi = ci.getMenuItem();
            BigDecimal price = mi.getPrice().setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            BigDecimal lineSubtotal = price.multiply(BigDecimal.valueOf(ci.getQuantity()))
                    .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            lines.add(OrderItem.builder()
                    .order(order)
                    .menuItem(mi)
                    .nameSnapshot(mi.getName())
                    .price(price)
                    .quantity(ci.getQuantity())
                    .subtotal(lineSubtotal)
                    .specialInstructions(ci.getNote())
                    .build());
        }
        return lines;
    }

    private Map<String, Object> buildSnapshot(OrderDeliveryOption option, Address a) {
        Map<String, Object> m = new LinkedHashMap<>();
        if (a == null) {
            m.put("fulfillment", option.name());
            return m;
        }
        m.put("governorate", a.getGovernorate());
        m.put("city", a.getCity());
        if (a.getDistrict() != null) {
            m.put("district", a.getDistrict());
        }
        m.put("street", a.getStreet());
        if (a.getBuildingNumber() != null) {
            m.put("building", a.getBuildingNumber());
        }
        if (a.getFloor() != null) {
            m.put("floor", a.getFloor());
        }
        if (a.getApartment() != null) {
            m.put("apartment", a.getApartment());
        }
        if (a.getLabel() != null) {
            m.put("label", a.getLabel());
        }
        m.put("latitude", a.getLatitude());
        m.put("longitude", a.getLongitude());
        return m;
    }

    private OrderResponse toOrderResponse(Order o, List<OrderItem> lines) {
        List<OrderResponse.OrderLine> items = lines.stream()
                .map(l -> new OrderResponse.OrderLine(
                        l.getMenuItem().getId(), l.getNameSnapshot(), l.getQuantity(),
                        l.getPrice(), l.getSubtotal()))
                .toList();
        return new OrderResponse(
                o.getId(), o.getIdempotencyKey().toString(), o.getDeliveryOption(),
                o.getFinalStatus(), o.getPaymentStatus(), o.getPaymentMethod(), o.getCurrencyCode(),
                o.getSubtotal(), o.getDeliveryFees(), o.getServiceFees(), o.getRiderTips(),
                o.getDiscountAmount(), o.getTaxAmount(), o.getTotalAmount(),
                o.getEstimatedDeliveryAt(), items);
    }

    private BigDecimal discountFor(BigDecimal subtotal, String code) {
        if (code == null || code.isBlank()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }
        return promotionDiscountCalculator.discountFor(requireValidPromotion(code, subtotal), subtotal);
    }

    private Promotion requireValidPromotion(String code, BigDecimal subtotal) {
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new PromotionNotFoundException(ErrorCode.PROMOTION_NOT_FOUND.format(code)));

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
        return promotion;
    }

    private BigDecimal normalizeTip(BigDecimal riderTip) {
        BigDecimal tip = (riderTip == null) ? BigDecimal.ZERO : riderTip;
        if (tip.signum() < 0) {
            throw new IllegalArgumentException(ErrorCode.RIDER_TIP_NEGATIVE.getMessage());
        }
        return tip.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal distanceKm(Address address, Restaurant restaurant) {
        double d = geoDistanceCalculator.distanceKm(
                address.getLatitude(), address.getLongitude(),
                restaurant.getLatitude(), restaurant.getLongitude());
        return BigDecimal.valueOf(d).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private String formatAddress(Address a) {
        return Stream.of(a.getStreet(), a.getDistrict(), a.getCity(), a.getGovernorate())
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(", "));
    }
}