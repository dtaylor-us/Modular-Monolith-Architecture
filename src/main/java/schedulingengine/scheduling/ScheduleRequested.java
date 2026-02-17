package schedulingengine.scheduling;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when a schedule is created.
 * Other modules (constraints, etc.) may listen to this event.
 */
public record ScheduleRequested(
    UUID scheduleId,
    Instant startTime,
    Instant endTime,
    String title,
    Instant requestedAt
) {}
