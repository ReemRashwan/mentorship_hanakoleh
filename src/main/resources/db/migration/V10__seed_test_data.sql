-- DEV / TEST SEED DATA

-- --- Lookup: add a rider user type (customer/admin already seeded in V1) ------
INSERT INTO user_type (user_type_name, user_type_description)
VALUES ('rider', 'Delivery rider')
ON CONFLICT (user_type_name) DO NOTHING;

-- --- Users (one customer, one rider) -----------------------------------------
-- NB: password hash is a bcrypt-shaped placeholder; not a real login credential.
INSERT INTO users (user_type_id, user_email, user_phone_number, user_first_name,
                   user_last_name, user_language_id, user_password_hash)
SELECT ut.user_type_id, 'sara.customer@hanakoleh.test', '+201000000001', 'Sara', 'Ahmed',
       l.language_id, '$2a$10$C0nsTanTdUmMyBcRyPtHashForSeedDataOnly000000000000'
FROM user_type ut, language l
WHERE ut.user_type_name = 'customer' AND l.language_code = 'en'
ON CONFLICT (user_email) DO NOTHING;

INSERT INTO users (user_type_id, user_email, user_phone_number, user_first_name,
                   user_last_name, user_language_id, user_password_hash)
SELECT ut.user_type_id, 'omar.rider@hanakoleh.test', '+201000000002', 'Omar', 'Hassan',
       l.language_id, '$2a$10$C0nsTanTdUmMyBcRyPtHashForSeedDataOnly000000000000'
FROM user_type ut, language l
WHERE ut.user_type_name = 'rider' AND l.language_code = 'en'
ON CONFLICT (user_email) DO NOTHING;

-- --- Customer -----------------------------------------------------------------
INSERT INTO customer (user_id, customer_notification_status)
SELECT u.user_id, TRUE FROM users u WHERE u.user_email = 'sara.customer@hanakoleh.test'
ON CONFLICT (user_id) DO NOTHING;

-- --- Restaurant ---------------------------------------------------------------
INSERT INTO restaurant (restaurant_name, restaurant_phone, restaurant_email,
                        restaurant_rating, restaurant_longitude, restaurant_latitude,
                        restaurant_avg_preparation_time_in_mins)
VALUES ('Koshary El Tahrir', '+20221000000', 'info@koshary.test',
        4.50, 31.235700, 30.044400, 20)
ON CONFLICT (restaurant_name) DO NOTHING;

-- --- Item categories ----------------------------------------------------------
INSERT INTO item_category (item_category_name)
VALUES ('Main Dishes'), ('Drinks')
ON CONFLICT (item_category_name) DO NOTHING;

-- --- Menu ---------------------------------------------------------------------
INSERT INTO menu (restaurant_id, menu_name, menu_ui_order, menu_is_visible)
SELECT r.restaurant_id, 'Main Menu', 0, TRUE
FROM restaurant r WHERE r.restaurant_name = 'Koshary El Tahrir';

-- --- Menu items (varied statuses so validation can be tested both ways) --------
INSERT INTO menu_item (menu_id, menu_item_category_id, menu_item_name, menu_item_price,
                       menu_item_available_quantity, menu_item_on_demand_status, menu_item_ui_order)
SELECT m.menu_id, c.item_category_id, 'Koshary Large', 55.00, NULL, 'AVAILABLE', 1
FROM menu m JOIN restaurant r ON m.restaurant_id = r.restaurant_id
JOIN item_category c ON c.item_category_name = 'Main Dishes'
WHERE r.restaurant_name = 'Koshary El Tahrir' AND m.menu_name = 'Main Menu';

INSERT INTO menu_item (menu_id, menu_item_category_id, menu_item_name, menu_item_price,
                       menu_item_available_quantity, menu_item_on_demand_status, menu_item_ui_order)
SELECT m.menu_id, c.item_category_id, 'Grilled Chicken', 120.00, 10, 'AVAILABLE', 2
FROM menu m JOIN restaurant r ON m.restaurant_id = r.restaurant_id
JOIN item_category c ON c.item_category_name = 'Main Dishes'
WHERE r.restaurant_name = 'Koshary El Tahrir' AND m.menu_name = 'Main Menu';

INSERT INTO menu_item (menu_id, menu_item_category_id, menu_item_name, menu_item_price,
                       menu_item_available_quantity, menu_item_on_demand_status, menu_item_ui_order)
SELECT m.menu_id, c.item_category_id, 'Soft Drink', 15.00, 50, 'AVAILABLE', 3
FROM menu m JOIN restaurant r ON m.restaurant_id = r.restaurant_id
JOIN item_category c ON c.item_category_name = 'Drinks'
WHERE r.restaurant_name = 'Koshary El Tahrir' AND m.menu_name = 'Main Menu';

-- Out-of-stock item, for testing "not orderable" validation
INSERT INTO menu_item (menu_id, menu_item_category_id, menu_item_name, menu_item_price,
                       menu_item_available_quantity, menu_item_on_demand_status, menu_item_ui_order)
SELECT m.menu_id, c.item_category_id, 'Seasonal Special', 90.00, 0, 'OUT_OF_STOCK', 4
FROM menu m JOIN restaurant r ON m.restaurant_id = r.restaurant_id
JOIN item_category c ON c.item_category_name = 'Main Dishes'
WHERE r.restaurant_name = 'Koshary El Tahrir' AND m.menu_name = 'Main Menu';

-- --- Active cart for Sara (2 orderable items) ---------------------------------
INSERT INTO cart (customer_id, restaurant_id, cart_status, cart_created_at, cart_updated_at)
SELECT cu.customer_id, r.restaurant_id, 'ACTIVE', NOW(), NOW()
FROM customer cu JOIN users u ON cu.user_id = u.user_id
JOIN restaurant r ON r.restaurant_name = 'Koshary El Tahrir'
WHERE u.user_email = 'sara.customer@hanakoleh.test'
ON CONFLICT (customer_id) DO NOTHING;

INSERT INTO cart_item (cart_id, menu_item_id, cart_item_price, cart_item_quantity, cart_item_note)
SELECT ct.cart_id, mi.menu_item_id, 55.00, 2, 'Extra sauce'
FROM cart ct JOIN customer cu ON ct.customer_id = cu.customer_id
JOIN users u ON cu.user_id = u.user_id
JOIN menu_item mi ON mi.menu_item_name = 'Koshary Large'
WHERE u.user_email = 'sara.customer@hanakoleh.test';

INSERT INTO cart_item (cart_id, menu_item_id, cart_item_price, cart_item_quantity, cart_item_note)
SELECT ct.cart_id, mi.menu_item_id, 15.00, 3, NULL
FROM cart ct JOIN customer cu ON ct.customer_id = cu.customer_id
JOIN users u ON cu.user_id = u.user_id
JOIN menu_item mi ON mi.menu_item_name = 'Soft Drink'
WHERE u.user_email = 'sara.customer@hanakoleh.test';

-- --- Address (default) --------------------------------------------------------
INSERT INTO address (address_customer_id, address_governorate, address_city, address_district,
                     address_street, address_building_number, address_floor, address_apartment,
                     address_landmark, address_postal_code, address_latitude, address_longitude,
                     address_label, address_is_default)
SELECT cu.customer_id, 'Cairo', 'Cairo', 'Downtown', 'Tahrir Street', '12', '3', '7',
       'Near the metro station', '11511', 30.045000, 31.236000, 'Home', TRUE
FROM customer cu JOIN users u ON cu.user_id = u.user_id
WHERE u.user_email = 'sara.customer@hanakoleh.test';

-- --- Rider (available) --------------------------------------------------------
INSERT INTO rider (user_id, rider_national_id, rider_vehicle_type, rider_status,
                   rider_current_latitude, rider_current_longitude, rider_active_governorate)
SELECT u.user_id, '29001011200099', 'MOTORCYCLE', 'AVAILABLE', 30.048000, 31.240000, 'Cairo'
FROM users u WHERE u.user_email = 'omar.rider@hanakoleh.test';

-- --- Promotions ---------------------------------------------------------------
INSERT INTO promotion (promotion_code, promotion_description, promotion_discount_type,
                       promotion_discount_value, promotion_max_discount_amount, promotion_min_order_amount,
                       promotion_currency_code, promotion_usage_limit_total, promotion_usage_limit_per_customer,
                       promotion_usage_count_total, promotion_starts_at, promotion_ends_at, promotion_is_active)
VALUES ('WELCOME10', '10% off your order (max 30 EGP)', 'PERCENTAGE',
        10.0000, 30.0000, 50.0000, 'EGP', 1000, 1, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 days', TRUE)
ON CONFLICT (promotion_code) DO NOTHING;

INSERT INTO promotion (promotion_code, promotion_description, promotion_discount_type,
                       promotion_discount_value, promotion_max_discount_amount, promotion_min_order_amount,
                       promotion_currency_code, promotion_usage_limit_total, promotion_usage_limit_per_customer,
                       promotion_usage_count_total, promotion_starts_at, promotion_ends_at, promotion_is_active)
VALUES ('FLAT20', '20 EGP off orders over 100 EGP', 'FIXED_AMOUNT',
        20.0000, NULL, 100.0000, 'EGP', NULL, 3, 0,
        NOW() - INTERVAL '1 day', NOW() + INTERVAL '60 days', TRUE)
ON CONFLICT (promotion_code) DO NOTHING;

-- --- One historical order (COMPLETED, partially refunded) ----------------------
-- Totals honour chk__order_total_math_correct:
--   150 (subtotal) + 15 + 5 + 10 + 0 (tax) - 15 (discount) = 165 (total)
INSERT INTO orders (order_idempotency_key, order_customer_id, order_restaurant_id, order_rider_id,
                    order_promotion_id, order_address_id, order_delivery_option, order_final_status,
                    order_payment_status, order_payment_method, order_currency_code,
                    order_subtotal, order_delivery_fees, order_service_fees, order_rider_tips,
                    order_discount_amount, order_tax_amount, order_total_amount, order_refunded_amount,
                    order_delivery_instructions, order_delivery_address_snapshot, order_estimated_delivery_at)
SELECT '11111111-1111-1111-1111-111111111111',
       cu.customer_id, r.restaurant_id, rd.rider_id, p.promotion_id, a.address_id,
       'DELIVERY', 'COMPLETED', 'PARTIALLY_REFUNDED', 'CREDIT_CARD', 'EGP',
       150.0000, 15.0000, 5.0000, 10.0000, 15.0000, 0.0000, 165.0000, 20.0000,
       'Leave at the door',
       jsonb_build_object('governorate', 'Cairo', 'city', 'Cairo', 'street', 'Tahrir Street',
                          'building', '12', 'floor', '3', 'apartment', '7', 'label', 'Home'),
       NOW() - INTERVAL '2 days' + INTERVAL '40 minutes'
FROM customer cu JOIN users u ON cu.user_id = u.user_id
JOIN restaurant r ON r.restaurant_name = 'Koshary El Tahrir'
JOIN rider rd ON rd.rider_national_id = '29001011200099'
JOIN promotion p ON p.promotion_code = 'WELCOME10'
JOIN address a ON a.address_customer_id = cu.customer_id AND a.address_is_default = TRUE
WHERE u.user_email = 'sara.customer@hanakoleh.test'
ON CONFLICT (order_idempotency_key) DO NOTHING;

-- Order items (subtotal = price * quantity per chk__order_item_subtotal_math)
INSERT INTO order_items (order_id, menu_item_id, order_item_name_snapshot, order_item_price,
                         order_item_quantity, order_item_subtotal, order_item_special_instructions)
SELECT o.order_id, mi.menu_item_id, 'Grilled Chicken', 120.0000, 1, 120.0000, 'No salt'
FROM orders o
JOIN menu_item mi ON mi.menu_item_name = 'Grilled Chicken'
WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111';

INSERT INTO order_items (order_id, menu_item_id, order_item_name_snapshot, order_item_price,
                         order_item_quantity, order_item_subtotal, order_item_special_instructions)
SELECT o.order_id, mi.menu_item_id, 'Soft Drink', 15.0000, 2, 30.0000, NULL
FROM orders o
JOIN menu_item mi ON mi.menu_item_name = 'Soft Drink'
WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111';

-- Order tracking timeline
INSERT INTO order_tracking (order_id, order_tracking_status, order_tracking_notes,
                            order_tracking_created_by_user_id, order_tracking_created_at)
SELECT o.order_id, 'CREATED', 'Order placed', u.user_id, NOW() - INTERVAL '2 days'
FROM orders o, users u
WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111'
  AND u.user_email = 'sara.customer@hanakoleh.test';

INSERT INTO order_tracking (order_id, order_tracking_status, order_tracking_notes,
                            order_tracking_created_by_user_id, order_tracking_created_at)
SELECT o.order_id, 'ACCEPTED', 'Restaurant accepted the order', NULL, NOW() - INTERVAL '2 days' + INTERVAL '3 minutes'
FROM orders o WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111';

INSERT INTO order_tracking (order_id, order_tracking_status, order_tracking_notes,
                            order_tracking_created_by_user_id, order_tracking_created_at)
SELECT o.order_id, 'IN_DELIVERY', 'Rider on the way', NULL, NOW() - INTERVAL '2 days' + INTERVAL '25 minutes'
FROM orders o WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111';

INSERT INTO order_tracking (order_id, order_tracking_status, order_tracking_notes,
                            order_tracking_created_by_user_id, order_tracking_created_at)
SELECT o.order_id, 'COMPLETED', 'Delivered', NULL, NOW() - INTERVAL '2 days' + INTERVAL '45 minutes'
FROM orders o WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111';

-- Refund ledger (amount <= total; positive)
INSERT INTO order_refunds (order_id, refund_amount, refund_reason, refund_gateway_reference,
                           refund_initiated_by_user_id, refund_created_at)
SELECT o.order_id, 20.0000, 'ITEM_UNAVAILABLE', 'rfnd_seed_0001', u.user_id, NOW() - INTERVAL '1 day'
FROM orders o, users u
WHERE o.order_idempotency_key = '11111111-1111-1111-1111-111111111111'
  AND u.user_email = 'sara.customer@hanakoleh.test';
