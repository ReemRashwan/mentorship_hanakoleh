package com.mentorship.hanakoleh.domain.checkout.promotion;

import com.mentorship.hanakoleh.common.MoneyUtils;
import com.mentorship.hanakoleh.config.AppConstants;
import com.mentorship.hanakoleh.domain.order.model.Promotion;
import com.mentorship.hanakoleh.domain.order.model.PromotionDiscountType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class PromotionDiscountCalculator {

    public BigDecimal discountFor(Promotion promotion, BigDecimal subtotal) {
        BigDecimal discount;
        if (promotion.getDiscountType() == PromotionDiscountType.PERCENTAGE) {
            discount = subtotal.multiply(promotion.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
            if (promotion.getMaxDiscountAmount() != null) {
                discount = discount.min(promotion.getMaxDiscountAmount());
            }
        } else {
            discount = promotion.getDiscountValue();
        }
        return MoneyUtils.scale(discount.min(subtotal));
    }
}
