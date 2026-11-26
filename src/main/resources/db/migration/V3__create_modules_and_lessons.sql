CREATE TABLE course_modules (
 id UUID PRIMARY KEY, course_id UUID NOT NULL REFERENCES courses(id), title VARCHAR(180) NOT NULL,
 description TEXT, position INTEGER NOT NULL CHECK(position >= 0), created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_course_modules_course_position ON course_modules(course_id,position);
CREATE TABLE lessons (
 id UUID PRIMARY KEY, course_id UUID NOT NULL REFERENCES courses(id), module_id UUID REFERENCES course_modules(id),
 title VARCHAR(180) NOT NULL, description TEXT, youtube_video_id VARCHAR(20) NOT NULL,
 video_duration_seconds INTEGER CHECK(video_duration_seconds IS NULL OR video_duration_seconds > 0),
 position INTEGER NOT NULL CHECK(position >= 0), status VARCHAR(20) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_lessons_course_position ON lessons(course_id,position);
CREATE INDEX idx_lessons_module_position ON lessons(module_id,position);
