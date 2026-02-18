package schedulingengine.scheduling;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when a schedule request is created.
 * Payload allows constraints and optimization to run without querying scheduling.
 */
public record ScheduleRequested(
    UUID requestId,
    String title,
    Instant earliestStart,
    Instant latestEnd,
    int durationMinutes,
    Instant preferredStart,
    String priority,
    Instant requestedAt
) {}
