package com.mentorship.hanakoleh.domain.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "order_tracking")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_tracking_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_tracking_notes")
    @Size(max = 1000)
    private String notes;

    @Column(name = "order_tracking_created_by_user_id")
    private Integer triggeredByCustomerId;

    @Column(name = "order_tracking_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderTracking)) {
            return false;
        }
        OrderTracking orderTracking = (OrderTracking) other;
        return id != null && id.equals(orderTracking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
