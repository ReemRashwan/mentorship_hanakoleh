package com.mentorship.hanakoleh.domain.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "restaurant")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Integer id;

    @Column(name = "restaurant_name", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "restaurant_phone", length = 50)
    @Size(max = 50)
    private String phone;

    @Column(name = "restaurant_email", length = 255)
    @Email
    @Size(max = 255)
    private String email;

    @Column(name = "restaurant_rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "restaurant_longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "restaurant_latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "restaurant_avg_preparation_time_in_mins")
    private Integer avgPreparationTimeInMins;

    @Column(name = "restaurant_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }

        if (rating == null) {
            rating = BigDecimal.ZERO;
        }

        if (avgPreparationTimeInMins == null) {
            avgPreparationTimeInMins = 0;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Restaurant restaurant)) {
            return false;
        }
        return id != null && id.equals(restaurant.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
