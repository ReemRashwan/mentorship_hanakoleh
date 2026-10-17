package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.checkout.service.PromotionService;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(50)
@RequiredArgsConstructor
public class PromotionStepService implements PlaceOrderStep {

    private final PromotionService promotionService;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        String code = context.getRequest().promoCode();
        if (code != null && !code.isBlank()) {
            Promotion promotion = promotionService.validate(code, context.getSubtotal(), context.getCustomerId());
            context.setPromotion(promotion);
            context.setDiscount(promotionService.computeDiscount(promotion, context.getSubtotal()));
        } else {
            context.setDiscount(MoneyUtils.zero());
        }
        chain.next(context);
    }
}
