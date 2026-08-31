package com.mentorship.hanakoleh.domain.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_category_id")
    private ItemCategory category;

    @Column(name = "menu_item_name", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "menu_item_image", length = 255)
    @Size(max = 255)
    private String image;

    @Column(name = "menu_item_price", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @Column(name = "menu_item_available_quantity")
    @Min(0)
    private Integer availableQuantity;

    @Builder.Default
    @Column(name = "menu_item_ui_order", nullable = false)
    @NotNull
    @Min(0)
    private Integer uiOrder = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "menu_item_on_demand_status", nullable = false, length = 50)
    @NotNull
    private MenuItemOnDemandStatus onDemandStatus = MenuItemOnDemandStatus.AVAILABLE;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MenuItem menuItem)) {
            return false;
        }
        return id != null && id.equals(menuItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
