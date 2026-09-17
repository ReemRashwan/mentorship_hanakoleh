package com.mentorship.hanakoleh.config;

import java.math.BigDecimal;

/**
 * Application-wide constants.
 * <p>
 * Place for small, commonly used constants (scales, timeouts, default values).
 */
public final class AppConstants {

    public static final int MONEY_SCALE = 2;
    public static final BigDecimal SERVICE_FEE = BigDecimal.ZERO.setScale(MONEY_SCALE);
    public static final BigDecimal TAX_AMOUNT = BigDecimal.ZERO.setScale(MONEY_SCALE);
    public static final String CURRENCY = "EGP";
    private AppConstants() {
        // utility class
    }


}

