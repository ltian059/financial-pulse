-- 1. Add a new id column as primary key
ALTER TABLE comment_reactions
    ADD COLUMN id BIGSERIAL;

-- 2. Add unique constraint on (account_id, comment_id)
ALTER TABLE comment_reactions
    ADD CONSTRAINT uq_comment_reactions_account_comment UNIQUE (account_id, comment_id);

-- 3. Remove the original primary key constraint if it exists
ALTER TABLE comment_reactions
    DROP CONSTRAINT IF EXISTS comment_reactions_pkey;

-- 4. Set the new id column as the primary key
ALTER TABLE comment_reactions
    ADD CONSTRAINT comment_reactions_pkey PRIMARY KEY (id);