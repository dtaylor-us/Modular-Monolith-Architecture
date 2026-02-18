package schedulingengine.optimization.adapters.persistence;

import org.springframework.stereotype.Component;

import schedulingengine.optimization.application.OptimizationRepository;
import schedulingengine.optimization.domain.OptimizedSchedule;

@Component
class OptimizationRepositoryAdapter implements OptimizationRepository {

    private final OptimizedScheduleJpaRepository jpaRepository;

    OptimizationRepositoryAdapter(OptimizedScheduleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OptimizedSchedule save(OptimizedSchedule schedule) {
        jpaRepository.save(new OptimizedScheduleEntity(
            schedule.id(),
            schedule.scheduleId(),
            "v1",
            schedule.strategyUsed(),
            schedule.optimizedStart(),
            schedule.optimizedEnd(),
            schedule.strategyUsed(),
            schedule.optimizedAt()
        ));
        return schedule;
    }
}

