package com.mentorship.hanakoleh.domain.restaurant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDTO {
    private Integer customerId;
    private Integer orderId;
    private Integer restaurantId;
}
