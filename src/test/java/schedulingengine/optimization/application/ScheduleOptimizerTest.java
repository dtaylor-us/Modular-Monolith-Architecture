package schedulingengine.optimization.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import schedulingengine.optimization.domain.OptimizedSchedule;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Schedule optimizer")
class ScheduleOptimizerTest {

    private final ScheduleOptimizer optimizer = new ScheduleOptimizer();

    @Nested
    @DisplayName("earliest valid slot")
    class EarliestSlot {

        @Test
        @DisplayName("chooses earliestStart when no preferredStart")
        void choosesEarliestWhenNoPreferred() {
            UUID requestId = UUID.randomUUID();
            Instant earliest = Instant.parse("2026-02-18T09:00:00Z");
            Instant latest = Instant.parse("2026-02-18T17:00:00Z");
            int durationMinutes = 90;

            Optional<OptimizedSchedule> result = optimizer.optimize(requestId, earliest, latest, durationMinutes, null);

            assertThat(result).isPresent();
            assertThat(result.get().scheduleId()).isEqualTo(requestId);
            assertThat(result.get().optimizedStart()).isEqualTo(earliest);
            assertThat(result.get().optimizedEnd()).isEqualTo(earliest.plusSeconds(90 * 60));
            assertThat(result.get().strategyUsed()).isEqualTo("EARLIEST_START");
        }

        @Test
        @DisplayName("chooses earliestStart when preferredStart is before window")
        void choosesEarliestWhenPreferredBeforeWindow() {
            UUID requestId = UUID.randomUUID();
            Instant earliest = Instant.parse("2026-02-18T09:00:00Z");
            Instant latest = Instant.parse("2026-02-18T17:00:00Z");
            Instant preferred = Instant.parse("2026-02-18T08:00:00Z"); // before earliest

            Optional<OptimizedSchedule> result = optimizer.optimize(requestId, earliest, latest, 60, preferred);

            assertThat(result).isPresent();
            assertThat(result.get().optimizedStart()).isEqualTo(earliest);
            assertThat(result.get().strategyUsed()).isEqualTo("EARLIEST_START");
        }

        @Test
        @DisplayName("chooses earliestStart when preferredStart would exceed latestEnd")
        void choosesEarliestWhenPreferredExceedsEnd() {
            UUID requestId = UUID.randomUUID();
            Instant earliest = Instant.parse("2026-02-18T09:00:00Z");
            Instant latest = Instant.parse("2026-02-18T11:00:00Z"); // 2h window
            Instant preferred = Instant.parse("2026-02-18T10:30:00Z"); // 90 min from preferred would be 12:00

            Optional<OptimizedSchedule> result = optimizer.optimize(requestId, earliest, latest, 90, preferred);

            assertThat(result).isPresent();
            assertThat(result.get().optimizedStart()).isEqualTo(earliest);
            assertThat(result.get().strategyUsed()).isEqualTo("EARLIEST_START");
        }
    }

    @Nested
    @DisplayName("preferred start")
    class PreferredStart {

        @Test
        @DisplayName("chooses preferredStart when it fits in window")
        void choosesPreferredWhenFits() {
            UUID requestId = UUID.randomUUID();
            Instant earliest = Instant.parse("2026-02-18T09:00:00Z");
            Instant latest = Instant.parse("2026-02-18T17:00:00Z");
            Instant preferred = Instant.parse("2026-02-18T10:00:00Z");
            int durationMinutes = 90;

            Optional<OptimizedSchedule> result = optimizer.optimize(requestId, earliest, latest, durationMinutes, preferred);

            assertThat(result).isPresent();
            assertThat(result.get().optimizedStart()).isEqualTo(preferred);
            assertThat(result.get().optimizedEnd()).isEqualTo(preferred.plusSeconds(90 * 60));
            assertThat(result.get().strategyUsed()).isEqualTo("PREFERRED_START");
        }
    }

    @Test
    @DisplayName("returns empty when no slot fits")
    void returnsEmptyWhenNoSlotFits() {
        UUID requestId = UUID.randomUUID();
        Instant earliest = Instant.parse("2026-02-18T09:00:00Z");
        Instant latest = Instant.parse("2026-02-18T10:00:00Z"); // 1 hour window

        Optional<OptimizedSchedule> result = optimizer.optimize(requestId, earliest, latest, 90, null);

        assertThat(result).isEmpty();
    }
}
