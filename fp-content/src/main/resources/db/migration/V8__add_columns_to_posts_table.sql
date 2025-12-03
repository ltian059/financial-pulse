ALTER TABLE "posts"
    ADD COLUMN "quote_count"       bigint NOT NULL DEFAULT (0),
    ADD COLUMN "repost_of_post_id" bigint,
    ADD COLUMN "quote_of_post_id"  bigint,
    ADD CONSTRAINT fk_posts_repost_of_post FOREIGN KEY ("repost_of_post_id") REFERENCES "posts" ("id"),
    ADD CONSTRAINT fk_posts_quote_of_post FOREIGN KEY ("quote_of_post_id") REFERENCES "posts" ("id");



COMMENT ON COLUMN "posts"."quote_count" IS 'Cached quote count';

COMMENT ON COLUMN "posts"."repost_of_post_id" IS 'If this post is a repost, the original post id';

COMMENT ON COLUMN "posts"."quote_of_post_id" IS 'If this post is a quote, the quoted post id';

