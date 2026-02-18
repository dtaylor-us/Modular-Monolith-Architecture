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
    @DisplayName("publishes ConstraintsValidated for valid request")
    void publishesConstraintsValidatedForValidRequest() {
        validatedEvents.clear();
        failedEvents.clear();

        eventPublisher.publishEvent(new ScheduleRequested(
            UUID.randomUUID(),
            null,
            Instant.parse("2026-02-18T09:00:00Z"),
            Instant.parse("2026-02-18T17:00:00Z"),
            90,
            null,
            null,
            Instant.now()
        ));

        assertThat(validatedEvents).hasSize(1);
        assertThat(failedEvents).isEmpty();
    }

    @Test
    @DisplayName("publishes ConstraintsFailed for invalid request")
    void publishesConstraintsFailedForInvalidRequest() {
        validatedEvents.clear();
        failedEvents.clear();

        eventPublisher.publishEvent(new ScheduleRequested(
            UUID.randomUUID(),
            null,
            Instant.parse("2026-02-18T17:00:00Z"),
            Instant.parse("2026-02-18T09:00:00Z"),
            60,
            null,
            null,
            Instant.now()
        ));

        assertThat(failedEvents).hasSize(1);
        assertThat(failedEvents.get(0).reasons()).anyMatch(r -> r.contains("latestEnd must be after earliestStart"));
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
