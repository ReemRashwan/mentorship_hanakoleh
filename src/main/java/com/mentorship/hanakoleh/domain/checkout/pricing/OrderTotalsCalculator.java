package com.mentorship.hanakoleh.domain.checkout.pricing;

import com.mentorship.hanakoleh.config.AppConstants;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class OrderTotalsCalculator {

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
                .setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
    }
}