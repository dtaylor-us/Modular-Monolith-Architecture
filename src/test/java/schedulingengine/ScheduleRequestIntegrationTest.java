package schedulingengine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test: POST schedule request, wait for optimization, assert slot choice.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Schedule request integration")
class ScheduleRequestIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("scheduling")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    @DisplayName("optimizer picks preferredStart when it fits in window")
    void prefersPreferredStartWhenValid() {
        Map<String, Object> body = Map.of(
            "earliestStart", "2026-02-18T09:00:00",
            "latestEnd", "2026-02-18T17:00:00",
            "durationMinutes", 90,
            "preferredStart", "2026-02-18T10:00:00",
            "priority", "HIGH"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ScheduleResponse> create = restTemplate.postForEntity(
            "/api/schedules", new HttpEntity<>(body, headers), ScheduleResponse.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(create.getBody()).isNotNull();
        var requestId = create.getBody().requestId();
        assertThat(requestId).isNotNull();

        // Poll until optimized (events are async)
        ScheduleResponse response = pollUntilOptimized(requestId, Duration.ofSeconds(10));
        assertThat(response.status()).isEqualTo("OPTIMIZED");
        assertThat(response.optimizedStart()).isNotNull();
        assertThat(response.optimizedEnd()).isNotNull();
        // Preferred was 2026-02-18T10:00:00 UTC
        assertThat(response.optimizedStart()).isEqualTo(Instant.parse("2026-02-18T10:00:00Z"));
        assertThat(response.optimizedEnd()).isEqualTo(Instant.parse("2026-02-18T11:30:00Z"));
    }

    @Test
    @DisplayName("optimizer picks earliestStart when preferredStart does not fit")
    void fallsBackToEarliestWhenPreferredInvalid() {
        Map<String, Object> body = Map.of(
            "earliestStart", "2026-02-18T09:00:00",
            "latestEnd", "2026-02-18T17:00:00",
            "durationMinutes", 60,
            "preferredStart", "2026-02-18T08:00:00",
            "priority", "LOW"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ScheduleResponse> create = restTemplate.postForEntity(
            "/api/schedules", new HttpEntity<>(body, headers), ScheduleResponse.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(create.getBody()).isNotNull();
        var requestId = create.getBody().requestId();

        ScheduleResponse response = pollUntilOptimized(requestId, Duration.ofSeconds(10));
        assertThat(response.status()).isEqualTo("OPTIMIZED");
        assertThat(response.optimizedStart()).isEqualTo(Instant.parse("2026-02-18T09:00:00Z"));
        assertThat(response.optimizedEnd()).isEqualTo(Instant.parse("2026-02-18T10:00:00Z"));
    }

    private ScheduleResponse pollUntilOptimized(UUID requestId, Duration timeout) {
        Instant deadline = Instant.now().plus(timeout);
        while (Instant.now().isBefore(deadline)) {
            ResponseEntity<ScheduleResponse> get = restTemplate.getForEntity(
                "/api/schedules/" + requestId, ScheduleResponse.class);
            if (get.getStatusCode() == HttpStatus.OK && get.getBody() != null) {
                String status = get.getBody().status();
                if ("OPTIMIZED".equals(status) || "CONSTRAINTS_FAILED".equals(status)
                    || "OPTIMIZATION_FAILED".equals(status)) {
                    return get.getBody();
                }
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new AssertionError("Did not reach OPTIMIZED (or failed) within " + timeout);
    }

    /** DTO matching API response for REST binding */
    record ScheduleResponse(
        UUID requestId,
        String title,
        String status,
        Instant earliestStart,
        Instant latestEnd,
        int durationMinutes,
        Instant preferredStart,
        String priority,
        Instant createdAt,
        Instant optimizedStart,
        Instant optimizedEnd
    ) {}
}
