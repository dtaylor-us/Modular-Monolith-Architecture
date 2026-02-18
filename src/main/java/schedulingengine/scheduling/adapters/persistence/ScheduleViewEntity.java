package schedulingengine.scheduling.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule_views")
class ScheduleViewEntity {

    @Id
    @Column(name = "request_id", updatable = false, nullable = false)
    private UUID requestId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "optimized_start")
    private Instant optimizedStart;

    @Column(name = "optimized_end")
    private Instant optimizedEnd;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ScheduleViewEntity() {}

    ScheduleViewEntity(UUID requestId, String status, Instant optimizedStart, Instant optimizedEnd, Instant updatedAt) {
        this.requestId = requestId;
        this.status = status;
        this.optimizedStart = optimizedStart;
        this.optimizedEnd = optimizedEnd;
        this.updatedAt = updatedAt;
    }

    UUID getRequestId() { return requestId; }
    String getStatus() { return status; }
    Instant getOptimizedStart() { return optimizedStart; }
    Instant getOptimizedEnd() { return optimizedEnd; }
    Instant getUpdatedAt() { return updatedAt; }

    void setStatus(String status) { this.status = status; }
    void setOptimizedStart(Instant v) { this.optimizedStart = v; }
    void setOptimizedEnd(Instant v) { this.optimizedEnd = v; }
    void setUpdatedAt(Instant v) { this.updatedAt = v; }
}
