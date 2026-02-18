package schedulingengine.scheduleview;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import schedulingengine.constraints.ConstraintsFailed;
import schedulingengine.optimization.OptimizationFailed;
import schedulingengine.optimization.ScheduleOptimized;
import schedulingengine.scheduling.ScheduleViewUpdater;

/**
 * Updates the schedule view when constraints/optimization events occur.
 */
@Component
class ScheduleViewEventListener {

    private final ScheduleViewUpdater viewUpdater;

    ScheduleViewEventListener(ScheduleViewUpdater viewUpdater) {
        this.viewUpdater = viewUpdater;
    }

    @EventListener
    void onConstraintsFailed(ConstraintsFailed event) {
        viewUpdater.recordConstraintsFailed(event.requestId());
    }

    @EventListener
    void onScheduleOptimized(ScheduleOptimized event) {
        viewUpdater.recordOptimized(event.requestId(), event.optimizedStart(), event.optimizedEnd());
    }

    @EventListener
    void onOptimizationFailed(OptimizationFailed event) {
        viewUpdater.recordOptimizationFailed(event.requestId());
    }
}
