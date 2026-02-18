package schedulingengine.notifications.adapters.web;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for a single notification.
 */
public record NotificationResponse(
    UUID id,
    UUID requestId,
    String message,
    Instant createdAt
) {}
