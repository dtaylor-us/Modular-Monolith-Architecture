package schedulingengine.notifications.application;

import schedulingengine.notifications.domain.Notification;

import java.util.List;

/**
 * Port for querying notifications. Used by the web adapter.
 */
public interface NotificationQueryService {

    List<Notification> findAllByCreatedAtDesc();
}
