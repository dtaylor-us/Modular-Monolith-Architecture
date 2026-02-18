package schedulingengine.optimization.application;

import org.springframework.stereotype.Component;

import schedulingengine.optimization.domain.OptimizedSchedule;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Phase 1 optimizer: chooses earliest valid slot; uses preferredStart when it fits.
 */
@Component
class ScheduleOptimizer {

    private static final String STRATEGY_PREFERRED = "PREFERRED_START";
    private static final String STRATEGY_EARLIEST = "EARLIEST_START";

    /**
     * Chooses a slot within [earliestStart, latestEnd] that fits durationMinutes.
     * If preferredStart is present and fits, uses it; otherwise uses earliestStart.
     * Returns empty only if no slot fits (constraints should prevent this).
     */
    Optional<OptimizedSchedule> optimize(
        UUID requestId,
        Instant earliestStart,
        Instant latestEnd,
        int durationMinutes,
        Instant preferredStart
    ) {
        Duration duration = Duration.ofMinutes(durationMinutes);
        Instant slotStart;
        String strategy;

        if (preferredStart != null
            && !preferredStart.isBefore(earliestStart)
            && !preferredStart.plus(duration).isAfter(latestEnd)) {
            slotStart = preferredStart;
            strategy = STRATEGY_PREFERRED;
        } else {
            // Earliest valid slot
            if (earliestStart.plus(duration).isAfter(latestEnd)) {
                return Optional.empty(); // no slot fits
            }
            slotStart = earliestStart;
            strategy = STRATEGY_EARLIEST;
        }

        Instant slotEnd = slotStart.plus(duration);
        return Optional.of(new OptimizedSchedule(
            UUID.randomUUID(),
            requestId,
            slotStart,
            slotEnd,
            strategy,
            Instant.now()
        ));
    }
}
