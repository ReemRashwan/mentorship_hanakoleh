CREATE TABLE IF NOT EXISTS review (
    review_id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk__review__restaurant_id FOREIGN KEY (restaurant_id) REFERENCES restaurant (restaurant_id) ON DELETE CASCADE,
    CONSTRAINT fk__review__customer_id FOREIGN KEY (customer_id) REFERENCES customer (user_id) ON DELETE CASCADE
);

