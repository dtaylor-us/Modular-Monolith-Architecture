package schedulingengine.scheduling.adapters.web;

import java.time.Instant;
import java.util.UUID;

/**
 * Response for a schedule request. Includes request data and, when available, view status and optimized slot.
 */
public record ScheduleResponse(
    UUID requestId,
    String title,
    String status,
    Instant earliestStart,
    Instant latestEnd,
    int durationMinutes,
    Instant preferredStart,
    String priority,
    Instant createdAt,
    Instant optimizedStart,
    Instant optimizedEnd
) {}
