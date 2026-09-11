package com.mentorship.hanakoleh.exception;

import com.mentorship.hanakoleh.domain.cart.exception.CartItemNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.OperationNotAllowedException;
import com.mentorship.hanakoleh.domain.checkout.exception.CartNotActiveException;
import com.mentorship.hanakoleh.domain.checkout.exception.EmptyCartException;
import com.mentorship.hanakoleh.domain.restaurant.exception.MenuItemNotOrderableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.mentorship.hanakoleh.domain.checkout.exception.AddressNotFoundException;
import com.mentorship.hanakoleh.domain.checkout.exception.InvalidDeliveryAddressException;
import com.mentorship.hanakoleh.domain.checkout.exception.DeliveryOptionNotAvailableException;
import com.mentorship.hanakoleh.domain.checkout.exception.OutOfDeliveryZoneException;
import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotApplicableException;
import com.mentorship.hanakoleh.domain.checkout.exception.PromotionNotFoundException;

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

    @ExceptionHandler(EmptyCartException.class)
    public ProblemDetail handleEmptyCart(EmptyCartException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(CartNotActiveException.class)
    public ProblemDetail handleCartNotActive(CartNotActiveException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ProblemDetail handleAddressNotFound(AddressNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(InvalidDeliveryAddressException.class)
    public ProblemDetail handleInvalidDeliveryAddress(InvalidDeliveryAddressException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }

    @ExceptionHandler(DeliveryOptionNotAvailableException.class)
    public ProblemDetail handleDeliveryOptionNotAvailable(DeliveryOptionNotAvailableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(OutOfDeliveryZoneException.class)
    public ProblemDetail handleOutOfDeliveryZone(OutOfDeliveryZoneException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(PromotionNotFoundException.class)
    public ProblemDetail handlePromotionNotFound(PromotionNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PromotionNotApplicableException.class)
    public ProblemDetail handlePromotionNotApplicable(PromotionNotApplicableException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
    }
}
