-- Rename timestamp columns to use standard names
ALTER TABLE address RENAME COLUMN address_created_at TO created_at;
ALTER TABLE address RENAME COLUMN address_updated_at TO updated_at;
