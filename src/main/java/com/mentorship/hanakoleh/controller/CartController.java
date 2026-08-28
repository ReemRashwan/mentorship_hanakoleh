package com.mentorship.hanakoleh.controller;


import com.mentorship.hanakoleh.controller.DTO.CartItemDTO;
import com.mentorship.hanakoleh.domain.cart.Cart;
import com.mentorship.hanakoleh.domain.cart.CartItem;
import com.mentorship.hanakoleh.domain.restaurant.MenuItem;
import com.mentorship.hanakoleh.domain.user.GuestUser;
import com.mentorship.hanakoleh.service.CartService;
import com.mentorship.hanakoleh.exceptions.UserTokenNotFoundException;
import com.mentorship.hanakoleh.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    CartService cartService;
    @Autowired
    UserService userService;

    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> requestAddMenuItemToCart (@Valid @RequestBody CartItemDTO cartItemDTO,
                                                              Authentication authentication,
                                                              @CookieValue(name = "guest_user_token", required = false) String guestUserToken) {// throws InterruptedException {

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            //Authenticated User Flow
            Integer userId = (Integer) authentication.getPrincipal();
            CartItemDTO addedCartItem =
                    cartService.addItemToCart(cartItemDTO,userId);
            return new ResponseEntity<>(addedCartItem,
                    HttpStatus.CREATED);
        }
        else {
            throw new UsernameNotFoundException("user not authenticated");
//            GuestUser guestUser;
//            // Guest flow: verify existing token or generate guest token for first time users
//            if (guestUserToken == null || guestUserToken.isBlank()) {
//                guestUser = userService.createNewGuestUser();
//                // Return updated guest cookie in response if needed
//            } else {
//                guestUser = userService.retreiveGuestUserIdFromToken(guestUserToken);
//                if(guestUser == null){
//                    throw new UserTokenNotFoundException("Invalid guest user token");
//                }
//            }
//            MenuItem addedMenuItem =
//                    cartService.addMenuItemToCart(menuItem,guestUser.getGuestUserId().getId());
//            return new ResponseEntity<>(addedMenuItem,
//                    HttpStatus.CREATED);
        }

    }

}
