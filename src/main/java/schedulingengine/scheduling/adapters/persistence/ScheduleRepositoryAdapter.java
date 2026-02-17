package schedulingengine.scheduling.adapters.persistence;

import org.springframework.stereotype.Component;

import schedulingengine.scheduling.application.ScheduleRepository;
import schedulingengine.scheduling.domain.Schedule;

@Component
class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final ScheduleJpaRepository jpaRepository;

    ScheduleRepositoryAdapter(ScheduleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Schedule save(Schedule schedule) {
        ScheduleEntity entity = new ScheduleEntity(
            schedule.id(),
            schedule.startTime(),
            schedule.endTime(),
            schedule.title(),
            schedule.createdAt()
        );
        jpaRepository.save(entity);
        return schedule;
    }

    @Override
    public Schedule findById(java.util.UUID id) {
        return jpaRepository.findById(id)
            .map(e -> new Schedule(
                e.getId(),
                e.getStartTime(),
                e.getEndTime(),
                e.getTitle(),
                e.getCreatedAt()
            ))
            .orElse(null);
    }
}
