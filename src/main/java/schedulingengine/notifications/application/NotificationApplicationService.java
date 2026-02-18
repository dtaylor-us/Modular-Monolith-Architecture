package schedulingengine.notifications.application;

import org.springframework.stereotype.Service;

import schedulingengine.notifications.domain.Notification;

import java.util.List;

@Service
class NotificationApplicationService implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    NotificationApplicationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> findAllByCreatedAtDesc() {
        return notificationRepository.findAllByCreatedAtDesc();
    }
}
