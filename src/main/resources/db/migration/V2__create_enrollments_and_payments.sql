CREATE TABLE course_enrollments (
 id UUID PRIMARY KEY, student_id UUID NOT NULL REFERENCES users(id), course_id UUID NOT NULL REFERENCES courses(id),
 status VARCHAR(30) NOT NULL, source VARCHAR(20) NOT NULL, agreed_price NUMERIC(12,2) NOT NULL CHECK(agreed_price >= 0),
 currency VARCHAR(3) NOT NULL, enrolled_at TIMESTAMPTZ NOT NULL, activated_at TIMESTAMPTZ,
 revoked_at TIMESTAMPTZ, revoked_by_id UUID REFERENCES users(id), revocation_reason TEXT, granted_by_id UUID REFERENCES users(id),
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL, UNIQUE(student_id,course_id)
);
CREATE INDEX idx_enrollments_student ON course_enrollments(student_id);
CREATE INDEX idx_enrollments_course ON course_enrollments(course_id);
CREATE TABLE payments (
 id UUID PRIMARY KEY, enrollment_id UUID NOT NULL REFERENCES course_enrollments(id), method VARCHAR(20) NOT NULL,
 amount NUMERIC(12,2) NOT NULL CHECK(amount >= 0), currency VARCHAR(3) NOT NULL,
 transaction_reference VARCHAR(150) NOT NULL UNIQUE, proof_file_key VARCHAR(500), status VARCHAR(30) NOT NULL,
 submitted_at TIMESTAMPTZ NOT NULL, reviewed_at TIMESTAMPTZ, reviewed_by_id UUID REFERENCES users(id), rejection_reason TEXT,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_payments_enrollment ON payments(enrollment_id);
CREATE INDEX idx_payments_status ON payments(status);
