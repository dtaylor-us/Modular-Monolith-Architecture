CREATE TABLE notifications (
    id          UUID PRIMARY KEY,
    request_id  UUID NOT NULL,
    message     VARCHAR(1000) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_notifications_request_id ON notifications(request_id);
