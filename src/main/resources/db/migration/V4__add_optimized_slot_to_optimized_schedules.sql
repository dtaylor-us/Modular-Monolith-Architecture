ALTER TABLE optimized_schedules
    ADD COLUMN optimized_start TIMESTAMPTZ,
    ADD COLUMN optimized_end   TIMESTAMPTZ,
    ADD COLUMN strategy_used   VARCHAR(50);
