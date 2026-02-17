package schedulingengine.scheduling.adapters.web;

import java.time.Instant;
import java.util.UUID;

public record ScheduleResponse(
    UUID id,
    Instant start,
    Instant end,
    String title,
    Instant createdAt
) {}
