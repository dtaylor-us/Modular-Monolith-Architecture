CREATE TABLE schedule_views (
    request_id       UUID PRIMARY KEY,
    status           VARCHAR(50) NOT NULL,
    optimized_start  TIMESTAMPTZ,
    optimized_end    TIMESTAMPTZ,
    updated_at       TIMESTAMPTZ NOT NULL
);
