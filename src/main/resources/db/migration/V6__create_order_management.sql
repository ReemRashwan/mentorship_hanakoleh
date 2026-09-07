-- ============================================================
-- 1. ORDERS
-- ============================================================
CREATE TABLE IF NOT EXISTS orders (
    order_id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_idempotency_key       UUID NOT NULL UNIQUE,

    order_customer_id           INTEGER NOT NULL,
    order_restaurant_id         INTEGER NOT NULL,
    order_rider_id              INTEGER NULL,
    order_promotion_id          INTEGER NULL,
    order_address_id            INTEGER NULL,

    order_delivery_option       VARCHAR(30) NOT NULL DEFAULT 'DELIVERY',
    order_final_status          VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    order_payment_status        VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_payment_method        VARCHAR(30) NOT NULL DEFAULT 'CREDIT_CARD',

    order_currency_code         CHAR(3) NOT NULL DEFAULT 'EGP',

    order_subtotal               NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_delivery_fees          NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_service_fees           NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_rider_tips             NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_discount_amount        NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_tax_amount             NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_total_amount           NUMERIC(12, 4) NOT NULL DEFAULT 0,
    order_refunded_amount        NUMERIC(12, 4) NOT NULL DEFAULT 0,

    order_delivery_instructions        TEXT NULL,
    order_delivery_address_snapshot    JSONB NOT NULL,
    order_estimated_delivery_at        TIMESTAMPTZ NULL,

    order_created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    order_updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    version                     INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk__orders__order_customer_id FOREIGN KEY (order_customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk__orders__order_restaurant_id FOREIGN KEY (order_restaurant_id) REFERENCES restaurant (restaurant_id) ON DELETE RESTRICT,
    CONSTRAINT chk__order_delivery_option CHECK (order_delivery_option IN ('DELIVERY', 'TAKEAWAY', 'IN_RESTAURANT')),
    CONSTRAINT chk__order_final_status CHECK (order_final_status IN (
                                              'CREATED', 'ACCEPTED', 'IN_PROGRESS', 'IN_DELIVERY', 'COMPLETED', 'CANCELLED', 'REFUNDED'
                                                                    )),
    CONSTRAINT chk__order_payment_status CHECK (order_payment_status IN (
                                                'PENDING', 'AUTHORIZED', 'PAID', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED'
                                                                        )),
    CONSTRAINT chk__order_amounts_positive CHECK (
                                                     order_subtotal >= 0 AND order_delivery_fees >= 0 AND order_service_fees >= 0 AND
                                                     order_rider_tips >= 0 AND order_discount_amount >= 0 AND order_tax_amount >= 0 AND
                                                     order_total_amount >= 0 AND order_refunded_amount >= 0
                                                 ),
    CONSTRAINT chk__order_total_math_correct CHECK (
                                                       order_total_amount = (order_subtotal + order_delivery_fees + order_service_fees +
                                                       order_rider_tips + order_tax_amount - order_discount_amount)
    ),
    CONSTRAINT chk__refund_not_exceed_total CHECK (order_refunded_amount <= order_total_amount),

    CONSTRAINT chk__rider_requires_progress CHECK (
                                                      order_rider_id IS NULL OR order_final_status NOT IN ('CREATED')
    ),

    CONSTRAINT chk__status_payment_coherence CHECK (
                                                       NOT (order_final_status = 'COMPLETED' AND order_payment_status IN ('PENDING', 'FAILED'))
    )
    );

-- ============================================================
-- 2. ORDER ITEMS
-- ============================================================
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id                         BIGINT NOT NULL,
    menu_item_id                     INTEGER NOT NULL,
    order_item_name_snapshot         VARCHAR(255) NOT NULL,
    order_item_price                 NUMERIC(12, 4) NOT NULL,
    order_item_quantity              INTEGER NOT NULL DEFAULT 1,
    order_item_subtotal              NUMERIC(12, 4) NOT NULL,
    order_item_options_snapshot      JSONB NOT NULL DEFAULT '[]'::jsonb,
    order_item_special_instructions  TEXT NULL,

    CONSTRAINT fk__order_items__order_id FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE RESTRICT,
    CONSTRAINT fk__order_items__menu_item_id FOREIGN KEY (menu_item_id) REFERENCES menu_item (menu_item_id) ON DELETE RESTRICT,
    CONSTRAINT chk__order_item_qty CHECK (order_item_quantity > 0),
    CONSTRAINT chk__order_item_prices CHECK (order_item_price >= 0 AND order_item_subtotal >= 0),
    CONSTRAINT chk__order_item_subtotal_math CHECK (order_item_subtotal = (order_item_price * order_item_quantity))
    );

CREATE INDEX idx__order_items__order_id ON order_items (order_id);

-- ============================================================
-- 3. ORDER TRACKING
-- ============================================================
CREATE TABLE IF NOT EXISTS order_tracking (
    order_tracking_id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id                           BIGINT NOT NULL,
    order_tracking_status              VARCHAR(30) NOT NULL,
    order_tracking_notes               TEXT NULL,
    order_tracking_created_by_user_id  INTEGER NULL,
    order_tracking_created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk__order_tracking__order_id FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE RESTRICT
    );

CREATE INDEX idx__order_tracking__order_id__created_at ON order_tracking (order_id, order_tracking_created_at);

-- ============================================================
-- 4. REFUND LEDGER
-- ============================================================
CREATE TABLE IF NOT EXISTS order_refunds (
    order_refund_id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id                    BIGINT NOT NULL,
    refund_amount                NUMERIC(12, 4) NOT NULL,
    refund_reason                VARCHAR(50) NOT NULL,
    refund_gateway_reference     VARCHAR(100) NOT NULL,
    refund_initiated_by_user_id  INTEGER NOT NULL,
    refund_created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk__order_refunds__order_id FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE RESTRICT,
    CONSTRAINT uq__order_refunds__order_id__refund_gateway_ref UNIQUE (order_id, refund_gateway_reference),
    CONSTRAINT chk__refund_amount_positive CHECK (refund_amount > 0)
    );

CREATE INDEX idx__order_refunds__order_id ON order_refunds (order_id);

-- ============================================================
-- 5. INDEXES
-- ============================================================
CREATE INDEX idx__orders__customer_id__pagination ON orders (order_customer_id, order_created_at DESC, order_id DESC);

CREATE INDEX idx__orders__restaurant_id__pending ON orders (order_restaurant_id, order_created_at)
    WHERE order_final_status = 'CREATED';

CREATE INDEX idx__orders__restaurant_id__active ON orders (order_restaurant_id, order_created_at DESC)
    WHERE order_final_status IN ('ACCEPTED', 'IN_PROGRESS');

CREATE INDEX idx__orders__rider_id__active ON orders (order_rider_id, order_final_status)
    WHERE order_rider_id IS NOT NULL
      AND order_final_status IN ('ACCEPTED', 'IN_PROGRESS', 'IN_DELIVERY');

CREATE INDEX idx__orders__unassigned_ready ON orders (order_created_at)
    WHERE order_rider_id IS NULL AND order_final_status = 'ACCEPTED';

CREATE INDEX idx__orders__customer_id__promotion_id ON orders (order_customer_id, order_promotion_id)
    WHERE order_promotion_id IS NOT NULL;