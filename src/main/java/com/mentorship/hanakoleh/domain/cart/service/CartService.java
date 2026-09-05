package com.mentorship.hanakoleh.domain.cart.service;


import com.mentorship.hanakoleh.domain.cart.dto.ClearCartResponse;
import com.mentorship.hanakoleh.domain.cart.exception.CartNotFoundException;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartStatus;
import com.mentorship.hanakoleh.domain.cart.repository.CartRepository;
import com.mentorship.hanakoleh.domain.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerService customerService;

    @Transactional
    public ClearCartResponse clearCart(Integer userId) {
        Integer customerId = customerService.retrieveCustomerIdByUserId(userId);
        Cart customerCart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No Active Cart found for Customer Id " + customerId));
        customerCart.getItems().clear();
        return ClearCartResponse.builder()
                .cartId(customerCart.getId())
                .customerId(customerCart.getCustomer().getId())
                .restaurantId(customerCart.getRestaurant() != null ? customerCart.getRestaurant().getId() : null)
                .build();
    }
}