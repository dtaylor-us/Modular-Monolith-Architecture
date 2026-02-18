package schedulingengine.scheduling.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * A request for a schedule slot (time window + duration). The actual slot is chosen by the optimizer.
 */
public record ScheduleRequest(
    UUID id,
    String title,
    Instant earliestStart,
    Instant latestEnd,
    int durationMinutes,
    Instant preferredStart,
    String priority,
    Instant createdAt,
    String status
) {
    public static final String STATUS_REQUESTED = "REQUESTED";

    public static ScheduleRequest create(
        UUID id,
        String title,
        Instant earliestStart,
        Instant latestEnd,
        int durationMinutes,
        Instant preferredStart,
        String priority
    ) {
        return new ScheduleRequest(
            id,
            title,
            earliestStart,
            latestEnd,
            durationMinutes,
            preferredStart,
            priority,
            Instant.now(),
            STATUS_REQUESTED
        );
    }
}
