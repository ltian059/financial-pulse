CREATE TABLE
    IF NOT EXISTS "comments" (
        "id" BIGSERIAL PRIMARY KEY,
        "content" text NOT NULL,
        "post_id" bigint NOT NULL,
        "account_id" varchar NOT NULL,
        "created_at" timestamp with time zone NOT NULL DEFAULT (now () AT TIME ZONE 'UTC'),
        "modified_at" timestamp with time zone,
        "status" varchar NOT NULL DEFAULT 'ACTIVE',
        "like_count" bigint NOT NULL DEFAULT (0),
        "reply_count" bigint NOT NULL DEFAULT (0),
        "parent_comment_id" bigint,
        CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
        CONSTRAINT fk_comments_parent FOREIGN KEY (parent_comment_id) REFERENCES comments (id) ON DELETE SET NULL,
        CONSTRAINT chk_comments_status CHECK ("status" IN ('ACTIVE', 'DELETED'))
    );

COMMENT ON TABLE "comments" IS 'Comments on posts (one-level threading via parent_comment_id)';

COMMENT ON COLUMN "comments"."post_id" IS 'FK to posts.id';

COMMENT ON COLUMN "comments"."account_id" IS 'Author account id';

COMMENT ON COLUMN "comments"."status" IS 'active/deleted/hidden';

COMMENT ON COLUMN "comments"."like_count" IS 'Cached like count';

COMMENT ON COLUMN "comments"."reply_count" IS 'Cached direct reply count';

COMMENT ON COLUMN "comments"."parent_comment_id" IS 'Nullable parent for replies';
