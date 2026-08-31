package com.mentorship.hanakoleh.domain.cart.model;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "cart_item_price", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;

    @Builder.Default
    @Column(name = "cart_item_quantity", nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity = 1;

    @Column(name = "cart_item_note")
    private String note;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CartItem cartItem)) {
            return false;
        }
        return id != null && id.equals(cartItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
