CREATE TABLE optimized_schedules (
    id               UUID PRIMARY KEY,
    schedule_id       UUID NOT NULL,
    algorithm_version VARCHAR(50) NOT NULL,
    summary           VARCHAR(1000) NOT NULL,
    optimized_at      TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_optimized_schedules_schedule_id ON optimized_schedules(schedule_id);

