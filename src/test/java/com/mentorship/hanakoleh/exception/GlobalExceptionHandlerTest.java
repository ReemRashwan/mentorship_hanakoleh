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
        assertEquals("Cart item 42 was not found.", problemDetail.getDetail());
    }

    @Test
    void shouldReportUnorderableMenuItemAsConflict() {
        ProblemDetail problemDetail = globalExceptionHandler.handleMenuItemNotOrderable(
                new MenuItemNotOrderableException("Only 10 units of menu item 7 are available."));

        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("Only 10 units of menu item 7 are available.", problemDetail.getDetail());
    }

    @Test
    void shouldReportInvalidQuantityAsBadRequest() {
        ProblemDetail problemDetail = globalExceptionHandler.handleIllegalArgument(
                new IllegalArgumentException("Quantity must be greater than 0."));

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Quantity must be greater than 0.", problemDetail.getDetail());
    }
}
