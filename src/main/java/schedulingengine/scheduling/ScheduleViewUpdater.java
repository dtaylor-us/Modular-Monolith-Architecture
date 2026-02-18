package schedulingengine.scheduling;

import java.time.Instant;
import java.util.UUID;

/**
 * Port for updating the schedule view from external events.
 * Used by the scheduleview integration listener.
 */
public interface ScheduleViewUpdater {

    void recordConstraintsFailed(UUID requestId);

    void recordOptimized(UUID requestId, Instant optimizedStart, Instant optimizedEnd);

    void recordOptimizationFailed(UUID requestId);
}
