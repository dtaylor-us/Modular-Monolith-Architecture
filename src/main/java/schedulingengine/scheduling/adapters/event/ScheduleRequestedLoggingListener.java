package schedulingengine.scheduling.adapters.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.scheduling.ScheduleRequested;

/**
 * Logs {@link ScheduleRequested} events so they are visible in application logs.
 */
@Component
class ScheduleRequestedLoggingListener {

    private static final Logger log = LoggerFactory.getLogger(ScheduleRequestedLoggingListener.class);

    @EventListener
    void onScheduleRequested(ScheduleRequested event) {
        log.info("ScheduleRequested: requestId={}, title='{}', earliestStart={}, latestEnd={}, durationMinutes={}",
            event.requestId(),
            event.title(),
            event.earliestStart(),
            event.latestEnd(),
            event.durationMinutes());
    }
}
