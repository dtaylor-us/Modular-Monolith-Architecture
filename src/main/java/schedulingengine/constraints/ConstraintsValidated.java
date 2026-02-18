package schedulingengine.constraints;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when schedule constraints pass validation.
 * Optimization module may listen to this event.
 */
public record ConstraintsValidated(
    UUID scheduleId,
    Instant validatedAt
) {}
