--drop column order_tracking_status from order_tracking table and replace it with:
--1-order_tracking_previous_status and 2-order_tracking_current_status
ALTER TABLE order_tracking DROP COLUMN order_tracking_status;
ALTER TABLE order_tracking ADD COLUMN order_current_status  VARCHAR(30) NOT NULL;
ALTER TABLE order_tracking ADD COLUMN order_previous_status VARCHAR(30) NOT NULL;

