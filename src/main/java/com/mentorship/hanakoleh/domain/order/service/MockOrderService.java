package com.mentorship.hanakoleh.domain.order.service;

import com.mentorship.hanakoleh.domain.order.model.dto.PlaceOrderResponse;
import com.mentorship.hanakoleh.domain.order.model.*;
import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.restaurant.model.*;
import com.mentorship.hanakoleh.domain.restaurant.repository.ItemCategoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import com.mentorship.hanakoleh.domain.user.model.*;
import com.mentorship.hanakoleh.domain.user.repository.CustomerRepository;
import com.mentorship.hanakoleh.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
///////////////////*****NOTE EVERY CODE HERE IS FOR TESTING PURPOSE ONLY*****//////////////////////////////////
public class MockOrderService {
    private final OrderStatusUpdateService orderStatusUpdateService;
    /// /////////Repos
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final MenuItemRepository menuItemRepository;

    public MockOrderService(OrderStatusUpdateService orderStatusUpdateService, UserRepository userRepository, CustomerRepository customerRepository, OrderRepository orderRepository, RestaurantRepository restaurantRepository, MenuRepository menuRepository, ItemCategoryRepository itemCategoryRepository, MenuItemRepository menuItemRepository) {
        this.orderStatusUpdateService = orderStatusUpdateService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.itemCategoryRepository = itemCategoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public PlaceOrderResponse placeOrder(Integer customerId) {
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
        return new PlaceOrderResponse(activeOrder.getId(),activeOrder.getFinalStatus());
    }
}
