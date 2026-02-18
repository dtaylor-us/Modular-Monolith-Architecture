package schedulingengine.optimization;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when optimization cannot produce a valid schedule.
 */
public record OptimizationFailed(
    UUID requestId,
    String reason,
    Instant failedAt
) {}
