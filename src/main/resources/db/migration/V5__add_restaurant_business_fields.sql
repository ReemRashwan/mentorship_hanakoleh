ALTER TABLE restaurant
    ADD COLUMN restaurant_rating NUMERIC(3,2) NOT NULL DEFAULT 0.00,
    ADD COLUMN restaurant_longitude NUMERIC(9,6),
    ADD COLUMN restaurant_latitude NUMERIC(9,6),
    ADD COLUMN restaurant_avg_preparation_time_in_mins INTEGER NOT NULL DEFAULT 0;
