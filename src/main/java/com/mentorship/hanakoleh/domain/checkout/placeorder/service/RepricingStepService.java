package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.checkout.pricing.CartPricingCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(30)
@RequiredArgsConstructor
public class RepricingStepService implements PlaceOrderStep {

    private final CartPricingCalculator cartPricingCalculator;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        context.setSubtotal(cartPricingCalculator.reprice(context.getCart()).subtotal());
        chain.next(context);
    }
}
