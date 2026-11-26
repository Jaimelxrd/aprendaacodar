CREATE TABLE lesson_resources (
 id UUID PRIMARY KEY, lesson_id UUID NOT NULL REFERENCES lessons(id), name VARCHAR(180) NOT NULL,
 description TEXT, file_key VARCHAR(500) NOT NULL, original_file_name VARCHAR(255) NOT NULL,
 mime_type VARCHAR(150) NOT NULL, file_size_bytes BIGINT NOT NULL CHECK(file_size_bytes > 0),
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_lesson_resources_lesson ON lesson_resources(lesson_id);
