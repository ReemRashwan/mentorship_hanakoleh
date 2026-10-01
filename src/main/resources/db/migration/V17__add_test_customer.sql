-- Add a test customer user for login testing
-- Email: user@example.com
-- Password: password

INSERT INTO users (user_type_id, user_email, user_phone_number, user_first_name,
                   user_last_name, user_language_id, user_password_hash)
SELECT ut.user_type_id, 'user@example.com', '+201000000003', 'Test', 'User',
       l.language_id, '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
FROM user_type ut, language l
WHERE ut.user_type_name = 'customer' AND l.language_code = 'en'
ON CONFLICT (user_email) DO NOTHING;

-- Create customer record for the new user
INSERT INTO customer (user_id, customer_notification_status)
SELECT u.user_id, TRUE FROM users u WHERE u.user_email = 'user@example.com'
ON CONFLICT (user_id) DO NOTHING;
