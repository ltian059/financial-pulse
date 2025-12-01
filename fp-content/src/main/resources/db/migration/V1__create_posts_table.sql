CREATE TABLE
    IF NOT EXISTS "posts" (
        "id" BIGSERIAL PRIMARY KEY,
        "content" text NOT NULL,
        "account_id" varchar NOT NULL,
        "created_at" timestamp with time zone NOT NULL DEFAULT (now () AT TIME ZONE 'UTC'),
        "modified_at" timestamp with time zone,
        "image_links" text,
        "labels" text,
        "like_count" bigint NOT NULL DEFAULT (0),
        "view_count" bigint NOT NULL DEFAULT (0),
        "comment_count" bigint NOT NULL DEFAULT (0),
        "repost_count" bigint NOT NULL DEFAULT (0),
        "status" varchar NOT NULL DEFAULT 'ACTIVE',
        CONSTRAINT chk_posts_status CHECK ("status" IN ('ACTIVE', 'DELETED', 'HIDDEN'))
    );

COMMENT ON TABLE "posts" IS 'Financial Pulse user posts';

COMMENT ON COLUMN "posts"."content" IS 'Rich text HTML content';

COMMENT ON COLUMN "posts"."account_id" IS 'Author account id';

COMMENT ON COLUMN "posts"."image_links" IS 'Comma-separated or JSON-serialized image URLs/keys';

COMMENT ON COLUMN "posts"."labels" IS 'Tags/labels (consider jsonb for structure)';

COMMENT ON COLUMN "posts"."like_count" IS 'Cached like count';

COMMENT ON COLUMN "posts"."view_count" IS 'Cached view count';

COMMENT ON COLUMN "posts"."comment_count" IS 'Cached comment count';

COMMENT ON COLUMN "posts"."repost_count" IS 'Cached repost count';

COMMENT ON COLUMN "posts"."status" IS 'active/deleted/hidden (soft delete)';
