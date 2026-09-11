package com.mentorship.hanakoleh.exception;

public enum ErrorCode {
    QUANTITY_REQUIRED("CART_001", "Quantity is required."),
    QUANTITY_MUST_BE_POSITIVE(
            "CART_002", "Quantity must be greater than 0, or delete the item from the cart."),
    CART_ITEM_NOT_FOUND("CART_003", "Cart item %s was not found."),
    MENU_ITEM_NOT_ORDERABLE("CART_004", "Menu item %s is currently %s."),
    MENU_ITEM_INSUFFICIENT_STOCK("CART_005", "Only %s units of menu item %s are available."),
    CART_EMPTY("CART_006", "Cart is empty. Add items before checkout."),
    CART_NOT_ACTIVE("CART_007", "Checkout is not allowed while the cart is %s."),
    NO_ACTIVE_CART("CART_008", "No cart was found for the current customer."),
    ADDRESS_NOT_FOUND("ADDR_001", "Address %s was not found for the current customer."),
    NO_DEFAULT_ADDRESS("ADDR_002", "No default delivery address is set for the current customer."),
    ADDRESS_MISSING_LOCATION("ADDR_003", "The selected delivery address has no map location; please pin it on the map."),
    DELIVERY_OPTION_NOT_AVAILABLE("DLV_001", "Delivery option %s is not available for this restaurant."),
    OUT_OF_DELIVERY_ZONE("DLV_002", "Your address is %s km away, outside the restaurant's %s km delivery zone."),
    PROMOTION_NOT_FOUND("PROMO_001", "Promotion code %s was not found."),
    PROMOTION_NOT_ACTIVE("PROMO_002", "Promotion %s is not currently active."),
    PROMOTION_BELOW_MIN_ORDER("PROMO_003", "Promotion %s requires a minimum order of %s."),
    PROMOTION_USAGE_EXHAUSTED("PROMO_004", "Promotion %s has reached its usage limit."),
    RIDER_TIP_NEGATIVE("TOTAL_001", "Rider tip cannot be negative.");
    public static final String QUANTITY_REQUIRED_MESSAGE = "Quantity is required.";
    public static final String QUANTITY_MUST_BE_POSITIVE_MESSAGE =
            "Quantity must be greater than 0, or delete the item from the cart.";
    public static final String CART_ITEM_NOT_FOUND_MESSAGE = "Cart item %s was not found.";
    public static final String MENU_ITEM_NOT_ORDERABLE_MESSAGE = "Menu item %s is currently %s.";
    public static final String MENU_ITEM_INSUFFICIENT_STOCK_MESSAGE =
            "Only %s units of menu item %s are available.";

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
