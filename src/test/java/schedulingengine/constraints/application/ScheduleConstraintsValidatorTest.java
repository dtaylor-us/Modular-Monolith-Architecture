package schedulingengine.constraints.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import schedulingengine.constraints.domain.ValidationResult;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Schedule constraints validator")
class ScheduleConstraintsValidatorTest {

    private final ScheduleConstraintsValidator validator = new ScheduleConstraintsValidator();

    @Nested
    @DisplayName("valid schedule")
    class ValidSchedule {

        @Test
        @DisplayName("passes when start < end, duration <= 24h, title present")
        void passes() {
            Instant start = Instant.parse("2025-02-20T09:00:00Z");
            Instant end = Instant.parse("2025-02-20T17:00:00Z");

            ValidationResult result = validator.validate(start, end, "Team sync");

            assertThat(result.valid()).isTrue();
            assertThat(result.reasons()).isEmpty();
        }

        @Test
        @DisplayName("passes when duration exactly 24 hours")
        void passesMaxDuration() {
            Instant start = Instant.parse("2025-02-20T00:00:00Z");
            Instant end = Instant.parse("2025-02-21T00:00:00Z");

            ValidationResult result = validator.validate(start, end, "All day");

            assertThat(result.valid()).isTrue();
        }
    }

    @Nested
    @DisplayName("invalid schedule")
    class InvalidSchedule {

        @Test
        @DisplayName("fails when end not after start")
        void endNotAfterStart() {
            Instant start = Instant.parse("2025-02-20T17:00:00Z");
            Instant end = Instant.parse("2025-02-20T09:00:00Z");

            ValidationResult result = validator.validate(start, end, "Meeting");

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("endTime must be after startTime"));
        }

        @Test
        @DisplayName("fails when duration exceeds 24 hours")
        void durationExceedsMax() {
            Instant start = Instant.parse("2025-02-20T00:00:00Z");
            Instant end = Instant.parse("2025-02-21T01:00:00Z");

            ValidationResult result = validator.validate(start, end, "Long event");

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("duration must not exceed"));
        }

        @Test
        @DisplayName("fails when title is blank")
        void titleBlank() {
            Instant start = Instant.parse("2025-02-20T09:00:00Z");
            Instant end = Instant.parse("2025-02-20T17:00:00Z");

            ValidationResult result = validator.validate(start, end, "   ");

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("title"));
        }

        @Test
        @DisplayName("fails when title is null")
        void titleNull() {
            Instant start = Instant.parse("2025-02-20T09:00:00Z");
            Instant end = Instant.parse("2025-02-20T17:00:00Z");

            ValidationResult result = validator.validate(start, end, null);

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("title"));
        }
    }
}
