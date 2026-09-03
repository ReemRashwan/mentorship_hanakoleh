package com.mentorship.hanakoleh.domain.cart.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CartItemMapperTest {

    private final CartMapper cartMapper = Mappers.getMapper(CartMapper.class);

    @Test
    void shouldMapCartItemFieldsToTheResponse() {
        MenuItem menuItem = MenuItem.builder()
                .id(7)
                .name("Koshary Large")
                .price(new BigDecimal("55.00"))
                .build();
        CartItem cartItem = CartItem.builder()
                .id(1)
                .menuItem(menuItem)
                .quantity(3)
                .price(new BigDecimal("55.00"))
                .note("no onions")
                .build();

        UpdateCartItemQuantityResponse response = cartMapper.toUpdateQuantityResponse(cartItem);

        assertEquals(1, response.cartItemId());
        assertEquals(3, response.quantity());
        assertEquals(new BigDecimal("55.00"), response.price());
        assertEquals("no onions", response.note());
    }
}
