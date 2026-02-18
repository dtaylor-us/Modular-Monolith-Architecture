package schedulingengine.constraints;

import java.time.Instant;
import java.util.UUID;

/**
 * Public API event: emitted when schedule request constraints pass validation.
 * Carries request data so optimization can choose slot without calling scheduling.
 */
public record ConstraintsValidated(
    UUID requestId,
    String title,
    Instant earliestStart,
    Instant latestEnd,
    int durationMinutes,
    Instant preferredStart,
    Instant validatedAt
) {}
