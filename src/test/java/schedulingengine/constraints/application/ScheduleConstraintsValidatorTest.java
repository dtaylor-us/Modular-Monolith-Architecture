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
    @DisplayName("valid request")
    class ValidRequest {

        @Test
        @DisplayName("passes when earliestStart < latestEnd and duration fits")
        void passes() {
            Instant earliestStart = Instant.parse("2026-02-18T09:00:00Z");
            Instant latestEnd = Instant.parse("2026-02-18T17:00:00Z");
            int durationMinutes = 90;

            ValidationResult result = validator.validate(earliestStart, latestEnd, durationMinutes, null);

            assertThat(result.valid()).isTrue();
            assertThat(result.reasons()).isEmpty();
        }

        @Test
        @DisplayName("passes when preferredStart is within window")
        void passesWithPreferredStart() {
            Instant earliestStart = Instant.parse("2026-02-18T09:00:00Z");
            Instant latestEnd = Instant.parse("2026-02-18T17:00:00Z");
            Instant preferredStart = Instant.parse("2026-02-18T10:00:00Z");

            ValidationResult result = validator.validate(earliestStart, latestEnd, 60, preferredStart);

            assertThat(result.valid()).isTrue();
        }
    }

    @Nested
    @DisplayName("invalid request")
    class InvalidRequest {

        @Test
        @DisplayName("fails when latestEnd not after earliestStart")
        void latestEndNotAfterEarliestStart() {
            Instant earliestStart = Instant.parse("2026-02-18T17:00:00Z");
            Instant latestEnd = Instant.parse("2026-02-18T09:00:00Z");

            ValidationResult result = validator.validate(earliestStart, latestEnd, 60, null);

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("latestEnd must be after earliestStart"));
        }

        @Test
        @DisplayName("fails when durationMinutes is zero")
        void durationZero() {
            Instant earliestStart = Instant.parse("2026-02-18T09:00:00Z");
            Instant latestEnd = Instant.parse("2026-02-18T17:00:00Z");

            ValidationResult result = validator.validate(earliestStart, latestEnd, 0, null);

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("durationMinutes"));
        }

        @Test
        @DisplayName("fails when earliestStart + duration exceeds latestEnd")
        void slotExceedsLatestEnd() {
            Instant earliestStart = Instant.parse("2026-02-18T09:00:00Z");
            Instant latestEnd = Instant.parse("2026-02-18T10:00:00Z"); // only 1 hour

            ValidationResult result = validator.validate(earliestStart, latestEnd, 90, null);

            assertThat(result.valid()).isFalse();
            assertThat(result.reasons()).anyMatch(r -> r.contains("must not exceed latestEnd"));
        }
    }
}
