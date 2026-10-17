package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.common.EnumParser;
import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.checkout.service.CheckoutService;
import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import com.mentorship.hanakoleh.domain.order.model.OrderPaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(20)
@RequiredArgsConstructor
public class CartValidationStepService implements PlaceOrderStep {

    private final CheckoutService checkoutService;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        context.setDeliveryOption(
                EnumParser.parse(OrderDeliveryOption.class, context.getRequest().deliveryOption()));
        context.setPaymentMethod(
                EnumParser.parse(OrderPaymentMethod.class, context.getRequest().paymentMethod()));
        context.setTip(MoneyUtils.normalizeTip(context.getRequest().riderTip()));
        context.setCart(checkoutService.loadAndValidateCart(context.getCustomerId()));
        chain.next(context);
    }
}
