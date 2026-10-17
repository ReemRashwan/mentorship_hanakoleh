package com.mentorship.hanakoleh.domain.restaurant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.mentorship.hanakoleh.domain.restaurant.dto.RestaurantSummaryResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RestaurantSummaryMapper Unit Tests")
class RestaurantSummaryMapperTest {

    private final RestaurantSummaryMapper mapper = new RestaurantSummaryMapper();

    @Test
    @DisplayName("maps all fields incl category name")
    void toSummary_withCategory() {
        RestaurantCategory category = RestaurantCategory.builder().id(2).name("Fast Food").build();
        Restaurant r = Restaurant.builder()
                .id(1).name("Burger Town").category(category)
                .rating(new BigDecimal("4.80")).reviewCount(200).build();

        RestaurantSummaryResponse dto = mapper.toSummary(r);

        assertThat(dto.restaurantId()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Burger Town");
        assertThat(dto.category()).isEqualTo("Fast Food");
        assertThat(dto.rating()).isEqualByComparingTo("4.80");
        assertThat(dto.reviewCount()).isEqualTo(200);
    }

    @Test
    @DisplayName("null category maps to null")
    void toSummary_nullCategory() {
        Restaurant r = Restaurant.builder()
                .id(5).name("Mystery Kitchen").category(null)
                .rating(new BigDecimal("3.00")).reviewCount(0).build();

        assertThat(mapper.toSummary(r).category()).isNull();
    }
}
