package com.mentorship.hanakoleh.exception;

public enum ErrorCode {
    QUANTITY_REQUIRED("CART_001", "Quantity is required."),
    QUANTITY_MUST_BE_POSITIVE(
            "CART_002", "Quantity must be greater than 0, or delete the item from the cart."),
    CART_ITEM_NOT_FOUND("CART_003", "Cart item %s was not found."),
    MENU_ITEM_NOT_ORDERABLE("CART_004", "Menu item %s is currently %s."),
    MENU_ITEM_INSUFFICIENT_STOCK("CART_005", "Only %s units of menu item %s are available.");

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
