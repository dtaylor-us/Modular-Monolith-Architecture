-- Remove legacy schedules table; scheduling uses schedule_requests and schedule_views.
DROP TABLE IF EXISTS schedules;
