package com.mentorship.hanakoleh.domain.restaurant.service;

import com.mentorship.hanakoleh.domain.restaurant.dto.AddRestaurantRequest;
import com.mentorship.hanakoleh.domain.restaurant.dto.AddRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantDTO;
import com.mentorship.hanakoleh.domain.restaurant.dto.UpdateRestaurantRequest;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantCategoryNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.exception.RestaurantNotFoundException;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RestaurantMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantSearchCriteria;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantStatus;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantCategoryRepository;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import com.mentorship.hanakoleh.domain.user.exception.UserAccessDeniedException;
import com.mentorship.hanakoleh.domain.user.exception.UserNotFoundException;
import com.mentorship.hanakoleh.domain.user.repository.RestaurantRepresentativeRepository;
import com.mentorship.hanakoleh.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantManagementServiceTest {
    private static final String RESTAURANT_REPRESENTATIVE = "restaurant_representative";
    private static final String ADMIN = "admin";
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    RestaurantCategoryRepository restaurantCategoryRepository;
    @Mock
    private RestaurantRepresentativeRepository representativeRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private RestaurantService restaurantService;
    @Mock
    RestaurantMapper restaurantMapper;


    private Integer RESTAURANT_ID = 10;
    private Integer USER_ID = 100;
    private RestaurantCategory existingCategory = RestaurantCategory.builder().id(101).name("Burgers").build();

    private Restaurant existingRestaurant;


    private void givenRestaurantExists() {
        when(restaurantRepository.findById(RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));
    }

    private void givenUserIsRepresentative() {
        when(representativeRepository.existsByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(true);
    }
    @Nested
    @DisplayName("Tests for Add Restaurant")
    class AddRestaurantTests{
        private final Integer restaurantCategoryId = 10;
        private AddRestaurantRequest addRestaurantRequest;
        private RestaurantCategory restaurantCategory;
        @BeforeEach
        void setUp(){
            restaurantCategory=RestaurantCategory.builder().id(restaurantCategoryId).name("Burgers").build();
            addRestaurantRequest = new AddRestaurantRequest(
                    "Pizza Place", "+201234567890", "pizza@example.com",
                    new BigDecimal("31.2357"), new BigDecimal("30.0444"),
                    new BigDecimal("5.0"), 25, restaurantCategoryId);        }
        @Test
        @DisplayName("Successfully add a new Restaurant, when user is an Admin")
        void shouldSuccessfullyAddNewRestaurant_WhenUserIsAdmin(){
            //Given
            when(restaurantCategoryRepository.getReferenceById(restaurantCategoryId)).thenReturn(restaurantCategory);
            when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv ->{
                Restaurant savedRestaurant = inv.getArgument(0);
                savedRestaurant.setId(RESTAURANT_ID);
                return savedRestaurant;
            });
            //When
            AddRestaurantResponse addRestaurantResponse = restaurantService.addRestaurant(USER_ID,addRestaurantRequest);
            //Then
            verify(userService,times(1)).verifyUserType(USER_ID,ADMIN);
            ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);
            verify(restaurantRepository).save(captor.capture());
            Restaurant saved = captor.getValue();
            assertEquals("Pizza Place", saved.getName());
            assertEquals(restaurantCategoryId, saved.getCategory().getId());
            assertNotNull(saved.getCreatedAt());
            assertEquals(10, addRestaurantResponse.restaurantId());
            assertEquals("Pizza Place", addRestaurantResponse.restaurantName());
        }

        @Test
        @DisplayName("Fail adding a new Restaurant, when user is not an Admin")
        void shouldNotAddNewRestaurant_WhenUserNotAdmin(){
            //Given
            doThrow( new UserAccessDeniedException("You are not allowed to perform this action")).
            when(userService).verifyUserType(USER_ID,ADMIN);
            //When
            //Then
            Exception exception = assertThrows(UserAccessDeniedException.class, ()->restaurantService.addRestaurant(USER_ID,addRestaurantRequest));
            assertNotNull(exception);
            assertEquals("You are not allowed to perform this action", exception.getMessage());
            verifyNoInteractions(restaurantRepository,restaurantCategoryRepository);

        }

        @Test
        @DisplayName("Fail adding a new Restaurant, when user does not exist")
        void shouldNotAddRestaurant_WhenUserDoesNotExist() {
            doThrow(new UserNotFoundException("User with ID "+ USER_ID +" not found."))
                    .when(userService).verifyUserType(USER_ID, ADMIN);

            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> restaurantService.addRestaurant(USER_ID, addRestaurantRequest));
            assertNotNull(exception);
            assertEquals("User with ID "+ USER_ID +" not found.",exception.getMessage());
            verifyNoInteractions(restaurantRepository, restaurantCategoryRepository);
        }
    }

    @Nested
    @DisplayName(("Tests for Update Restaurant"))
    class UpdateRestaurantTests{
        private RestaurantCategory newRestaurantCategory = RestaurantCategory.builder().id(102).name("Pizza").build();

        @BeforeEach
        void setUp() {

            existingRestaurant = Restaurant.builder()
                    .id(RESTAURANT_ID).name("Old Rest Name").phone("+201000000000").email("old@oldrest.com")
                    .longitude(new BigDecimal("31.0")).latitude(new BigDecimal("30.0"))
                    .avgPreparationTimeInMins(20).deliveryRadiusKm(new BigDecimal("5.0"))
                    .category(existingCategory).status(RestaurantStatus.ACTIVE)
                    .build();
        }


        @Test
        @DisplayName("Update restaurant fields when all are provided")
        void updateRestaurant_updatesAllFields() {
            givenRestaurantExists();
            givenUserIsRepresentative();
            when(restaurantCategoryRepository.findById(102)).thenReturn(Optional.of(newRestaurantCategory));
            var updateRestaurantRequest = UpdateRestaurantRequest.builder()
                    .name("New Rest Name").phone("+2499999999999").email("new@newrest.com")
                    .longitude(new BigDecimal("32.0")).latitude(new BigDecimal("31.0"))
                    .avgPreparationTimeInMins(35).deliveryRadiusKm(new BigDecimal("8.5"))
                    .categoryId(newRestaurantCategory.getId()).build();

            var response = restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID, updateRestaurantRequest);

            assertEquals("New Rest Name", existingRestaurant.getName());
            assertEquals("+2499999999999", existingRestaurant.getPhone());
            assertEquals("new@newrest.com", existingRestaurant.getEmail());
            assertEquals(new BigDecimal("32.0"), existingRestaurant.getLongitude());
            assertEquals(new BigDecimal("31.0"), existingRestaurant.getLatitude());
            assertEquals(35, existingRestaurant.getAvgPreparationTimeInMins());
            assertEquals(new BigDecimal("8.5"), existingRestaurant.getDeliveryRadiusKm());
            assertSame(newRestaurantCategory, existingRestaurant.getCategory());
            assertNotNull(existingRestaurant.getUpdatedAt());

            verify(userService).verifyUserType(USER_ID, RESTAURANT_REPRESENTATIVE);
            verify(restaurantRepository).save(existingRestaurant);
            assertEquals(RESTAURANT_ID, response.id());   // adjust accessors
            assertEquals("New Rest Name", response.name());
        }

        @Test
        @DisplayName("Partial update changes only the provided fields")
        void updateRestaurant_partialUpdate_leavesOtherFieldsUntouched() {
            givenRestaurantExists();
            givenUserIsRepresentative();
            var request = UpdateRestaurantRequest.builder().name("Only Name Changed").build();

            restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID, request);

            assertEquals("Only Name Changed", existingRestaurant.getName());
            assertEquals("+201000000000", existingRestaurant.getPhone());
            assertEquals("old@oldrest.com", existingRestaurant.getEmail());
            assertEquals(new BigDecimal("31.0"), existingRestaurant.getLongitude());
            assertEquals(20, existingRestaurant.getAvgPreparationTimeInMins());
            assertSame(existingCategory, existingRestaurant.getCategory());
            verifyNoInteractions(restaurantCategoryRepository);   // no category lookup when categoryId is null
            verify(restaurantRepository).save(existingRestaurant);
        }

        @Test
        @DisplayName("Empty request changes no fields")
        void updateRestaurant_emptyRequest_changesNothing() {
            givenRestaurantExists();
            givenUserIsRepresentative();

            restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID, UpdateRestaurantRequest.builder().build());

            assertEquals("Old Rest Name", existingRestaurant.getName());
            assertEquals("old@oldrest.com", existingRestaurant.getEmail());
            verifyNoInteractions(restaurantCategoryRepository);
        }


        @Test
        @DisplayName("Fails when the restaurant does not exist")
        void updateRestaurant_restaurantNotFound() {
            when(restaurantRepository.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            Exception exception = assertThrows(RestaurantNotFoundException.class, () ->
                    restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID,
                            UpdateRestaurantRequest.builder().name("Restaurant that doesn't exist in the DB").build()));

            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("Fails when the user is not a restaurant_representative type")
        void updateRestaurant_UserUnAuthorized() {
            givenRestaurantExists();
            doThrow(new UserAccessDeniedException("Access Denied. Cannot Update"))
                    .when(userService).verifyUserType(USER_ID, RESTAURANT_REPRESENTATIVE);

            Exception exception = assertThrows(UserAccessDeniedException.class, () ->
                    restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID,
                            UpdateRestaurantRequest.builder().name("The new Restaurant Name").build()));
            verifyNoInteractions(representativeRepository);
            verify(restaurantRepository, never()).save(any());
            assertEquals("Old Rest Name", existingRestaurant.getName());
        }

        @Test
        @DisplayName("Fails when the user represents a different restaurant")
        void updateRestaurant_notRepresentativeOfThisRestaurant() {
            givenRestaurantExists();
            when(representativeRepository.existsByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(false);

            assertThrows(UserAccessDeniedException.class, () ->
                    restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID,
                            UpdateRestaurantRequest.builder().name("The new Restaurant Name").build()));

            verify(restaurantRepository,never()).save(any());
            assertEquals("Old Rest Name", existingRestaurant.getName());   // nothing was modified
        }

        @Test
        @DisplayName("Fails when the new category does not exist")
        void updateRestaurant_categoryNotFound() {
            givenRestaurantExists();
            givenUserIsRepresentative();
            when(restaurantCategoryRepository.findById(99)).thenReturn(Optional.empty());

            Exception exception = assertThrows(RestaurantCategoryNotFoundException.class, () ->
                    restaurantService.updateRestaurant(USER_ID, RESTAURANT_ID,
                            UpdateRestaurantRequest.builder().categoryId(99).build()));
            assertEquals("Restaurant category not found with ID: " + 99, exception.getMessage());

            verify(restaurantRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Enable Restaurant Tests")
    class EnableRestaurantTests{
        @BeforeEach
        void setUp() {

            existingRestaurant = Restaurant.builder()
                    .id(RESTAURANT_ID).name("Old Rest Name").phone("+201000000000").email("old@oldrest.com")
                    .longitude(new BigDecimal("31.0")).latitude(new BigDecimal("30.0"))
                    .avgPreparationTimeInMins(20).deliveryRadiusKm(new BigDecimal("5.0"))
                    .category(existingCategory).status(RestaurantStatus.ACTIVE)
                    .build();
        }

        @DisplayName("Successfully set Restaurant to Active State")
        @Test
        void enableRestaurant_setsStatusActive() {

            existingRestaurant.setStatus(RestaurantStatus.INACTIVE);

            givenRestaurantExists();
            givenUserIsRepresentative();

            restaurantService.enableRestaurant(USER_ID, RESTAURANT_ID);

            assertEquals(RestaurantStatus.ACTIVE, existingRestaurant.getStatus());
        }

        @DisplayName("leave Restaurant Status Unchanged when user is not representative")
        @Test
        void enableRestaurant_notRepresentative_leavesStatusUnchanged() {
            existingRestaurant.setStatus(RestaurantStatus.INACTIVE);

            when(representativeRepository.existsByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(false);

            assertThrows(UserAccessDeniedException.class,
                    () -> restaurantService.enableRestaurant(USER_ID, RESTAURANT_ID));

            assertEquals(RestaurantStatus.INACTIVE, existingRestaurant.getStatus());
            verify(restaurantRepository, never()).findById(any());   // authorization runs first here
        }

        @DisplayName("Requested Restaurant not Found")
        @Test
        void enableRestaurant_restaurantNotFound() {
            givenUserIsRepresentative();
            when(restaurantRepository.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class,
                    () -> restaurantService.enableRestaurant(USER_ID, RESTAURANT_ID));
        }

    }

    @Nested
    @DisplayName("Disable Restaurant Tests")
    class DisableRestaurantTests{
        @BeforeEach
        void setUp() {

            existingRestaurant = Restaurant.builder()
                    .id(RESTAURANT_ID).name("Old Rest Name").phone("+201000000000").email("old@oldrest.com")
                    .longitude(new BigDecimal("31.0")).latitude(new BigDecimal("30.0"))
                    .avgPreparationTimeInMins(20).deliveryRadiusKm(new BigDecimal("5.0"))
                    .category(existingCategory).status(RestaurantStatus.ACTIVE)
                    .build();
        }

        @DisplayName("Successfully Disable Restaurant and set to InActive State")
        @Test
        void disableRestaurant_setsStatusInactive() {
            existingRestaurant.setStatus(RestaurantStatus.ACTIVE);
            givenRestaurantExists();
            givenUserIsRepresentative();

            restaurantService.disableRestaurant(USER_ID, RESTAURANT_ID);

            assertEquals(RestaurantStatus.INACTIVE, existingRestaurant.getStatus());
        }

        @DisplayName("leave Restaurant Status Unchanged when user is not representative")
        @Test
        void disableRestaurant_notRepresentative_leavesStatusUnchanged() {
            existingRestaurant.setStatus(RestaurantStatus.ACTIVE);
            when(representativeRepository.existsByUserIdAndRestaurantId(USER_ID, RESTAURANT_ID)).thenReturn(false);

            assertThrows(UserAccessDeniedException.class,
                    () -> restaurantService.disableRestaurant(USER_ID, RESTAURANT_ID));

            assertEquals(RestaurantStatus.ACTIVE, existingRestaurant.getStatus());
        }

    }

    @Nested
    @DisplayName("List/Search Restaurant Tests")
    class ListSearchRestaurantTests{

        @BeforeEach
        void setUp() {

            existingRestaurant = Restaurant.builder()
                    .id(RESTAURANT_ID).name("Old Rest Name").phone("+201000000000").email("old@oldrest.com")
                    .longitude(new BigDecimal("31.0")).latitude(new BigDecimal("30.0"))
                    .avgPreparationTimeInMins(20).deliveryRadiusKm(new BigDecimal("5.0"))
                    .category(existingCategory).status(RestaurantStatus.ACTIVE)
                    .build();
        }

        @Test
        void searchRestaurants_mapsEntitiesToDTOs() {
            Pageable pageable = PageRequest.of(0, 10);
            RestaurantDTO dto = mock(RestaurantDTO.class);
            when(restaurantRepository.findAll(ArgumentMatchers.<Specification<Restaurant>>any(), eq(pageable)))
                    .thenReturn(new PageImpl<>(List.of(existingRestaurant), pageable, 1));
            when(restaurantMapper.toRestaurantDTO(existingRestaurant)).thenReturn(dto);

            Page<RestaurantDTO> result = restaurantService.searchRestaurants(
                    new RestaurantSearchCriteria(null, null, null, null, null), pageable);

            assertEquals(1, result.getTotalElements());
            assertSame(dto, result.getContent().get(0));
        }

        @Test
        void searchRestaurants_noMatches_returnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(restaurantRepository.findAll(ArgumentMatchers.<Specification<Restaurant>>any(), eq(pageable)))
                    .thenReturn(Page.empty(pageable));

            Page<RestaurantDTO> result = restaurantService.searchRestaurants(
                    new RestaurantSearchCriteria("Burger King", null, null, null, null), pageable);

            assertTrue(result.isEmpty());
            verifyNoInteractions(restaurantMapper);
        }

        @Test
        void listAllRestaurants_mapsEntitiesToDTOs() {
            Pageable pageable = PageRequest.of(0, 10);
            RestaurantDTO dto = mock(RestaurantDTO.class);
            when(restaurantRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(existingRestaurant), pageable, 1));
            when(restaurantMapper.toRestaurantDTO(existingRestaurant)).thenReturn(dto);

            Page<RestaurantDTO> result = restaurantService.listAllRestaurants(pageable);

            assertSame(dto, result.getContent().get(0));
        }
    }
}
