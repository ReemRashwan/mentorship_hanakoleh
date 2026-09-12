package com.mentorship.hanakoleh.exception;

import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemNotOrderableException;
import com.mentorship.hanakoleh.domain.cart.exception.OperationNotAllowedException;
import com.mentorship.hanakoleh.domain.order.exception.InvalidOrderTransitionException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByRestaurantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OperationNotAllowedException.class)
    public ProblemDetail handleOperationNotAllowed(OperationNotAllowedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ProblemDetail handleCartNotFound(CartNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ProblemDetail handleCartItemNotFound(CartItemNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MenuItemNotOrderableException.class)
    public ProblemDetail handleMenuItemNotOrderable(MenuItemNotOrderableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ProblemDetail handleNotFound(OrderNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(OrderNotOwnedByRestaurantException.class)
    ProblemDetail handleOrderNotOwned(OrderNotOwnedByRestaurantException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,exception.getMessage());
    }

    @ExceptionHandler(InvalidOrderTransitionException.class)
   ProblemDetail handleInvalidTransition(InvalidOrderTransitionException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
