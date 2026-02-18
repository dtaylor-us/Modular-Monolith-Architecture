package schedulingengine.constraints;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Public API event: emitted when schedule request constraints fail validation.
 */
public record ConstraintsFailed(
    UUID requestId,
    List<String> reasons,
    Instant failedAt
) {}
