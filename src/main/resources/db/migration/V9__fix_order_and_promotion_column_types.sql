
ALTER TABLE orders
    ALTER COLUMN order_rider_id TYPE BIGINT,
    ALTER COLUMN order_promotion_id TYPE BIGINT,
    ALTER COLUMN order_address_id TYPE BIGINT,
    ALTER COLUMN version TYPE BIGINT,
    ALTER COLUMN order_currency_code TYPE VARCHAR(3);

ALTER TABLE promotion
    ALTER COLUMN promotion_currency_code TYPE VARCHAR(3);
