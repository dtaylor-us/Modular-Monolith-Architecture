package schedulingengine.optimization.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "optimized_schedules")
class OptimizedScheduleEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "algorithm_version", nullable = false, length = 50)
    private String algorithmVersion;

    @Column(name = "summary", nullable = false, length = 1000)
    private String summary;

    @Column(name = "optimized_start")
    private Instant optimizedStart;

    @Column(name = "optimized_end")
    private Instant optimizedEnd;

    @Column(name = "strategy_used", length = 50)
    private String strategyUsed;

    @Column(name = "optimized_at", nullable = false)
    private Instant optimizedAt;

    protected OptimizedScheduleEntity() {}

    OptimizedScheduleEntity(UUID id, UUID scheduleId, String algorithmVersion, String summary,
                            Instant optimizedStart, Instant optimizedEnd, String strategyUsed, Instant optimizedAt) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.algorithmVersion = algorithmVersion;
        this.summary = summary;
        this.optimizedStart = optimizedStart;
        this.optimizedEnd = optimizedEnd;
        this.strategyUsed = strategyUsed;
        this.optimizedAt = optimizedAt;
    }

    UUID getId() { return id; }
    UUID getScheduleId() { return scheduleId; }
    String getAlgorithmVersion() { return algorithmVersion; }
    String getSummary() { return summary; }
    Instant getOptimizedStart() { return optimizedStart; }
    Instant getOptimizedEnd() { return optimizedEnd; }
    String getStrategyUsed() { return strategyUsed; }
    Instant getOptimizedAt() { return optimizedAt; }
}

