-- ============================================================
-- SUPPORTING ENUM-ISH LOOKUPS (Egypt governorates — kept as
-- a CHECK list rather than a separate table since it's static
-- reference data; promote to a table later if you need i18n
-- names or add other countries)
-- ============================================================

-- ============================================================
-- A. ADDRESS (Egypt-shaped)
-- ============================================================
CREATE TABLE IF NOT EXISTS address (
    address_id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    address_customer_id     INTEGER NOT NULL,

    address_governorate     VARCHAR(50) NOT NULL,   -- e.g. 'Cairo', 'Giza', 'Alexandria'
    address_city            VARCHAR(100) NOT NULL,  -- e.g. 'Nasr City', 'Maadi', 'Smouha'
    address_district        VARCHAR(100) NULL,      -- neighborhood/hay, common in EG addressing
    address_street          VARCHAR(255) NOT NULL,
    address_building_number VARCHAR(20) NULL,
    address_floor           VARCHAR(20) NULL,
    address_apartment       VARCHAR(20) NULL,
    address_landmark        VARCHAR(255) NULL,      -- "nearest landmark" is standard practice in EG delivery
    address_postal_code     VARCHAR(10) NULL,       -- optional; not universally used in Egypt

    address_latitude        NUMERIC(9, 6) NULL,
    address_longitude       NUMERIC(9, 6) NULL,

    address_label           VARCHAR(30) NULL,       -- 'Home', 'Work', etc.
    address_is_default      BOOLEAN NOT NULL DEFAULT false,

    address_created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    address_updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk__address__customer_id FOREIGN KEY (address_customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT,
    CONSTRAINT chk__address_lat_range CHECK (address_latitude IS NULL OR (address_latitude BETWEEN -90 AND 90)),
    CONSTRAINT chk__address_lng_range CHECK (address_longitude IS NULL OR (address_longitude BETWEEN -180 AND 180))
);

CREATE INDEX idx__address__customer_id ON address (address_customer_id);
-- Only one default address per customer
CREATE UNIQUE INDEX uq__address__one_default_per_customer
    ON address (address_customer_id)
    WHERE address_is_default = true;

-- ============================================================
-- B. RIDER
-- ============================================================
CREATE TABLE IF NOT EXISTS rider (
    rider_id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    rider_full_name          VARCHAR(150) NOT NULL,
    rider_phone_number       VARCHAR(20) NOT NULL UNIQUE,
    rider_national_id        VARCHAR(20) NULL UNIQUE,   -- Egyptian national ID, if you collect it
    rider_vehicle_type       VARCHAR(20) NOT NULL DEFAULT 'MOTORCYCLE',
    rider_status             VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',

    rider_current_latitude   NUMERIC(9, 6) NULL,
    rider_current_longitude  NUMERIC(9, 6) NULL,
    rider_location_updated_at TIMESTAMPTZ NULL,

    rider_active_governorate VARCHAR(50) NULL,   -- which governorate they operate in

    rider_created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    rider_updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk__rider_vehicle_type CHECK (rider_vehicle_type IN ('MOTORCYCLE', 'BICYCLE', 'CAR', 'TUKTUK')),
    CONSTRAINT chk__rider_status CHECK (rider_status IN ('OFFLINE', 'AVAILABLE', 'ON_DELIVERY', 'SUSPENDED')),
    CONSTRAINT chk__rider_lat_range CHECK (rider_current_latitude IS NULL OR (rider_current_latitude BETWEEN -90 AND 90)),
    CONSTRAINT chk__rider_lng_range CHECK (rider_current_longitude IS NULL OR (rider_current_longitude BETWEEN -180 AND 180))
);

-- fast lookup for "available riders near me" matching, scoped by governorate first
CREATE INDEX idx__rider__matching
    ON rider (rider_active_governorate, rider_status)
    WHERE rider_status = 'AVAILABLE';

-- ============================================================
-- C. PROMOTION
-- ============================================================
CREATE TABLE IF NOT EXISTS promotion (
    promotion_id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    promotion_code            VARCHAR(30) NOT NULL UNIQUE,
    promotion_description     VARCHAR(255) NULL,

    promotion_discount_type   VARCHAR(20) NOT NULL,   -- PERCENTAGE or FIXED_AMOUNT
    promotion_discount_value  NUMERIC(12, 4) NOT NULL,
    promotion_max_discount_amount NUMERIC(12, 4) NULL, -- cap for percentage promos
    promotion_min_order_amount    NUMERIC(12, 4) NOT NULL DEFAULT 0,

    promotion_currency_code   CHAR(3) NOT NULL DEFAULT 'EGP',

    promotion_usage_limit_total    INTEGER NULL,       -- NULL = unlimited
    promotion_usage_limit_per_customer INTEGER NOT NULL DEFAULT 1,
    promotion_usage_count_total    INTEGER NOT NULL DEFAULT 0,

    promotion_starts_at        TIMESTAMPTZ NOT NULL,
    promotion_ends_at          TIMESTAMPTZ NOT NULL,
    promotion_is_active        BOOLEAN NOT NULL DEFAULT true,

    promotion_created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    promotion_updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),

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

-- ============================================================
-- E. DEFAULT CURRENCY → EGP
-- ============================================================
ALTER TABLE orders ALTER COLUMN order_currency_code SET DEFAULT 'EGP';
