package schedulingengine.scheduling.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schedulingengine.scheduling.ScheduleViewUpdater;
import schedulingengine.scheduling.domain.ScheduleView;

import java.time.Instant;
import java.util.UUID;

/**
 * Updates the schedule view when constraints/optimization events occur.
 */
@Service
class ScheduleViewUpdaterService implements ScheduleViewUpdater {

    private final ScheduleViewRepository viewRepository;

    ScheduleViewUpdaterService(ScheduleViewRepository viewRepository) {
        this.viewRepository = viewRepository;
    }

    @Override
    @Transactional
    public void recordConstraintsFailed(UUID requestId) {
        viewRepository.save(new ScheduleView(
            requestId,
            ScheduleView.STATUS_CONSTRAINTS_FAILED,
            null,
            null,
            Instant.now()
        ));
    }

    @Override
    @Transactional
    public void recordOptimized(UUID requestId, Instant optimizedStart, Instant optimizedEnd) {
        viewRepository.save(new ScheduleView(
            requestId,
            ScheduleView.STATUS_OPTIMIZED,
            optimizedStart,
            optimizedEnd,
            Instant.now()
        ));
    }

    @Override
    @Transactional
    public void recordOptimizationFailed(UUID requestId) {
        viewRepository.save(new ScheduleView(
            requestId,
            ScheduleView.STATUS_OPTIMIZATION_FAILED,
            null,
            null,
            Instant.now()
        ));
    }
}
