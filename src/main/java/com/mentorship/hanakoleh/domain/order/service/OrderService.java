package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.constants.OrderConstants;
import com.mentorship.hanakoleh.domain.order.dto.UpdateOrderStatusResponse;
import com.mentorship.hanakoleh.domain.order.model.*;
import com.mentorship.hanakoleh.domain.order.dto.CancelOrderResponse;
import com.mentorship.hanakoleh.domain.order.dto.OrderHistoryResponse;
import com.mentorship.hanakoleh.domain.order.exception.InvalidOrderTransitionException;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotOwnedByCustomerException;
import com.mentorship.hanakoleh.domain.order.mapper.OrderMapper;
import com.mentorship.hanakoleh.domain.order.exception.OrderNotFoundException;
import com.mentorship.hanakoleh.domain.order.dto.OrderDetails;
import com.mentorship.hanakoleh.domain.order.repository.OrderItemRepository;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.order.projection.OrderItemLineCountProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.OffsetDateTime;

import com.mentorship.hanakoleh.domain.restaurant.model.*;
import com.mentorship.hanakoleh.domain.restaurant.repository.ItemCategoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import com.mentorship.hanakoleh.domain.user.model.*;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class OrderService {

    private static final List<OrderFinalStatus> NON_CURRENT_STATUSES = List.of(OrderFinalStatus.COMPLETED, OrderFinalStatus.CANCELLED, OrderFinalStatus.REFUNDED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderStatusUpdateService orderStatusUpdateService;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final ItemCategoryRepository itemCategoryRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderMapper orderMapper, OrderStatusUpdateService orderStatusUpdateService, UserRepository userRepository, CustomerRepository customerRepository, RestaurantRepository restaurantRepository, MenuRepository menuRepository, MenuItemRepository menuItemRepository, ItemCategoryRepository itemCategoryRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderStatusUpdateService = orderStatusUpdateService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.menuItemRepository = menuItemRepository;
        this.itemCategoryRepository = itemCategoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getHistoricalOrders(Integer customerId, Pageable pageable) {
        OffsetDateTime startDate = getHistoricalOrderStartDate();
        Page<Order> orders = orderRepository.findByCustomerIdAndCreatedAtGreaterThanEqual(customerId, startDate, pageable);
        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Long> orderIds = orders.getContent().stream().map(Order::getId).toList();

        List<OrderItemLineCountProjection> lineCounts = orderItemRepository.findLineCountByOrderIds(orderIds);

        Map<Long, Long> lineCountByOrderId = lineCounts.stream().collect(Collectors.toMap(OrderItemLineCountProjection::getOrderId, OrderItemLineCountProjection::getLineCount));

        return orders.map(order -> {
            long lineCount = lineCountByOrderId.getOrDefault(order.getId(), 0L);

            return orderMapper.toOrderHistoryResponse(order, lineCount);
        });

    }

    // Helper methods
    // Helper method to get the start date for the historical orders
    private OffsetDateTime getHistoricalOrderStartDate() {
        return OffsetDateTime.now().minusMonths(OrderConstants.NUMBER_OF_MONTHS_FOR_HISTORICAL_ORDERS);
    }

    @Transactional(readOnly = true)
    public List<Order> getCurrentOrders(Integer customerId) {
        return orderRepository.findByCustomerIdAndFinalStatusNotInOrderByCreatedAtDesc(customerId, NON_CURRENT_STATUSES);
    }

    @Transactional(readOnly = true)
    public OrderDetails getOrder(Long orderId, Integer customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return new OrderDetails(order, orderItemRepository.findByOrderIdOrderByIdAsc(orderId));
    }

    @Transactional
    public CancelOrderResponse cancelOrder(Integer cancelingActorUserId, Long orderId, OrderCancellationTrigger cancellationTrigger, String reason, String notes) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(String.format("Order with id %d not found", orderId)));
        switch (cancellationTrigger) {
            case CUSTOMER_CANCELLED -> {
                if (!order.getCustomer().getUser().getId().equals(cancelingActorUserId)) {
                    throw new OrderNotOwnedByCustomerException(orderId, cancelingActorUserId);
                }
                if (order.getFinalStatus() != OrderFinalStatus.CREATED && order.getFinalStatus() != OrderFinalStatus.CONFIRMED) {
                    throw new InvalidOrderTransitionException(order.getFinalStatus(), OrderFinalStatus.CANCELLED);
                }

            }
            case RESTAURANT_CANCELLED -> {
                if (reason == null || reason.isBlank()) {
                    throw new IllegalArgumentException("Reason is required for restaurant emergency cancellation");
                }
            }
            case SLA_BREACH -> {
                // verify breach against Order's stored timestamps needed
            }
        }
        orderStatusUpdateService.cancelOrder(cancelingActorUserId, orderId, notes);
        return CancelOrderResponse.builder().orderId(orderId).build();
    }

    public UpdateOrderStatusResponse changeOrderStatus(Integer customerId) {
        User mockUser = User.builder().userType(UserType.builder().id(customerId).name("Customer").description("Customer who purchase food online").build()).email("zeinab@google.om").phoneNumber("091091").firstName("Zeinab").lastName("Osman").language(Language.builder().id(1).name("Arabic").code("AR").build()).passwordHash("112233").joinedAt(OffsetDateTime.now()).lastLoginAt(OffsetDateTime.now().minusDays(4)).lastLoginStatus(LoginStatus.SUCCESS).build();
        userRepository.save(mockUser);
        Customer mockCustomer = Customer.builder().user(mockUser).notificationStatus(true).build();
        customerRepository.save(mockCustomer);
        Restaurant mockRestaurant = Restaurant.builder().name("GrillOnWheels").phone("123456").rating(BigDecimal.valueOf(1000)).longitude(BigDecimal.valueOf(15.577968879582503)).latitude(BigDecimal.valueOf(32.5685861095599)).avgPreparationTimeInMins(30).createdAt(OffsetDateTime.now()).build();
        restaurantRepository.save(mockRestaurant);
        Menu mockMenu = Menu.builder().restaurant(mockRestaurant).name("Meat Burgers").uiOrder(1).visible(true).build();
        menuRepository.save(mockMenu);
        ItemCategory mockItemCategory = ItemCategory.builder().name("Burgers").build();
        itemCategoryRepository.save(mockItemCategory);
        MenuItem mockMenuItem = MenuItem.builder().menu(mockMenu).category(mockItemCategory)
                .name("Juicy Lucy Double Cheese Burger")
                .price(BigDecimal.valueOf(15))
                .availableQuantity(5)
                .uiOrder(1)
                .onDemandStatus(MenuItemOnDemandStatus.AVAILABLE)
                .build();
        menuItemRepository.save(mockMenuItem);
        Rider mockRider = Rider.builder().user(mockUser)
                .nationalId("EG")
                .riderVehicleType(RiderVehicleType.MOTORCYCLE)
                .status(RiderStatus.AVAILABLE)// to be changed on delivery
                .currentLatitude(BigDecimal.valueOf(1231412))
                .currentLongitude(BigDecimal.valueOf(12313123)).
                locationUpdatedAt(OffsetDateTime.now())
                .activeGovernorate("Cairo")
                .createdAt(OffsetDateTime.now().minusMonths(3))
                .build();
        Address mockAddress = Address.builder().customer(mockCustomer).governorate("Cairo").city("Nasr City").district("Cairo").street("4").buildingNumber("5").floor("3").apartment("10").label("Home Address").isDefault(true).createdAt(OffsetDateTime.now()).build();

        Order activeOrder = Order.builder().idempotencyKey(UUID.randomUUID())
                .customer(mockCustomer)
                .restaurant(mockRestaurant)
                .rider(mockRider)
                .address(mockAddress)
                .deliveryOption(OrderDeliveryOption.DELIVERY)
                .finalStatus(OrderFinalStatus.CREATED)
                .paymentStatus(OrderPaymentStatus.PAID)
                .paymentMethod(OrderPaymentMethod.CREDIT_CARD)
                .currencyCode("EGP")
                .subtotal(BigDecimal.valueOf(30))
                .deliveryFees(BigDecimal.valueOf(10))
                .serviceFees(BigDecimal.valueOf(5))
                .taxAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(45))
                .estimatedDeliveryAt(OffsetDateTime.now().plusMinutes(30))
                .createdAt(OffsetDateTime.now())
                .build();
        orderStatusUpdateService.confirmOrder(activeOrder.getId(), "Order placed successfully");
        log.info("Order placed successfully");
        return new UpdateOrderStatusResponse(activeOrder.getId(),activeOrder.getFinalStatus());
    }
}
