package com.mentorship.hanakoleh.domain.order.factory;

import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import com.mentorship.hanakoleh.domain.user.model.Address;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class OrderBuilder {

    private Cart cart;
    private UUID idempotencyKey;
    private OrderDeliveryOption deliveryOption;
    private OrderPaymentMethod paymentMethod;
    private Address address;
    private Promotion promotion;
    private String currencyCode;
    private String deliveryInstructions;
    private int estimatedMinutes;

    private BigDecimal subtotal;
    private BigDecimal deliveryFees;
    private BigDecimal serviceFees;
    private BigDecimal riderTips;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    private OrderBuilder() {
    }

    public static OrderBuilder create() {
        return new OrderBuilder();
    }

    public OrderBuilder cart(Cart cart) {
        this.cart = cart;
        return this;
    }

    public OrderBuilder idempotencyKey(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
        return this;
    }

    public OrderBuilder deliveryOption(OrderDeliveryOption deliveryOption) {
        this.deliveryOption = deliveryOption;
        return this;
    }

    public OrderBuilder paymentMethod(OrderPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public OrderBuilder promotion(Promotion promotion) {
        this.promotion = promotion;
        return this;
    }

    public OrderBuilder currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public OrderBuilder deliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
        return this;
    }

    public OrderBuilder estimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
        return this;
    }

    public OrderBuilder subtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
        return this;
    }

    public OrderBuilder deliveryFees(BigDecimal deliveryFees) {
        this.deliveryFees = deliveryFees;
        return this;
    }

    public OrderBuilder serviceFees(BigDecimal serviceFees) {
        this.serviceFees = serviceFees;
        return this;
    }

    public OrderBuilder riderTips(BigDecimal riderTips) {
        this.riderTips = riderTips;
        return this;
    }

    public OrderBuilder discountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        return this;
    }

    public OrderBuilder taxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
        return this;
    }

    public OrderBuilder totalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public AssembledOrder build() {
        Order order = Order.builder()
                .idempotencyKey(idempotencyKey)
                .customer(cart.getCustomer())
                .restaurant(cart.getRestaurant())
                .address(address)
                .promotion(promotion)
                .deliveryOption(deliveryOption)
                .paymentMethod(paymentMethod)
                .currencyCode(currencyCode)
                .subtotal(subtotal)
                .deliveryFees(deliveryFees)
                .serviceFees(serviceFees)
                .riderTips(riderTips)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .deliveryInstructions(deliveryInstructions)
                .deliveryAddressSnapshot(buildSnapshot())
                .estimatedDeliveryAt(OffsetDateTime.now().plusMinutes(estimatedMinutes))
                .build();

        return new AssembledOrder(order, buildItems(order));
    }

    private List<OrderItem> buildItems(Order order) {
        List<OrderItem> lines = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            MenuItem menuItem = cartItem.getMenuItem();
            BigDecimal price = MoneyUtils.scale(menuItem.getPrice());
            BigDecimal lineSubtotal = MoneyUtils.lineTotal(menuItem.getPrice(), cartItem.getQuantity());
            lines.add(OrderItem.builder()
                    .order(order)
                    .menuItem(menuItem)
                    .nameSnapshot(menuItem.getName())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(lineSubtotal)
                    .specialInstructions(cartItem.getNote())
                    .build());
        }
        return lines;
    }

    private Map<String, Object> buildSnapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        if (address == null) {
            snapshot.put("fulfillment", deliveryOption.name());
            return snapshot;
        }
        snapshot.put("street", address.getStreetAddress());
        if (address.getDistrictName() != null) {
            snapshot.put("district", address.getDistrictName());
        }
        if (address.getBuildingNumber() != null) {
            snapshot.put("building", address.getBuildingNumber());
        }
        if (address.getFloor() != null) {
            snapshot.put("floor", address.getFloor());
        }
        if (address.getApartmentNumber() != null) {
            snapshot.put("apartment", address.getApartmentNumber());
        }
        if (address.getLandmark() != null) {
            snapshot.put("landmark", address.getLandmark());
        }
        return snapshot;
    }

    public record AssembledOrder(Order order, List<OrderItem> items) {
    }
}
