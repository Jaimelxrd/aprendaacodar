CREATE TABLE lesson_completions (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL REFERENCES course_enrollments(id),
    lesson_id UUID NOT NULL REFERENCES lessons(id),
    completed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_lesson_completions_enrollment_lesson UNIQUE (enrollment_id, lesson_id)
);

CREATE INDEX idx_lesson_completions_enrollment ON lesson_completions(enrollment_id);
