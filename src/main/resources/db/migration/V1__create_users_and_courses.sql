CREATE TABLE users (
 id UUID PRIMARY KEY, first_name VARCHAR(80) NOT NULL, last_name VARCHAR(80) NOT NULL,
 email VARCHAR(254) NOT NULL UNIQUE, phone_number VARCHAR(30), password_hash VARCHAR(255) NOT NULL,
 profile_photo_key VARCHAR(500), role VARCHAR(20) NOT NULL, status VARCHAR(20) NOT NULL,
 email_verified BOOLEAN NOT NULL DEFAULT FALSE, last_login_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE refresh_tokens (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id), token_hash VARCHAR(64) NOT NULL UNIQUE,
 expires_at TIMESTAMPTZ NOT NULL, revoked_at TIMESTAMPTZ,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE TABLE teacher_profiles (
 id UUID PRIMARY KEY, user_id UUID NOT NULL UNIQUE REFERENCES users(id), biography TEXT,
 specialization VARCHAR(255), professional_title VARCHAR(255), created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE TABLE courses (
 id UUID PRIMARY KEY, title VARCHAR(180) NOT NULL, slug VARCHAR(200) NOT NULL UNIQUE,
 short_description VARCHAR(500) NOT NULL, description TEXT NOT NULL, cover_image_key VARCHAR(500),
 price NUMERIC(12,2) NOT NULL CHECK(price >= 0), currency VARCHAR(3) NOT NULL DEFAULT 'MZN',
 duration_hours INTEGER CHECK(duration_hours IS NULL OR duration_hours > 0), prerequisites TEXT, learning_outcomes TEXT,
 curriculum_mode VARCHAR(30) NOT NULL, status VARCHAR(30) NOT NULL, teacher_id UUID NOT NULL REFERENCES users(id),
 submitted_at TIMESTAMPTZ, reviewed_at TIMESTAMPTZ, reviewed_by_id UUID REFERENCES users(id), rejection_reason TEXT,
 published_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_teacher ON courses(teacher_id);
