package schedulingengine.constraints.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

import schedulingengine.constraints.ConstraintsFailed;
import schedulingengine.constraints.ConstraintsValidated;
import schedulingengine.scheduling.ScheduleRequested;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = ScheduleRequestedListenerTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@DisplayName("ScheduleRequested listener")
class ScheduleRequestedListenerTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    List<ConstraintsValidated> validatedEvents;

    @Autowired
    List<ConstraintsFailed> failedEvents;

    @Test
    @DisplayName("publishes ConstraintsValidated for valid schedule")
    void publishesConstraintsValidatedForValidSchedule() {
        validatedEvents.clear();
        failedEvents.clear();

        eventPublisher.publishEvent(new ScheduleRequested(
            UUID.randomUUID(),
            Instant.parse("2025-02-20T09:00:00Z"),
            Instant.parse("2025-02-20T17:00:00Z"),
            "Valid meeting",
            Instant.now()
        ));

        assertThat(validatedEvents).hasSize(1);
        assertThat(failedEvents).isEmpty();
    }

    @Test
    @DisplayName("publishes ConstraintsFailed for invalid schedule")
    void publishesConstraintsFailedForInvalidSchedule() {
        validatedEvents.clear();
        failedEvents.clear();

        eventPublisher.publishEvent(new ScheduleRequested(
            UUID.randomUUID(),
            Instant.parse("2025-02-20T17:00:00Z"),
            Instant.parse("2025-02-20T09:00:00Z"),
            "Invalid - end before start",
            Instant.now()
        ));

        assertThat(failedEvents).hasSize(1);
        assertThat(failedEvents.get(0).reasons()).anyMatch(r -> r.contains("endTime must be after startTime"));
        assertThat(validatedEvents).isEmpty();
    }

    @Configuration
    @Import({ ScheduleRequestedListener.class, ScheduleConstraintsValidator.class })
    static class TestConfig {

        @Bean
        List<ConstraintsValidated> validatedEvents() {
            return new ArrayList<>();
        }

        @Bean
        List<ConstraintsFailed> failedEvents() {
            return new ArrayList<>();
        }

        @Bean
        ConstraintsEventCapture constraintsEventCapture(
            List<ConstraintsValidated> validatedEvents,
            List<ConstraintsFailed> failedEvents
        ) {
            return new ConstraintsEventCapture(validatedEvents, failedEvents);
        }
    }

    static class ConstraintsEventCapture {

        private final List<ConstraintsValidated> validated;
        private final List<ConstraintsFailed> failed;

        ConstraintsEventCapture(List<ConstraintsValidated> validated, List<ConstraintsFailed> failed) {
            this.validated = validated;
            this.failed = failed;
        }

        @EventListener
        void onConstraintsValidated(ConstraintsValidated event) {
            validated.add(event);
        }

        @EventListener
        void onConstraintsFailed(ConstraintsFailed event) {
            failed.add(event);
        }
    }
}
