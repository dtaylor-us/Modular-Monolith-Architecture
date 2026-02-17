package schedulingengine.scheduling.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedules")
class ScheduleEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ScheduleEntity() {}

    ScheduleEntity(UUID id, Instant startTime, Instant endTime, String title, Instant createdAt) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.createdAt = createdAt;
    }

    UUID getId() { return id; }
    Instant getStartTime() { return startTime; }
    Instant getEndTime() { return endTime; }
    String getTitle() { return title; }
    Instant getCreatedAt() { return createdAt; }
}
