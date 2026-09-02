package com.mentorship.hanakoleh.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.MenuItemNotOrderableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldReportMissingCartItemAsNotFound() {
        ProblemDetail problemDetail =
                globalExceptionHandler.handleCartItemNotFound(new CartItemNotFoundException(42));

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND.format(42), problemDetail.getDetail());
    }

    @Test
    void shouldReportUnorderableMenuItemAsConflict() {
        ProblemDetail problemDetail = globalExceptionHandler.handleMenuItemNotOrderable(
                new MenuItemNotOrderableException(ErrorCode.MENU_ITEM_INSUFFICIENT_STOCK.format(10, 7)));

        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals(ErrorCode.MENU_ITEM_INSUFFICIENT_STOCK.format(10, 7), problemDetail.getDetail());
    }

    @Test
    void shouldReportInvalidQuantityAsBadRequest() {
        ProblemDetail problemDetail = globalExceptionHandler.handleIllegalArgument(
                new IllegalArgumentException(ErrorCode.QUANTITY_MUST_BE_POSITIVE.getMessage()));

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals(ErrorCode.QUANTITY_MUST_BE_POSITIVE.getMessage(), problemDetail.getDetail());
    }
}
