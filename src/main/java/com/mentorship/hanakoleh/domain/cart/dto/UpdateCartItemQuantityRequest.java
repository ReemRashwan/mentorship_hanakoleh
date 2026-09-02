package com.mentorship.hanakoleh.domain.cart.dto;

import com.mentorship.hanakoleh.exception.ErrorCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartItemQuantityRequest {

    @NotNull(message = ErrorCode.QUANTITY_REQUIRED_MESSAGE)
    @Min(value = 1, message = ErrorCode.QUANTITY_MUST_BE_POSITIVE_MESSAGE)
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
