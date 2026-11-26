CREATE TABLE lesson_questions (
 id UUID PRIMARY KEY, lesson_id UUID NOT NULL REFERENCES lessons(id), student_id UUID NOT NULL REFERENCES users(id),
 content TEXT NOT NULL, status VARCHAR(20) NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_questions_lesson_created ON lesson_questions(lesson_id,created_at);
CREATE TABLE question_replies (
 id UUID PRIMARY KEY, question_id UUID NOT NULL REFERENCES lesson_questions(id), author_id UUID NOT NULL REFERENCES users(id),
 content TEXT NOT NULL, created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_replies_question_created ON question_replies(question_id,created_at);
