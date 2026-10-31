package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.order.event.OrderConfirmedEvent;
import com.mentorship.hanakoleh.domain.order.event.OrderPreparedEvent;
import com.mentorship.hanakoleh.domain.restaurant.dto.*;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantCategoryNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RestaurantMapper;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RestaurantSummaryMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.*;
import com.mentorship.hanakoleh.domain.restaurant.repository.MenuItemRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantCategoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import com.mentorship.hanakoleh.domain.user.exception.UserAccessDeniedException;
import com.mentorship.hanakoleh.domain.user.repository.RestaurantRepresentativeRepository;
import com.mentorship.hanakoleh.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.OffsetDateTime;
import java.util.Optional;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
@Slf4j
public class RestaurantService {
    public static final String RESTAURANT_REPRESENTATIVE = "restaurant_representative";
    public static final String ADMIN = "admin";
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final RestaurantCategoryRepository restaurantCategoryRepository;
    private final UserService userService;
    private final RestaurantRepresentativeRepository representativeRepository;
    private final RestaurantMapper restaurantMapper;



    public RestaurantService(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository, RestaurantCategoryRepository restaurantCategoryRepository, UserService userService, RestaurantRepresentativeRepository representativeRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.restaurantCategoryRepository = restaurantCategoryRepository;
        this.userService = userService;
        this.representativeRepository = representativeRepository;
        this.restaurantMapper = restaurantMapper;
    }

    public Restaurant getRestaurantReferenceById(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + restaurantId);
        }
        return restaurantRepository.getReferenceById(restaurantId);
    }

    public Optional<MenuItem> getMenuItemByMenuItemId(@NotEmpty Integer menuItemId) {
        return menuItemRepository.findById(menuItemId);
    }

    public Integer getMenuItemInventory(Integer menuItemId) {
        Optional<Integer> availableQuantity = menuItemRepository.findAvailableQuantityById(menuItemId);
        return availableQuantity.orElseThrow(() -> new EntityNotFoundException("Quantity for Menu Item with ID " + menuItemId + " not found."));
    }


    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingConfirmedOrders(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("Incoming Order{}that is {}is received. \n1-matching the order to correct restaurant. 2-checking restaurant status and ability to process.\n3-pushing notification to restaurant.", orderConfirmedEvent.getOrderId(), orderConfirmedEvent.getFinalStatus());
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handleIncomingDeliveredOrders(OrderPreparedEvent orderPreparedEvent) {
        log.info("Order {} that is {} is received. \n1-marking order as ready in restaurant dashboard. 2-releasing order for courier pickup eligibility.\n3-restaurant's job on this order effectively ends here.",
                orderPreparedEvent.getOrderId(), orderPreparedEvent.getFinalStatus());
    }

    @Transactional
    public AddRestaurantResponse addRestaurant(Integer userId, AddRestaurantRequest addRestaurantRequest) {
        userService.verifyUserType(userId, ADMIN);

        RestaurantCategory categoryRef =
                restaurantCategoryRepository.getReferenceById(addRestaurantRequest.restaurantCategoryId());
        Restaurant newRestaurant = Restaurant.builder().name(addRestaurantRequest.name())
                .phone(addRestaurantRequest.phone())
                .email(addRestaurantRequest.email())
                .longitude(addRestaurantRequest.longitude())
                .latitude(addRestaurantRequest.latitude())
                .avgPreparationTimeInMins(addRestaurantRequest.avgPreparationTimeInMins())
                .createdAt(OffsetDateTime.now())
                .deliveryRadiusKm(addRestaurantRequest.deliveryRadiusInKm())
                .category(categoryRef).build();
        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);

        return AddRestaurantResponse.builder()
                .restaurantId(savedRestaurant.getId())
                .restaurantName(savedRestaurant.getName())
                .categoryId(savedRestaurant.getCategory().getId())
                .categoryName(savedRestaurant.getCategory().getName()).build();
    }

    @Transactional
    public UpdateRestaurantResponse updateRestaurant(Integer userId, Integer restaurantId, UpdateRestaurantRequest request) {
        Restaurant updatedRestaurant = getRestaurantOrThrow(restaurantId);
        verifyUserIsRestaurantRepresentative(userId,updatedRestaurant.getId());
        if (request.name() != null)
            updatedRestaurant.setName(request.name());
        if (request.phone() != null)
            updatedRestaurant.setPhone(request.phone());
        if (request.email() != null)
            updatedRestaurant.setEmail(request.email());
        if (request.longitude() != null)
            updatedRestaurant.setLongitude(request.longitude());
        if (request.latitude() != null)
            updatedRestaurant.setLatitude(request.latitude());
        if (request.avgPreparationTimeInMins() != null)
            updatedRestaurant.setAvgPreparationTimeInMins(request.avgPreparationTimeInMins());
        if (request.deliveryRadiusKm() != null)
            updatedRestaurant.setDeliveryRadiusKm(request.deliveryRadiusKm());
        if (request.categoryId() != null) {
            RestaurantCategory category = getRestaurantCategoryOrThrow(request.categoryId());
            updatedRestaurant.setCategory(category);
        }
        updatedRestaurant.setUpdatedAt(OffsetDateTime.now());
        restaurantRepository.save(updatedRestaurant);
        return new UpdateRestaurantResponse(updatedRestaurant.getId(), updatedRestaurant.getName());
    }

    public void verifyUserIsRestaurantRepresentative(Integer userId, Integer restaurantId) {
        userService.verifyUserType(userId, RESTAURANT_REPRESENTATIVE);
        if (!representativeRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            log.warn("User {} attempted to modify restaurant {} without being its representative", userId, restaurantId);
            throw new UserAccessDeniedException("You are not allowed to modify this restaurant");
        }
    }

    private @NonNull RestaurantCategory getRestaurantCategoryOrThrow(Integer categoryId) {
        return restaurantCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RestaurantCategoryNotFoundException(
                        "Restaurant category not found with ID: " + categoryId));
    }

    Restaurant getRestaurantOrThrow(Integer restaurantId)
    {
        return getRestaurant(restaurantId).orElseThrow(()-> new RestaurantNotFoundException("Restaurant with ID "+restaurantId+"Not Found"));
    }

    Optional<Restaurant> getRestaurant(Integer restaurantId){
        return restaurantRepository.findById(restaurantId);
    }

    @Transactional
    public void enableRestaurant(Integer userId, Integer restaurantId) {
        verifyUserIsRestaurantRepresentative(userId,restaurantId);
        Restaurant toBeActiveRestaurant = getRestaurantOrThrow(restaurantId);
        toBeActiveRestaurant.setStatus(RestaurantStatus.ACTIVE);
    }

    @Transactional
    public void disableRestaurant(Integer userId, Integer restaurantId) {
        verifyUserIsRestaurantRepresentative(userId,restaurantId);
        Restaurant toBeActiveRestaurant = getRestaurantOrThrow(restaurantId);
        toBeActiveRestaurant.setStatus(RestaurantStatus.INACTIVE);
    }

    @Transactional (readOnly = true)
    public Page<RestaurantDTO> searchRestaurants(RestaurantSearchCriteria criteria, Pageable pageable) {
        return restaurantRepository
                .findAll(RestaurantSpecifications.from(criteria), pageable)
                .map(restaurantMapper::toRestaurantDTO);
    }

    @Transactional  (readOnly = true)
    public Page<RestaurantDTO> listAllRestaurants(Pageable pageable) {

        return restaurantRepository.findAll(pageable).map(restaurantMapper::toRestaurantDTO);
    }
}
