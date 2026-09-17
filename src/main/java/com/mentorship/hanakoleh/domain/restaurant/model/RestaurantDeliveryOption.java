package com.mentorship.hanakoleh.domain.restaurant.model;

import com.mentorship.hanakoleh.domain.order.model.OrderDeliveryOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant_delivery_option")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RestaurantDeliveryOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_option", nullable = false, length = 30)
    @NotNull
    private OrderDeliveryOption deliveryOption;

    @Builder.Default
    @Column(name = "additional_fee", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal additionalFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "time_modifier_mins", nullable = false)
    @NotNull
    @Min(0)
    private Integer timeModifierMins = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    @NotNull
    private Boolean isActive = true;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RestaurantDeliveryOption option)) {
            return false;
        }
        return id != null && id.equals(option.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}