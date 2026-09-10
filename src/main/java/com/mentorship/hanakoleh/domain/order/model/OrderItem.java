package com.mentorship.hanakoleh.domain.order.model;

import com.mentorship.hanakoleh.domain.restaurant.model.MenuItem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "order_item_name_snapshot", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String nameSnapshot;

    @Column(name = "order_item_price", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal price;

    @Builder.Default
    @Column(name = "order_item_quantity", nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity = 1;

    @Column(name = "order_item_subtotal", nullable = false, precision = 12, scale = 4)
    @NotNull
    @DecimalMin(value = "0.0000")
    private BigDecimal subtotal;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "order_item_options_snapshot", nullable = false)
    @NotNull
    @Builder.Default
    private List<Map<String, Object>> optionsSnapshot = List.of();

    @Column(name = "order_item_special_instructions")
    @Size(max = 1000)
    private String specialInstructions;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OrderItem)) {
            return false;
        }
        OrderItem orderItem = (OrderItem) other;
        return id != null && id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
