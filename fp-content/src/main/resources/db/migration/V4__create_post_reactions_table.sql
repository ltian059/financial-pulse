CREATE TABLE
    IF NOT EXISTS post_reactions (
        post_id bigint NOT NULL,
        account_id varchar NOT NULL,
        type varchar NOT NULL,
        created_at timestamp with time zone NOT NULL DEFAULT (now () AT TIME ZONE 'UTC'),
        PRIMARY KEY (post_id, account_id, type),
        CONSTRAINT fk_post_reactions_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
        CONSTRAINT chk_post_reactions_type CHECK (type IN ('LIKE', 'REPOST', 'QUOTE'))
    );
