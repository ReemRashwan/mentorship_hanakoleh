package com.mentorship.hanakoleh.domain.cart.exception;

import com.mentorship.hanakoleh.exception.ErrorCode;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Integer cartItemId) {
        super(ErrorCode.CART_ITEM_NOT_FOUND.format(cartItemId));
    }
}
