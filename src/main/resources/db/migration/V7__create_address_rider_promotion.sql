-- ============================================================
-- A. ADDRESS (Egypt-shaped)
-- ============================================================
CREATE TABLE IF NOT EXISTS address (
    address_id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address_customer_id      INTEGER NOT NULL,

    address_governorate      VARCHAR(50) NOT NULL,
    address_city             VARCHAR(100) NOT NULL,
    address_district         VARCHAR(100) NULL,
    address_street           VARCHAR(255) NOT NULL,
    address_building_number  VARCHAR(20) NULL,
    address_floor            VARCHAR(20) NULL,
    address_apartment        VARCHAR(20) NULL,
    address_landmark         VARCHAR(255) NULL,
    address_postal_code      VARCHAR(10) NULL,

    address_latitude         NUMERIC(9, 6) NULL,
    address_longitude        NUMERIC(9, 6) NULL,

    address_label            VARCHAR(30) NULL,
    address_is_default       BOOLEAN NOT NULL DEFAULT false,

    address_created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    address_updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk__address__customer_id FOREIGN KEY (address_customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT,
    CONSTRAINT chk__address_lat_range CHECK (address_latitude IS NULL OR (address_latitude BETWEEN -90 AND 90)),
    CONSTRAINT chk__address_lng_range CHECK (address_longitude IS NULL OR (address_longitude BETWEEN -180 AND 180))
    );

CREATE INDEX idx__address__customer_id ON address (address_customer_id);
CREATE UNIQUE INDEX ux__address__one_default_per_customer
    ON address (address_customer_id)
    WHERE address_is_default = true;

-- ============================================================
-- B. RIDER
-- ============================================================
CREATE TABLE IF NOT EXISTS rider (
    rider_id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id                    INTEGER NOT NULL UNIQUE,
    rider_national_id          VARCHAR(20) NULL UNIQUE,
    rider_vehicle_type         VARCHAR(20) NOT NULL DEFAULT 'MOTORCYCLE',
    rider_status                VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',

    rider_current_latitude      NUMERIC(9, 6) NULL,
    rider_current_longitude     NUMERIC(9, 6) NULL,
    rider_location_updated_at   TIMESTAMPTZ NULL,

    rider_active_governorate    VARCHAR(50) NULL,

    rider_created_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    rider_updated_at            TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk__rider__user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT,
    CONSTRAINT chk__rider_vehicle_type CHECK (rider_vehicle_type IN ('MOTORCYCLE', 'BICYCLE', 'CAR', 'TUKTUK')),
    CONSTRAINT chk__rider_status CHECK (rider_status IN ('OFFLINE', 'AVAILABLE', 'ON_DELIVERY', 'SUSPENDED')),
    CONSTRAINT chk__rider_lat_range CHECK (rider_current_latitude IS NULL OR (rider_current_latitude BETWEEN -90 AND 90)),
    CONSTRAINT chk__rider_lng_range CHECK (rider_current_longitude IS NULL OR (rider_current_longitude BETWEEN -180 AND 180))
    );

CREATE INDEX idx__rider__matching
    ON rider (rider_active_governorate, rider_status)
    WHERE rider_status = 'AVAILABLE';

-- ============================================================
-- C. PROMOTION
-- ============================================================
CREATE TABLE IF NOT EXISTS promotion (
    promotion_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    promotion_code              VARCHAR(30) NOT NULL UNIQUE,
    promotion_description       VARCHAR(255) NULL,

    promotion_discount_type     VARCHAR(20) NOT NULL,
    promotion_discount_value    NUMERIC(12, 4) NOT NULL,
    promotion_max_discount_amount NUMERIC(12, 4) NULL,
    promotion_min_order_amount    NUMERIC(12, 4) NOT NULL DEFAULT 0,

    promotion_currency_code     CHAR(3) NOT NULL DEFAULT 'EGP',

    promotion_usage_limit_total         INTEGER NULL,
    promotion_usage_limit_per_customer  INTEGER NOT NULL DEFAULT 1,
    promotion_usage_count_total         INTEGER NOT NULL DEFAULT 0,

    promotion_starts_at         TIMESTAMPTZ NOT NULL,
    promotion_ends_at           TIMESTAMPTZ NOT NULL,
    promotion_is_active         BOOLEAN NOT NULL DEFAULT true,

    promotion_created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    promotion_updated_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk__promotion_discount_type CHECK (promotion_discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    CONSTRAINT chk__promotion_discount_value_positive CHECK (promotion_discount_value > 0),
    CONSTRAINT chk__promotion_percentage_range CHECK (
                                                         promotion_discount_type <> 'PERCENTAGE' OR promotion_discount_value <= 100
                                                     ),
    CONSTRAINT chk__promotion_dates CHECK (promotion_ends_at > promotion_starts_at),
    CONSTRAINT chk__promotion_usage_positive CHECK (
                                                       promotion_usage_count_total >= 0 AND promotion_usage_limit_per_customer > 0
                                                   )
    );

CREATE INDEX idx__promotion__code_active ON promotion (promotion_code) WHERE promotion_is_active = true;

-- ============================================================
-- D. WIRE FKs BACK INTO ORDERS
-- ============================================================
ALTER TABLE orders
    ADD CONSTRAINT fk__orders__order_rider_id FOREIGN KEY (order_rider_id) REFERENCES rider (rider_id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk__orders__order_promotion_id FOREIGN KEY (order_promotion_id) REFERENCES promotion (promotion_id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk__orders__order_address_id FOREIGN KEY (order_address_id) REFERENCES address (address_id) ON DELETE RESTRICT;