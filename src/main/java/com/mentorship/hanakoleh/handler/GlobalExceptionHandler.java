package com.mentorship.hanakoleh.handler;

import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.exception.ItemUnavailableException;
import com.mentorship.hanakoleh.domain.restaurant.exception.InvalidRestaurantIdException;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.CustomerNotFoundException;
import com.mentorship.hanakoleh.domain.user.exception.UserTokenNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserTokenNotFoundException.class)
    public ResponseEntity<?> handleUserTokenNotFoundException() {
        ResponseMessage userTokenNotFoundMessage = new ResponseMessage(
                "Unauthorized User Access",
                "User Access Denied due to lack of credentials",
                LocalDateTime.now());
        return new ResponseEntity<>(userTokenNotFoundMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> handleCustomerNotFoundException() {
        ResponseMessage customerNotFoundMessage = new ResponseMessage(
                "Customer Not Found",
                "Customer does not exist",
                LocalDateTime.now());
        return new ResponseEntity<>(customerNotFoundMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<?> handleCartNotFoundException() {
        ResponseMessage cartNotFoundMessage = new ResponseMessage(
                "Cart Not Found",
                "Cart does not exist",
                LocalDateTime.now());
        return new ResponseEntity<>(cartNotFoundMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<?> handleRestaurantNotFoundException() {
        ResponseMessage restaurantNotFoundMessage = new ResponseMessage(
                "Restaurant Not Found",
                "Restaurant does not exist",
                LocalDateTime.now());
        return new ResponseEntity<>(restaurantNotFoundMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemUnavailableException.class)
    public ResponseEntity<?> handleItemUnavailableException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(LocalDateTime.now() + " : Sorry Item is unavailable in stock");
    }

    @ExceptionHandler(InvalidRestaurantIdException.class)
    public ResponseEntity<?> handleInvalidRestaurantIdException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LocalDateTime.now() + " :Authentication token missing");
    }

}

