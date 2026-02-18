package schedulingengine.optimization;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when an optimized schedule is produced.
 * Notifications module may listen to this event.
 */
public record ScheduleOptimized(
    UUID requestId,
    String title,
    Instant optimizedStart,
    Instant optimizedEnd,
    Instant optimizedAt
) {}

