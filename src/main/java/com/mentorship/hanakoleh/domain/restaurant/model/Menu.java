package com.mentorship.hanakoleh.domain.restaurant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "menu_name", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String name;

    @Column(name = "menu_icon", length = 255)
    @Size(max = 255)
    private String icon;

    @Builder.Default
    @Column(name = "menu_ui_order", nullable = false)
    @NotNull
    @Min(0)
    private Integer uiOrder = 0;

    @Builder.Default
    @Column(name = "menu_is_visible", nullable = false)
    @NotNull
    private Boolean visible = true;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Menu)) {
            return false;
        }
        Menu menu = (Menu) other;
        return id != null && id.equals(menu.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
