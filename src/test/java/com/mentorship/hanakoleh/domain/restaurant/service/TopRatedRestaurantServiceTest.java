package com.mentorship.hanakoleh.domain.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.mapper.RestaurantSummaryMapper;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopRatedRestaurantService Unit Tests")
class TopRatedRestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantSummaryMapper restaurantSummaryMapper;

    @InjectMocks
    private TopRatedRestaurantService service;

    private void stubEmpty() {
        when(restaurantRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    private PageRequest capturePageRequest() {
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(restaurantRepository).findAll(any(Specification.class), captor.capture());
        return captor.getValue();
    }

    @Test
    @DisplayName("sorts by rating desc then reviewCount desc (tiebreaker)")
    void topRated_sortOrder() {
        stubEmpty();

        service.getTopRated(null, null, 10);

        Sort sort = capturePageRequest().getSort();
        List<Sort.Order> orders = sort.toList();
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getProperty()).isEqualTo("rating");
        assertThat(orders.get(0).getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(orders.get(1).getProperty()).isEqualTo("reviewCount");
        assertThat(orders.get(1).getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("defaults limit to 10 when null")
    void topRated_defaultLimit() {
        stubEmpty();
        service.getTopRated(null, null, null);
        assertThat(capturePageRequest().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("caps limit at 50")
    void topRated_capsLimit() {
        stubEmpty();
        service.getTopRated(null, null, 500);
        assertThat(capturePageRequest().getPageSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("raises a limit below 1 up to 1")
    void topRated_minLimit() {
        stubEmpty();
        service.getTopRated(null, null, 0);
        assertThat(capturePageRequest().getPageSize()).isEqualTo(1);
    }

    @Test
    @DisplayName("maps results through the summary mapper")
    void topRated_mapsResults() {
        Restaurant r = Restaurant.builder().id(1).name("Burger Town")
                .rating(new BigDecimal("4.80")).reviewCount(200).build();
        when(restaurantRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(r)));
        when(restaurantSummaryMapper.toSummary(r)).thenReturn(
                new RestaurantSummaryResponse(1, "Burger Town", "Fast Food",
                        new BigDecimal("4.80"), 200));

        List<RestaurantSummaryResponse> result = service.getTopRated("Fast Food", 10, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Burger Town");
        verify(restaurantRepository).findAll(any(Specification.class), any(PageRequest.class));
    }
}
