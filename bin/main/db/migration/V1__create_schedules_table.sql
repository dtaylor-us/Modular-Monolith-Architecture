CREATE TABLE schedules (
    id          UUID PRIMARY KEY,
    start_time  TIMESTAMPTZ NOT NULL,
    end_time    TIMESTAMPTZ NOT NULL,
    title       VARCHAR(500),
    created_at  TIMESTAMPTZ NOT NULL
);
