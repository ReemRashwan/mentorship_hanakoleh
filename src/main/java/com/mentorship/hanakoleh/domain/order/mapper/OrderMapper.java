package com.mentorship.hanakoleh.domain.order.mapper;


import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "restaurant.name", target = "restaurantName")
    @Mapping(source = "finalStatus", target = "status")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "currencyCode", target = "currencyCode")
    @Mapping(source = "itemLineCount", target = "itemLineCount")
    @Mapping(source = "createdAt", target = "createdAt")
    OrderHistoryResponse toOrderHistoryResponse(Order order, long itemLineCount);
}
