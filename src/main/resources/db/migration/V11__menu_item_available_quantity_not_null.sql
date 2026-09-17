
UPDATE menu_item
SET menu_item_available_quantity = 100
WHERE menu_item_available_quantity IS NULL;

ALTER TABLE menu_item
    ALTER COLUMN menu_item_available_quantity SET DEFAULT 0;

ALTER TABLE menu_item
    ALTER COLUMN menu_item_available_quantity SET NOT NULL;
