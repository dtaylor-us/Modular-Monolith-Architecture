package schedulingengine.scheduling.application;

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

import schedulingengine.scheduling.ScheduleRequested;
import schedulingengine.scheduling.domain.Schedule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    classes = ScheduleApplicationServiceTest.TestConfig.class,
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@DisplayName("Schedule application service")
class ScheduleApplicationServiceTest {

    @Autowired
    ScheduleCommandService commandService;

    @Autowired
    ScheduleQueryService queryService;

    @Autowired
    List<ScheduleRequested> publishedEvents;

    @Test
    @DisplayName("create schedule persists and publishes ScheduleRequested")
    void createSchedulePersistsAndPublishesEvent() {
        var start = Instant.parse("2025-02-20T09:00:00Z");
        var end = Instant.parse("2025-02-20T17:00:00Z");
        var title = "Team sync";
        publishedEvents.clear();

        var result = commandService.createSchedule(new CreateScheduleCommand(start, end, title));

        assertThat(result.scheduleId()).isNotNull();
        Optional<Schedule> loaded = queryService.findById(result.scheduleId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().startTime()).isEqualTo(start);
        assertThat(loaded.get().endTime()).isEqualTo(end);
        assertThat(loaded.get().title()).isEqualTo(title);
        assertThat(publishedEvents).hasSize(1);
        assertThat(publishedEvents.get(0).scheduleId()).isEqualTo(result.scheduleId());
        assertThat(publishedEvents.get(0).title()).isEqualTo(title);
    }

    @Configuration
    @Import(ScheduleApplicationService.class)
    static class TestConfig {

        @Bean
        List<ScheduleRequested> publishedEvents() {
            return new ArrayList<>();
        }

        @Bean
        ScheduleRepository scheduleRepository() {
            return new ScheduleRepository() {
                private final java.util.Map<UUID, Schedule> store = new java.util.HashMap<>();

                @Override
                public Schedule save(Schedule schedule) {
                    store.put(schedule.id(), schedule);
                    return schedule;
                }

                @Override
                public Schedule findById(UUID id) {
                    return store.get(id);
                }
            };
        }

        @Bean
        ApplicationEventPublisher eventPublisher(List<ScheduleRequested> publishedEvents) {
            return event -> {
                if (event instanceof ScheduleRequested e) {
                    publishedEvents.add(e);
                }
            };
        }
    }
}
