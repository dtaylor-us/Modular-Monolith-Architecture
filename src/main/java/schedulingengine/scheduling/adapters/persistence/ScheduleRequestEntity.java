package schedulingengine.scheduling.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule_requests")
class ScheduleRequestEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "earliest_start", nullable = false)
    private Instant earliestStart;

    @Column(name = "latest_end", nullable = false)
    private Instant latestEnd;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "preferred_start")
    private Instant preferredStart;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    protected ScheduleRequestEntity() {}

    ScheduleRequestEntity(UUID id, String title, Instant earliestStart, Instant latestEnd, int durationMinutes,
                          Instant preferredStart, String priority, Instant createdAt, String status) {
        this.id = id;
        this.title = title;
        this.earliestStart = earliestStart;
        this.latestEnd = latestEnd;
        this.durationMinutes = durationMinutes;
        this.preferredStart = preferredStart;
        this.priority = priority;
        this.createdAt = createdAt;
        this.status = status;
    }

    UUID getId() { return id; }
    String getTitle() { return title; }
    Instant getEarliestStart() { return earliestStart; }
    Instant getLatestEnd() { return latestEnd; }
    int getDurationMinutes() { return durationMinutes; }
    Instant getPreferredStart() { return preferredStart; }
    String getPriority() { return priority; }
    Instant getCreatedAt() { return createdAt; }
    String getStatus() { return status; }
}
