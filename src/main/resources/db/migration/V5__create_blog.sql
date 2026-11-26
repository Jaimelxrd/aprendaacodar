CREATE TABLE blog_posts (
 id UUID PRIMARY KEY, title VARCHAR(180) NOT NULL, slug VARCHAR(200) NOT NULL UNIQUE, excerpt VARCHAR(500) NOT NULL,
 content TEXT NOT NULL, cover_image_key VARCHAR(500), author_id UUID NOT NULL REFERENCES users(id), status VARCHAR(20) NOT NULL,
 published_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_blog_posts_status_published ON blog_posts(status,published_at DESC);
CREATE TABLE blog_comments (
 id UUID PRIMARY KEY, post_id UUID NOT NULL REFERENCES blog_posts(id), author_id UUID NOT NULL REFERENCES users(id),
 content TEXT NOT NULL, status VARCHAR(20) NOT NULL, moderated_by_id UUID REFERENCES users(id), moderated_at TIMESTAMPTZ,
 moderation_reason TEXT, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_blog_comments_post_status ON blog_comments(post_id,status,created_at);
