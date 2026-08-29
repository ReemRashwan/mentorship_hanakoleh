package com.mentorship.hanakoleh.controller;


import com.mentorship.hanakoleh.controller.DTO.CartDTO;
import com.mentorship.hanakoleh.controller.DTO.CartItemDTO;
import com.mentorship.hanakoleh.exception.UserNotFoundException;
import com.mentorship.hanakoleh.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    CartService cartService;


    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> requestAddMenuItemToCart(@Valid @RequestBody CartItemDTO cartItemDTO,
                                                                Authentication authentication) {// throws InterruptedException {

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            //Authenticated User Flow
            Integer userId = (Integer) authentication.getPrincipal();
            CartItemDTO addedCartItem =
                    cartService.addItemToCart(cartItemDTO, userId);
            return new ResponseEntity<>(addedCartItem,
                    HttpStatus.CREATED);
        } else {
            throw new UserNotFoundException("user not authenticated");
        }

    }

    @DeleteMapping("/items")
    public ResponseEntity<CartDTO> requestClearCart(Integer userId) {

        CartDTO clearedCart = cartService.clearCart(userId);
        return new ResponseEntity<>(clearedCart, HttpStatus.OK);

    }

}
