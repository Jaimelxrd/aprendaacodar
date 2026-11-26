CREATE TABLE audit_logs (
 id UUID PRIMARY KEY, actor_id UUID REFERENCES users(id), action VARCHAR(100) NOT NULL,
 entity_type VARCHAR(100) NOT NULL, entity_id UUID NOT NULL, details JSONB NOT NULL DEFAULT '{}'::jsonb,
 created_at TIMESTAMPTZ NOT NULL, updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at DESC);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type,entity_id);
