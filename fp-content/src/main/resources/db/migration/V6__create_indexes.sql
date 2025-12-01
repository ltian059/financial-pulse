CREATE INDEX IF NOT EXISTS idx_posts_account_created ON posts (account_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_posts_created ON posts (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_comments_post_created ON comments (post_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_comments_parent_created ON comments (parent_comment_id, created_at DESC);
