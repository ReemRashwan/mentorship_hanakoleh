package com.mentorship.hanakoleh.domain.cart.mapper;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.cart.dto.response.RemoveCartItemResponse;
import com.mentorship.hanakoleh.domain.cart.model.Cart;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

     // Update cart item quantity
    @Mapping(source = "id", target = "cartItemId")
    UpdateCartItemQuantityResponse toUpdateQuantityResponse(CartItem cartItem);
    // Remove cart item
    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "itemId", source = "removedItemId")
    @Mapping(target = "totalItems", expression = "java(cart.getItems() == null ? 0 : cart.getItems().size())")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cart))")
    @Mapping(target = "restaurantId", expression = "java(cart.getRestaurant() == null || cart.getRestaurant().getId() == null ? null : cart.getRestaurant().getId().longValue())")
    RemoveCartItemResponse toResponse(Cart cart, Long removedItemId);

    // Helper methods
    default BigDecimal calculateSubtotal(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO.setScale(2);
        }

        return cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2);
    }
}
