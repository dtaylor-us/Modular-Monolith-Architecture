package schedulingengine.notifications.adapters.persistence;

import org.springframework.stereotype.Component;

import schedulingengine.notifications.application.NotificationRepository;
import schedulingengine.notifications.domain.Notification;

import java.util.List;
import java.util.UUID;

@Component
class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        jpaRepository.save(new NotificationEntity(
            notification.id(),
            notification.requestId(),
            notification.message(),
            notification.createdAt()
        ));
        return notification;
    }

    @Override
    public List<Notification> findAllByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
            .map(e -> new Notification(e.getId(), e.getRequestId(), e.getMessage(), e.getCreatedAt()))
            .toList();
    }
}
