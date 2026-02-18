package schedulingengine.optimization.application;

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

import schedulingengine.constraints.ConstraintsValidated;
import schedulingengine.optimization.ScheduleOptimized;
import schedulingengine.optimization.domain.OptimizedSchedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = ConstraintsValidatedListenerTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@DisplayName("ConstraintsValidated listener")
class ConstraintsValidatedListenerTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    InMemoryOptimizationRepository repository;

    @Autowired
    List<ScheduleOptimized> optimizedEvents;

    @Test
    @DisplayName("persists optimized schedule and publishes ScheduleOptimized")
    void persistsAndPublishes() {
        optimizedEvents.clear();
        repository.saved.clear();

        UUID requestId = UUID.randomUUID();
        Instant now = Instant.now();
        eventPublisher.publishEvent(new ConstraintsValidated(
            requestId,
            "Team sync",
            Instant.parse("2026-02-18T09:00:00Z"),
            Instant.parse("2026-02-18T17:00:00Z"),
            90,
            null,
            now
        ));

        assertThat(repository.saved).hasSize(1);
        OptimizedSchedule saved = repository.saved.get(0);
        assertThat(saved.scheduleId()).isEqualTo(requestId);
        assertThat(saved.optimizedStart()).isNotNull();
        assertThat(saved.optimizedEnd()).isNotNull();

        assertThat(optimizedEvents).hasSize(1);
        assertThat(optimizedEvents.get(0).requestId()).isEqualTo(requestId);
        assertThat(optimizedEvents.get(0).optimizedStart()).isEqualTo(saved.optimizedStart());
        assertThat(optimizedEvents.get(0).optimizedEnd()).isEqualTo(saved.optimizedEnd());
    }

    @Configuration
    @Import({ ConstraintsValidatedListener.class, ScheduleOptimizer.class })
    static class TestConfig {

        @Bean
        InMemoryOptimizationRepository optimizationRepository() {
            return new InMemoryOptimizationRepository();
        }

        @Bean
        List<ScheduleOptimized> optimizedEvents() {
            return new ArrayList<>();
        }

        @Bean
        ScheduleOptimizedCapture scheduleOptimizedCapture(List<ScheduleOptimized> optimizedEvents) {
            return new ScheduleOptimizedCapture(optimizedEvents);
        }
    }

    static class InMemoryOptimizationRepository implements OptimizationRepository {
        final List<OptimizedSchedule> saved = new ArrayList<>();

        @Override
        public OptimizedSchedule save(OptimizedSchedule schedule) {
            saved.add(schedule);
            return schedule;
        }
    }

    static class ScheduleOptimizedCapture {
        private final List<ScheduleOptimized> events;

        ScheduleOptimizedCapture(List<ScheduleOptimized> events) {
            this.events = events;
        }

        @EventListener
        void onScheduleOptimized(ScheduleOptimized event) {
            events.add(event);
        }
    }
}

