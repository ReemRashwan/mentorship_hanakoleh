package com.mentorship.hanakoleh.controller.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CartDTO {
    private Integer cartId;
    private Integer customerId;
    private Integer restaurantId;
}
