package com.mentorship.hanakoleh.domain.checkout.placeorder;

import com.mentorship.hanakoleh.domain.checkout.dto.DeliveryOptionResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.OrderResponse;
import com.mentorship.hanakoleh.domain.checkout.dto.PlaceOrderRequest;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.user.model.Address;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderContext {

    // inputs
    private Integer customerId;
    private UUID idempotencyKey;
    private PlaceOrderRequest request;

    // derived from the request
    private OrderDeliveryOption deliveryOption;
    private OrderPaymentMethod paymentMethod;
    private BigDecimal tip;

    // gathered during the cycle
    private Cart cart;
    private BigDecimal subtotal;
    private DeliveryOptionResponse delivery;
    private Address address;
    private Promotion promotion;
    private BigDecimal discount;
    private BigDecimal total;
    private Order order;
    private List<OrderItem> items;

    // result
    private OrderResponse response;
}
