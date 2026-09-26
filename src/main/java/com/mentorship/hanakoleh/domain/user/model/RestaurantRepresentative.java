package com.mentorship.hanakoleh.domain.user.model;

import com.mentorship.hanakoleh.domain.restaurant.model.Restaurant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "restaurant_representatives")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RestaurantRepresentative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_representative_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Builder.Default
    @Column(name = "representative_notification_status", nullable = false)
    @NotNull
    private Boolean notificationStatus = true;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof com.mentorship.hanakoleh.domain.user.model.RestaurantRepresentative)) {
            return false;
        }
        com.mentorship.hanakoleh.domain.user.model.RestaurantRepresentative restaurantRepresentative = (com.mentorship.hanakoleh.domain.user.model.RestaurantRepresentative) other;
        return id != null && id.equals(restaurantRepresentative.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
