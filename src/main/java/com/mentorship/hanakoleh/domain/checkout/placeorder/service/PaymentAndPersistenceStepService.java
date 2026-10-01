package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.domain.cart.service.CartService;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.checkout.service.PromotionService;
import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.service.OrderService;
import com.mentorship.hanakoleh.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Prepare payment (no charge), persist the order + lines + tracking, complete the cart, consume the promo. */
@Service
@org.springframework.core.annotation.Order(70)
@RequiredArgsConstructor
public class PaymentAndPersistenceStepService implements PlaceOrderStep {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CartService cartService;
    private final PromotionService promotionService;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        Order order = context.getOrder();
        paymentService.prepare(order);
        order = orderService.persist(order);
        context.setOrder(order);
        orderService.persistItems(context.getItems());
        orderService.recordInitialTracking(order);
        cartService.completeCart(context.getCart());
        if (context.getPromotion() != null) {
            promotionService.consume(context.getPromotion());
        }
        chain.next(context);
    }
}
