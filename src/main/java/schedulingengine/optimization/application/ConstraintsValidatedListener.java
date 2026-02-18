package schedulingengine.optimization.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import schedulingengine.constraints.ConstraintsValidated;
import schedulingengine.optimization.OptimizationFailed;
import schedulingengine.optimization.ScheduleOptimized;
import schedulingengine.optimization.domain.OptimizedSchedule;

/**
 * Listens for {@link ConstraintsValidated}, runs optimization, persists the result,
 * and publishes {@link ScheduleOptimized} or {@link OptimizationFailed}.
 */
@Component
class ConstraintsValidatedListener {

    private final ScheduleOptimizer optimizer;
    private final OptimizationRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    ConstraintsValidatedListener(ScheduleOptimizer optimizer,
                                 OptimizationRepository repository,
                                 ApplicationEventPublisher eventPublisher) {
        this.optimizer = optimizer;
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    @Transactional
    void onConstraintsValidated(ConstraintsValidated event) {
        var result = optimizer.optimize(
            event.requestId(),
            event.earliestStart(),
            event.latestEnd(),
            event.durationMinutes(),
            event.preferredStart()
        );
        if (result.isPresent()) {
            OptimizedSchedule optimized = result.get();
            repository.save(optimized);
            eventPublisher.publishEvent(new ScheduleOptimized(
                optimized.scheduleId(),
                event.title(),
                optimized.optimizedStart(),
                optimized.optimizedEnd(),
                optimized.optimizedAt()
            ));
        } else {
            eventPublisher.publishEvent(new OptimizationFailed(
                event.requestId(),
                "No valid slot within window",
                java.time.Instant.now()
            ));
        }
    }
}

