INSERT INTO user_type (user_type_name, user_type_description)
VALUES
    ('restaurant_representative', 'Authorized staff member who manages a restaurant''s profile and menu, and accepts, prepares and cancels its orders'),

CREATE UNIQUE INDEX uq_restaurant_representative_user_id_restaurant_id ON restaurant_representative (user_id, restaurant_id);