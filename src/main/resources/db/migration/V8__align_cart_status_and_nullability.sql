ALTER TABLE cart
    ADD COLUMN cart_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN cart_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

ALTER TABLE cart
    ADD CONSTRAINT chk__cart__cart_status
        CHECK (cart_status IN ('ACTIVE', 'EMPTY', 'COMPLETED', 'EXPIRED'));

ALTER TABLE cart
    ALTER COLUMN restaurant_id DROP NOT NULL;
