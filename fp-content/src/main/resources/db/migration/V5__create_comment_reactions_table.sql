CREATE TABLE
    IF NOT EXISTS comment_reactions (
        comment_id bigint NOT NULL,
        account_id varchar NOT NULL,
        created_at timestamp with time zone NOT NULL DEFAULT (now () AT TIME ZONE 'UTC'),
        PRIMARY KEY (comment_id, account_id),
        CONSTRAINT fk_comment_reactions FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
    );
