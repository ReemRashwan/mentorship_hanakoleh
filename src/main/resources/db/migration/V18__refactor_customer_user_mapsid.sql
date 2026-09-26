-- Refactor customer table to use user_id as primary key with @MapsId pattern
-- This eliminates the surrogate customer_id and makes user_id both PK and FK

-- Step 1: Drop foreign key constraints that depend on customer.customer_id
ALTER TABLE cart DROP CONSTRAINT IF EXISTS fk__cart__customer_id;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS fk__orders__order_customer_id;
ALTER TABLE address DROP CONSTRAINT IF EXISTS fk__address__customer_id;

-- Step 2: Drop the unique constraint on user_id (will be re-added as PK)
ALTER TABLE customer DROP CONSTRAINT IF EXISTS uq__customer__user_id;

-- Step 3: Drop the foreign key constraint on customer.user_id
ALTER TABLE customer DROP CONSTRAINT IF EXISTS fk__customer__user_id;

-- Step 4: Drop the primary key constraint on customer_id
ALTER TABLE customer DROP CONSTRAINT IF EXISTS pk__customer;

-- Step 5: Drop the customer_id column
ALTER TABLE customer DROP COLUMN IF EXISTS customer_id;

-- Step 6: Add primary key constraint on user_id
ALTER TABLE customer ADD CONSTRAINT pk__customer PRIMARY KEY (user_id);

-- Step 7: Re-add foreign key constraint on customer.user_id with ON DELETE CASCADE
ALTER TABLE customer ADD CONSTRAINT fk__customer__user_id 
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;

-- Step 8: Recreate foreign key constraints that reference customer (now using user_id)
ALTER TABLE cart ADD CONSTRAINT fk__cart__customer_id 
    FOREIGN KEY (customer_id) REFERENCES customer (user_id) ON DELETE CASCADE;
ALTER TABLE orders ADD CONSTRAINT fk__orders__order_customer_id 
    FOREIGN KEY (order_customer_id) REFERENCES customer (user_id) ON DELETE CASCADE;
ALTER TABLE address ADD CONSTRAINT fk__address__customer_id 
    FOREIGN KEY (customer_id) REFERENCES customer (user_id) ON DELETE CASCADE;
