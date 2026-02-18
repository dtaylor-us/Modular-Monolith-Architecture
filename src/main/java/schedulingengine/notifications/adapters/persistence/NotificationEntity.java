package schedulingengine.notifications.adapters.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
class NotificationEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "request_id", nullable = false)
    private UUID requestId;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected NotificationEntity() {}

    NotificationEntity(UUID id, UUID requestId, String message, Instant createdAt) {
        this.id = id;
        this.requestId = requestId;
        this.message = message;
        this.createdAt = createdAt;
    }

    UUID getId() { return id; }
    UUID getRequestId() { return requestId; }
    String getMessage() { return message; }
    Instant getCreatedAt() { return createdAt; }
}
