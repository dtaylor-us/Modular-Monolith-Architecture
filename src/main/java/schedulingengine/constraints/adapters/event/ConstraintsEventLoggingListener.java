package schedulingengine.constraints.adapters.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.constraints.ConstraintsFailed;
import schedulingengine.constraints.ConstraintsValidated;

/**
 * Logs constraints events so they are visible in application logs.
 */
@Component
class ConstraintsEventLoggingListener {

    private static final Logger log = LoggerFactory.getLogger(ConstraintsEventLoggingListener.class);

    @EventListener
    void onConstraintsValidated(ConstraintsValidated event) {
        log.info("ConstraintsValidated: requestId={}, earliestStart={}, latestEnd={}", 
            event.requestId(), event.earliestStart(), event.latestEnd());
    }

    @EventListener
    void onConstraintsFailed(ConstraintsFailed event) {
        log.warn("ConstraintsFailed: requestId={}, reasons={}", event.requestId(), event.reasons());
    }
}
