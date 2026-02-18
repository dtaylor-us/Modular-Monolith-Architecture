package schedulingengine.notifications.adapters.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.notifications.application.NotificationRepository;
import schedulingengine.notifications.domain.Notification;
import schedulingengine.optimization.ScheduleOptimized;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Stores a notification when a schedule is optimized.
 */
@Component
class ScheduleOptimizedListener {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .withZone(ZoneOffset.UTC);

    private final NotificationRepository notificationRepository;

    ScheduleOptimizedListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    void onScheduleOptimized(ScheduleOptimized event) {
        String startStr = FORMAT.format(event.optimizedStart());
        String endStr = FORMAT.format(event.optimizedEnd());
        String titlePart = (event.title() != null && !event.title().isBlank())
            ? " '" + event.title() + "'"
            : "";
        String message = String.format("Schedule optimized for request %s%s: %s - %s",
            event.requestId(), titlePart, startStr, endStr);
        notificationRepository.save(new Notification(
            UUID.randomUUID(),
            event.requestId(),
            message,
            Instant.now()
        ));
    }
}
