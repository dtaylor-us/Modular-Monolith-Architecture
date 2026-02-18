package schedulingengine.optimization.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model produced by the optimization module.
 */
public record OptimizedSchedule(
    UUID id,
    UUID scheduleId, // requestId
    Instant optimizedStart,
    Instant optimizedEnd,
    String strategyUsed,
    Instant optimizedAt
) {}

