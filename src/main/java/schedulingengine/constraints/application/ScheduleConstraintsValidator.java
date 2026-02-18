package schedulingengine.constraints.application;

import org.springframework.stereotype.Component;

import schedulingengine.constraints.domain.ValidationResult;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates schedule request constraints: earliestStart &lt; latestEnd, durationMinutes &gt; 0,
 * and earliestStart + duration fits within latestEnd.
 */
@Component
class ScheduleConstraintsValidator {

    ValidationResult validate(Instant earliestStart, Instant latestEnd, int durationMinutes, Instant preferredStart) {
        List<String> reasons = new ArrayList<>();

        if (earliestStart == null) {
            reasons.add("earliestStart is required");
        }
        if (latestEnd == null) {
            reasons.add("latestEnd is required");
        }
        if (earliestStart != null && latestEnd != null && !latestEnd.isAfter(earliestStart)) {
            reasons.add("latestEnd must be after earliestStart");
        }
        if (durationMinutes <= 0) {
            reasons.add("durationMinutes must be greater than 0");
        }
        if (earliestStart != null && latestEnd != null && durationMinutes > 0) {
            Instant slotEnd = earliestStart.plus(Duration.ofMinutes(durationMinutes));
            if (slotEnd.isAfter(latestEnd)) {
                reasons.add("earliestStart + durationMinutes must not exceed latestEnd");
            }
        }

        return reasons.isEmpty()
            ? ValidationResult.passed()
            : ValidationResult.failed(reasons);
    }
}
