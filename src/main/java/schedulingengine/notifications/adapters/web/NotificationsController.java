package schedulingengine.notifications.adapters.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import schedulingengine.notifications.application.NotificationQueryService;
import schedulingengine.notifications.domain.Notification;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
class NotificationsController {

    private final NotificationQueryService queryService;

    NotificationsController(NotificationQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    ResponseEntity<List<NotificationResponse>> list() {
        List<NotificationResponse> body = queryService.findAllByCreatedAtDesc().stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(body);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.id(), n.requestId(), n.message(), n.createdAt());
    }
}
