package schedulingengine.optimization.application;

import schedulingengine.optimization.domain.OptimizedSchedule;

/**
 * Port for persisting optimized schedules. Implemented by persistence adapter.
 */
public interface OptimizationRepository {

    OptimizedSchedule save(OptimizedSchedule schedule);
}

