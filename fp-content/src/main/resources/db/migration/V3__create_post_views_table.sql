CREATE TABLE
    IF NOT EXISTS post_views (
        id BIGSERIAL PRIMARY KEY,
        post_id bigint NOT NULL,
        account_id varchar NULL,
        client_hash varchar NULL,
        created_at timestamp with time zone NOT NULL DEFAULT (now () AT TIME ZONE 'UTC'),
        CONSTRAINT uq_post_views_account UNIQUE (post_id, account_id),
        CONSTRAINT uq_post_views_client UNIQUE (post_id, client_hash),
        CONSTRAINT fk_post_views_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
    );
