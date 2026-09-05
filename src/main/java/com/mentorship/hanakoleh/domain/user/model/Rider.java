package com.mentorship.hanakoleh.domain.user.model;

import com.mentorship.hanakoleh.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rider")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "rider_national_id", unique = true, length = 20)
    @Size(max = 20)
    private String nationalId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "rider_vehicle_type", nullable = false, length = 20)
    @NotNull
    private RiderVehicleType riderVehicleType = RiderVehicleType.MOTORCYCLE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "rider_status", nullable = false, length = 20)
    @NotNull
    private RiderStatus status = RiderStatus.OFFLINE;

    @Column(name = "rider_current_latitude", precision = 9, scale = 6)
    private BigDecimal currentLatitude;

    @Column(name = "rider_current_longitude", precision = 9, scale = 6)
    private BigDecimal currentLongitude;

    @Column(name = "rider_location_updated_at")
    private OffsetDateTime locationUpdatedAt;

    @Column(name = "rider_active_governorate", length = 50)
    @Size(max = 50)
    private String activeGovernorate;

    @Column(name = "rider_created_at", nullable = false, updatable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(name = "rider_updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    private void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Rider)) {
            return false;
        }
        Rider rider = (Rider) other;
        return id != null && id.equals(rider.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
