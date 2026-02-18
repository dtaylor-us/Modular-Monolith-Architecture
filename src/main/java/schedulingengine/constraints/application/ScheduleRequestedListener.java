package schedulingengine.constraints.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.constraints.ConstraintsFailed;
import schedulingengine.constraints.ConstraintsValidated;
import schedulingengine.constraints.domain.ValidationResult;
import schedulingengine.scheduling.ScheduleRequested;

/**
 * Listens for {@link ScheduleRequested}, validates constraints, and publishes
 * {@link ConstraintsValidated} or {@link ConstraintsFailed}.
 */
@Component
class ScheduleRequestedListener {

    private final ScheduleConstraintsValidator validator;
    private final ApplicationEventPublisher eventPublisher;

    ScheduleRequestedListener(ScheduleConstraintsValidator validator,
                              ApplicationEventPublisher eventPublisher) {
        this.validator = validator;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    void onScheduleRequested(ScheduleRequested event) {
        ValidationResult result = validator.validate(
            event.startTime(),
            event.endTime(),
            event.title()
        );

        if (result.valid()) {
            eventPublisher.publishEvent(new ConstraintsValidated(
                event.scheduleId(),
                java.time.Instant.now()
            ));
        } else {
            eventPublisher.publishEvent(new ConstraintsFailed(
                event.scheduleId(),
                result.reasons(),
                java.time.Instant.now()
            ));
        }
    }
}
