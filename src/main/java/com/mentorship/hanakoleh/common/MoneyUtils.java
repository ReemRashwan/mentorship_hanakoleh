package com.mentorship.hanakoleh.common;

import com.mentorship.hanakoleh.common.AppConstants;
import com.mentorship.hanakoleh.exception.ErrorCode;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {

    private MoneyUtils() {
    }

    /** Round a monetary value to the application money scale. */
    public static BigDecimal scale(BigDecimal value) {
        return value.setScale(AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
    }

    /** Zero at the application money scale. */
    public static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(AppConstants.MONEY_SCALE);
    }

    /** unit price x quantity, rounded to the money scale. */
    public static BigDecimal lineTotal(BigDecimal unitPrice, int quantity) {
        return scale(scale(unitPrice).multiply(BigDecimal.valueOf(quantity)));
    }

    /** Default missing tip to zero and reject a negative tip. */
    public static BigDecimal normalizeTip(BigDecimal riderTip) {
        BigDecimal tip = (riderTip == null) ? BigDecimal.ZERO : riderTip;
        if (tip.signum() < 0) {
            throw new IllegalArgumentException(ErrorCode.RIDER_TIP_NEGATIVE.getMessage());
        }
        return scale(tip);
    }
}
