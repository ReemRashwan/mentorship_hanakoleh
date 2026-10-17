package com.mentorship.hanakoleh.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.order.repository.OrderRepository;
import com.mentorship.hanakoleh.domain.restaurant.dto.RecommendedRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RecommendedRestaurantMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantRecommendationService Unit Tests")
class RestaurantRecommendationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RecommendedRestaurantMapper recommendedRestaurantMapper;

    @InjectMocks
    private RestaurantRecommendationService service;

    private final Integer customerId = 101;

    private Restaurant restaurant(Integer id, String name) {
        return Restaurant.builder().id(id).name(name).rating(new BigDecimal("4.50")).reviewCount(10).build();
    }

    @Test
    @DisplayName("has history: re-order tier first, then top-rated fill, both reasons, limit respected")
    void recommendations_withHistory() {
        Restaurant r2 = restaurant(2, "Prev A");
        Restaurant r3 = restaurant(3, "Prev B");
        Restaurant r5 = restaurant(5, "Popular");
        // tier 1 ids (recency order)
        when(orderRepository.findDistinctRestaurantIdsOrderedByRecency(eq(customerId), any(Pageable.class)))
                .thenReturn(List.of(2, 3));
        when(restaurantRepository.findAllById(List.of(2, 3))).thenReturn(List.of(r3, r2)); // unordered
        // tier 2 fill (limit 3 -> 1 remaining)
        when(restaurantRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(r5)));
        when(recommendedRestaurantMapper.toResponse(eq(r2), eq("You ordered here before")))
                .thenReturn(new RecommendedRestaurantResponse(2, "Prev A", null, null, "You ordered here before"));
        when(recommendedRestaurantMapper.toResponse(eq(r3), eq("You ordered here before")))
                .thenReturn(new RecommendedRestaurantResponse(3, "Prev B", null, null, "You ordered here before"));
        when(recommendedRestaurantMapper.toResponse(eq(r5), eq("Popular right now")))
                .thenReturn(new RecommendedRestaurantResponse(5, "Popular", null, null, "Popular right now"));

        List<RecommendedRestaurantResponse> result = service.getRecommendations(customerId, 3);

        assertThat(result).extracting(RecommendedRestaurantResponse::restaurantId)
                .containsExactly(2, 3, 5); // tier1 preserves recency order, then fill
        assertThat(result).extracting(RecommendedRestaurantResponse::reason)
                .containsExactly("You ordered here before", "You ordered here before", "Popular right now");
    }

    @Test
    @DisplayName("no history: falls back entirely to top-rated with the popular reason")
    void recommendations_noHistory_fallsBackToTopRated() {
        when(orderRepository.findDistinctRestaurantIdsOrderedByRecency(eq(customerId), any(Pageable.class)))
                .thenReturn(List.of());
        Restaurant r5 = restaurant(5, "Popular");
        when(restaurantRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(r5)));
        when(recommendedRestaurantMapper.toResponse(eq(r5), eq("Popular right now")))
                .thenReturn(new RecommendedRestaurantResponse(5, "Popular", null, null, "Popular right now"));

        List<RecommendedRestaurantResponse> result = service.getRecommendations(customerId, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).reason()).isEqualTo("Popular right now");
        verify(restaurantRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("when history already fills the limit, no top-rated fill is queried")
    void recommendations_historyFillsLimit_noFill() {
        when(orderRepository.findDistinctRestaurantIdsOrderedByRecency(eq(customerId), any(Pageable.class)))
                .thenReturn(List.of(2));
        Restaurant r2 = restaurant(2, "Prev A");
        when(restaurantRepository.findAllById(List.of(2))).thenReturn(List.of(r2));
        when(recommendedRestaurantMapper.toResponse(eq(r2), anyString()))
                .thenReturn(new RecommendedRestaurantResponse(2, "Prev A", null, null, "You ordered here before"));

        List<RecommendedRestaurantResponse> result = service.getRecommendations(customerId, 1);

        assertThat(result).hasSize(1);
        verify(restaurantRepository, never()).findAll(any(Specification.class), any(PageRequest.class));
    }
}
