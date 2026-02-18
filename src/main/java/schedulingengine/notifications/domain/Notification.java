package schedulingengine.notifications.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * A stored notification (e.g. after schedule optimized).
 */
public record Notification(
    UUID id,
    UUID requestId,
    String message,
    Instant createdAt
) {}
