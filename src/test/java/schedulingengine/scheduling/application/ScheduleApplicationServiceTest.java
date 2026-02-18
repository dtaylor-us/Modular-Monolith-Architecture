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
import schedulingengine.scheduling.domain.ScheduleRequest;
import schedulingengine.scheduling.domain.ScheduleView;

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
    @DisplayName("create schedule request persists and publishes ScheduleRequested")
    void createScheduleRequestPersistsAndPublishesEvent() {
        var title = "Team sync";
        var earliestStart = Instant.parse("2026-02-18T09:00:00Z");
        var latestEnd = Instant.parse("2026-02-18T17:00:00Z");
        var durationMinutes = 90;
        var preferredStart = Instant.parse("2026-02-18T10:00:00Z");
        var priority = "HIGH";
        publishedEvents.clear();

        var result = commandService.createSchedule(new CreateScheduleCommand(
            title, earliestStart, latestEnd, durationMinutes, preferredStart, priority));

        assertThat(result.requestId()).isNotNull();
        Optional<ScheduleRequest> loaded = queryService.findById(result.requestId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().title()).isEqualTo(title);
        assertThat(loaded.get().earliestStart()).isEqualTo(earliestStart);
        assertThat(loaded.get().latestEnd()).isEqualTo(latestEnd);
        assertThat(loaded.get().durationMinutes()).isEqualTo(durationMinutes);
        assertThat(loaded.get().preferredStart()).isEqualTo(preferredStart);
        assertThat(loaded.get().priority()).isEqualTo(priority);
        assertThat(publishedEvents).hasSize(1);
        assertThat(publishedEvents.get(0).requestId()).isEqualTo(result.requestId());
        assertThat(publishedEvents.get(0).title()).isEqualTo(title);
        assertThat(publishedEvents.get(0).durationMinutes()).isEqualTo(durationMinutes);
    }

    @Configuration
    @Import(ScheduleApplicationService.class)
    static class TestConfig {

        @Bean
        List<ScheduleRequested> publishedEvents() {
            return new ArrayList<>();
        }

        @Bean
        ScheduleRequestRepository scheduleRequestRepository() {
            return new ScheduleRequestRepository() {
                private final java.util.Map<UUID, ScheduleRequest> store = new java.util.HashMap<>();

                @Override
                public ScheduleRequest save(ScheduleRequest request) {
                    store.put(request.id(), request);
                    return request;
                }

                @Override
                public ScheduleRequest findById(UUID id) {
                    return store.get(id);
                }
            };
        }

        @Bean
        ScheduleViewRepository scheduleViewRepository() {
            return new ScheduleViewRepository() {
                private final java.util.Map<UUID, ScheduleView> store = new java.util.HashMap<>();

                @Override
                public void save(ScheduleView view) {
                    store.put(view.requestId(), view);
                }

                @Override
                public ScheduleView findByRequestId(UUID requestId) {
                    return store.get(requestId);
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
