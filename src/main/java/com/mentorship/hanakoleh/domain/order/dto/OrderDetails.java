package com.mentorship.hanakoleh.domain.order.dto;

import com.mentorship.hanakoleh.domain.order.model.Order;
import com.mentorship.hanakoleh.domain.order.model.OrderItem;
import java.util.List;

public record OrderDetails(Order order, List<OrderItem> items) {
}
