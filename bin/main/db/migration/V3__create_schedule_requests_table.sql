CREATE TABLE schedule_requests (
    id                  UUID PRIMARY KEY,
    earliest_start      TIMESTAMPTZ NOT NULL,
    latest_end          TIMESTAMPTZ NOT NULL,
    duration_minutes    INT NOT NULL,
    preferred_start     TIMESTAMPTZ,
    priority            VARCHAR(50),
    created_at          TIMESTAMPTZ NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'REQUESTED'
);

CREATE INDEX idx_schedule_requests_status ON schedule_requests(status);
