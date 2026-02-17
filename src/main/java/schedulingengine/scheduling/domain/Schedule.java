package schedulingengine.scheduling.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for a schedule. No JPA annotations; persistence is in adapters.
 */
public record Schedule(
    UUID id,
    Instant startTime,
    Instant endTime,
    String title,
    Instant createdAt
) {
    public static Schedule create(UUID id, Instant startTime, Instant endTime, String title) {
        return new Schedule(id, startTime, endTime, title, Instant.now());
    }
}
