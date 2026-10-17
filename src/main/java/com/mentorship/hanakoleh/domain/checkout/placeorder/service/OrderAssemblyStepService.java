package com.mentorship.hanakoleh.domain.checkout.placeorder.service;

import com.mentorship.hanakoleh.common.AppConstants;
import com.mentorship.hanakoleh.domain.checkout.pricing.OrderTotalsCalculator;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderChain;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderContext;
import com.mentorship.hanakoleh.domain.checkout.placeorder.PlaceOrderStep;
import com.mentorship.hanakoleh.domain.order.factory.OrderBuilder;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(60)
@RequiredArgsConstructor
public class OrderAssemblyStepService implements PlaceOrderStep {

    private final OrderTotalsCalculator orderTotalsCalculator;

    @Override
    public void handle(PlaceOrderContext context, PlaceOrderChain chain) {
        BigDecimal total = orderTotalsCalculator.total(
                context.getSubtotal(), context.getDelivery().deliveryFee(),
                AppConstants.SERVICE_FEE, context.getTip(), AppConstants.TAX_AMOUNT, context.getDiscount());
        context.setTotal(total);

        UUID idempotencyKey = context.getIdempotencyKey() != null ? context.getIdempotencyKey() : UUID.randomUUID();
        OrderBuilder.AssembledOrder assembled = OrderBuilder.create()
                .cart(context.getCart())
                .idempotencyKey(idempotencyKey)
                .deliveryOption(context.getDeliveryOption())
                .paymentMethod(context.getPaymentMethod())
                .address(context.getAddress())
                .promotion(context.getPromotion())
                .currencyCode(AppConstants.CURRENCY)
                .subtotal(context.getSubtotal())
                .deliveryFees(context.getDelivery().deliveryFee())
                .serviceFees(AppConstants.SERVICE_FEE)
                .riderTips(context.getTip())
                .discountAmount(context.getDiscount())
                .taxAmount(AppConstants.TAX_AMOUNT)
                .totalAmount(total)
                .deliveryInstructions(context.getRequest().deliveryInstructions())
                .estimatedMinutes(context.getDelivery().estimatedMinutes())
                .build();
        context.setOrder(assembled.order());
        context.setItems(assembled.items());
        chain.next(context);
    }
}
