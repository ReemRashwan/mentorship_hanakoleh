package com.mentorship.hanakoleh.domain.checkout.mapper;

import com.mentorship.hanakoleh.domain.cart.model.Cart;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import com.mentorship.hanakoleh.domain.checkout.dto.CartValidationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CheckoutCartMapper {

    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "totalItems", expression = "java(cart.getItems() == null ? 0 : cart.getItems().size())")
    @Mapping(target = "items", source = "items")
    CartValidationResponse toValidationResponse(Cart cart);

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "menuItemId", source = "menuItem.id")
    @Mapping(target = "menuItemName", source = "menuItem.name")
    @Mapping(target = "quantity", source = "quantity")
    CartValidationResponse.CartItemSummary toItemSummary(CartItem item);
}
