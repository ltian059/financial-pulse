-- 1. Add a new id column as primary key
ALTER TABLE post_reactions
    ADD COLUMN id BIGSERIAL;

-- 2. Add unique constraint on (post_id, account_id, type)
ALTER TABLE post_reactions
    ADD CONSTRAINT uq_post_reactions_post_account_type UNIQUE (post_id, account_id, type);

-- 3. Remove the original primary key constraint if it exists
ALTER TABLE post_reactions
    DROP CONSTRAINT IF EXISTS post_reactions_pkey;

-- 4. Set the new id column as the primary key
ALTER TABLE post_reactions
    ADD CONSTRAINT post_reactions_pkey PRIMARY KEY (id);