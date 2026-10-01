-- 1. Remove unnecessary columns
ALTER TABLE address
    DROP COLUMN IF EXISTS address_governorate,
    DROP COLUMN IF EXISTS address_city,
    DROP COLUMN IF EXISTS address_postal_code,
    DROP COLUMN IF EXISTS address_latitude,
    DROP COLUMN IF EXISTS address_longitude,
    DROP COLUMN IF EXISTS address_label;

-- 2. Simplify column names
ALTER TABLE address RENAME COLUMN address_customer_id TO customer_id;
ALTER TABLE address RENAME COLUMN address_street TO street;
ALTER TABLE address RENAME COLUMN address_building_number TO building_number;
ALTER TABLE address RENAME COLUMN address_floor TO floor;
ALTER TABLE address RENAME COLUMN address_apartment TO apartment;
ALTER TABLE address RENAME COLUMN address_landmark TO landmark;
ALTER TABLE address RENAME COLUMN address_district TO district;
ALTER TABLE address RENAME COLUMN address_is_default TO is_default;

-- 3. Update Indexes & Constraints
DROP INDEX IF EXISTS idx__address__customer_id;
CREATE INDEX idx__address__customer_id ON address (customer_id);

DROP INDEX IF EXISTS ux__address__one_default_per_customer;
CREATE UNIQUE INDEX ux__address__one_default_per_customer ON address (customer_id) WHERE is_default = true;