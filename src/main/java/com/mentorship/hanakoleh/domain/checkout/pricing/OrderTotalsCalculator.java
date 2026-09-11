package com.mentorship.hanakoleh.domain.checkout.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class OrderTotalsCalculator {

    private static final int MONEY_SCALE = 2;

    public BigDecimal total(BigDecimal subtotal,
                            BigDecimal deliveryFee,
                            BigDecimal serviceFee,
                            BigDecimal riderTip,
                            BigDecimal tax,
                            BigDecimal discount) {
        return subtotal
                .add(deliveryFee)
                .add(serviceFee)
                .add(riderTip)
                .add(tax)
                .subtract(discount)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}