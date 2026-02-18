package schedulingengine.constraints.application;

import org.springframework.stereotype.Component;

import schedulingengine.constraints.domain.ValidationResult;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates schedule constraints: start &lt; end, duration within limit, non-blank title.
 */
@Component
class ScheduleConstraintsValidator {

    private static final Duration MAX_DURATION = Duration.ofHours(24);

    ValidationResult validate(Instant startTime, Instant endTime, String title) {
        List<String> reasons = new ArrayList<>();

        if (startTime == null) {
            reasons.add("startTime is required");
        }
        if (endTime == null) {
            reasons.add("endTime is required");
        }
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            reasons.add("endTime must be after startTime");
        }
        if (startTime != null && endTime != null && endTime.isAfter(startTime)) {
            Duration duration = Duration.between(startTime, endTime);
            if (duration.compareTo(MAX_DURATION) > 0) {
                reasons.add("duration must not exceed " + MAX_DURATION.toHours() + " hours");
            }
        }
        if (title == null || title.isBlank()) {
            reasons.add("title is required and must not be blank");
        }

        return reasons.isEmpty()
            ? ValidationResult.passed()
            : ValidationResult.failed(reasons);
    }
}
