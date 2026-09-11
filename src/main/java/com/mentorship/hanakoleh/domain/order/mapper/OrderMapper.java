package com.mentorship.hanakoleh.domain.order.mapper;


import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.restaurant.name", target = "restaurantName")
    @Mapping(source = "order.finalStatus", target = "status")
    @Mapping(source = "order.totalAmount", target = "totalAmount")
    @Mapping(source = "order.currencyCode", target = "currencyCode")
    @Mapping(source = "itemLineCount", target = "itemLineCount")
    @Mapping(source = "order.createdAt", target = "createdAt")
    OrderHistoryResponse toOrderHistoryResponse(Order order, long itemLineCount);
}
