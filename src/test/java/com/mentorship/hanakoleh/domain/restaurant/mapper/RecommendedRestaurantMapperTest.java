package com.mentorship.hanakoleh.domain.restaurant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.mentorship.hanakoleh.domain.restaurant.dto.RecommendedRestaurantResponse;
import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import com.mentorship.hanakoleh.domain.restaurant.model.RestaurantCategory;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RecommendedRestaurantMapper Unit Tests")
class RecommendedRestaurantMapperTest {

    private final RecommendedRestaurantMapper mapper = new RecommendedRestaurantMapper();

    @Test
    @DisplayName("maps fields and passes the reason through; category from linked category")
    void toResponse_withCategory() {
        RestaurantCategory category = RestaurantCategory.builder().id(1).name("Egyptian").build();
        Restaurant r = Restaurant.builder().id(1).name("Koshary El Tahrir")
                .category(category).rating(new BigDecimal("4.50")).reviewCount(120).build();

        RecommendedRestaurantResponse dto = mapper.toResponse(r, "You ordered here before");

        assertThat(dto.restaurantId()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("Koshary El Tahrir");
        assertThat(dto.category()).isEqualTo("Egyptian");
        assertThat(dto.rating()).isEqualByComparingTo("4.50");
        assertThat(dto.reason()).isEqualTo("You ordered here before");
    }

    @Test
    @DisplayName("null category maps to null")
    void toResponse_nullCategory() {
        Restaurant r = Restaurant.builder().id(2).name("Mystery").category(null)
                .rating(new BigDecimal("3.00")).reviewCount(0).build();

        assertThat(mapper.toResponse(r, "Popular right now").category()).isNull();
    }
}
