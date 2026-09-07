package com.mentorship.hanakoleh.domain.user.model;

import com.mentorship.hanakoleh.domain.user.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_customer_id", nullable = false)
    private Customer customer;

    @Column(name = "address_governorate", nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String governorate;

    @Column(name = "address_city", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String city;

    @Column(name = "address_district", length = 100)
    @Size(max = 100)
    private String district;

    @Column(name = "address_street", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String street;

    @Column(name = "address_building_number", length = 20)
    @Size(max = 20)
    private String buildingNumber;

    @Column(name = "address_floor", length = 20)
    @Size(max = 20)
    private String floor;

    @Column(name = "address_apartment", length = 20)
    @Size(max = 20)
    private String apartment;

    @Column(name = "address_landmark", length = 255)
    @Size(max = 255)
    private String landmark;

    @Column(name = "address_postal_code", length = 10)
    @Size(max = 10)
    private String postalCode;

    @Column(name = "address_latitude", precision = 9, scale = 6)
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private BigDecimal latitude;

    @Column(name = "address_longitude", precision = 9, scale = 6)
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private BigDecimal longitude;

    @Column(name = "address_label", length = 30)
    @Size(max = 30)
    private String label;

    @Builder.Default
    @Column(name = "address_is_default", nullable = false)
    @NotNull
    private Boolean isDefault = false;

    @Column(name = "address_created_at", nullable = false)
    @NotNull
    private OffsetDateTime createdAt;

    @Column(name = "address_updated_at", nullable = false)
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
        if (!(other instanceof Address)) {
            return false;
        }
        Address address = (Address) other;
        return id != null && id.equals(address.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
