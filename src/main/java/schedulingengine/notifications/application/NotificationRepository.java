package schedulingengine.notifications.application;

import schedulingengine.notifications.domain.Notification;

import java.util.List;

/**
 * Port for storing and querying notifications.
 */
public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findAllByCreatedAtDesc();
}
