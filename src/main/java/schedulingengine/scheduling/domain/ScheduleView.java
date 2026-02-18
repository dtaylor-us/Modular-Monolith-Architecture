package schedulingengine.scheduling.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Read model for a schedule request's status and optimized slot.
 */
public record ScheduleView(
    UUID requestId,
    String status,
    Instant optimizedStart,
    Instant optimizedEnd,
    Instant updatedAt
) {
    public static final String STATUS_REQUESTED = "REQUESTED";
    public static final String STATUS_CONSTRAINTS_FAILED = "CONSTRAINTS_FAILED";
    public static final String STATUS_OPTIMIZED = "OPTIMIZED";
    public static final String STATUS_OPTIMIZATION_FAILED = "OPTIMIZATION_FAILED";
}
