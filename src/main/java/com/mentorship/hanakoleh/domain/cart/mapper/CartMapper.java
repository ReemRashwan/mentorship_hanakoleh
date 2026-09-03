package com.mentorship.hanakoleh.domain.cart.mapper;

import com.mentorship.hanakoleh.domain.cart.dto.UpdateCartItemQuantityResponse;
import com.mentorship.hanakoleh.domain.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "cartItemId")
    @Mapping(source = "menuItem.id", target = "menuItemId")
    UpdateCartItemQuantityResponse toUpdateQuantityResponse(CartItem cartItem);
}
