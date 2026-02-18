package schedulingengine.optimization.adapters.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.optimization.ScheduleOptimized;

/**
 * Logs {@link ScheduleOptimized} events so they are visible in application logs.
 */
@Component
class ScheduleOptimizedLoggingListener {

    private static final Logger log = LoggerFactory.getLogger(ScheduleOptimizedLoggingListener.class);

    @EventListener
    void onScheduleOptimized(ScheduleOptimized event) {
        log.info("ScheduleOptimized: requestId={}, optimizedStart={}, optimizedEnd={}",
            event.requestId(),
            event.optimizedStart(),
            event.optimizedEnd());
    }
}

