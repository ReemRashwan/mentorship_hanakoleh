package com.mentorship.hanakoleh.domain.cart.exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Integer cartItemId) {
        super("Cart item " + cartItemId + " was not found.");
    }
}
