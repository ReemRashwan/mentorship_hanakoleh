package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.checkout.service.CheckoutService;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(40)
@RequiredArgsConstructor
public class DeliveryStepService implements PlaceOrderStep {

    private final CheckoutService checkoutService;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        context.setDelivery(checkoutService.resolveDeliveryOption(
                context.getCustomerId(), context.getDeliveryOption(), context.getRequest().addressId()));
        if (context.getDeliveryOption() == OrderDeliveryOption.DELIVERY) {
            context.setAddress(checkoutService.resolveDeliveryAddressEntity(
                    context.getCustomerId(), context.getRequest().addressId()));
        }
        chain.next(context);
    }
}
