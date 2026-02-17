package schedulingengine.scheduling.application;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schedulingengine.scheduling.ScheduleRequested;
import schedulingengine.scheduling.domain.Schedule;

@Service
class ScheduleApplicationService implements ScheduleCommandService, ScheduleQueryService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    ScheduleApplicationService(ScheduleRepository scheduleRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Schedule> findById(UUID id) {
        return Optional.ofNullable(scheduleRepository.findById(id));
    }

    @Override
    @Transactional
    public ScheduleResult createSchedule(CreateScheduleCommand command) {
        UUID id = UUID.randomUUID();
        Schedule schedule = Schedule.create(
            id,
            command.startTime(),
            command.endTime(),
            command.title()
        );
        scheduleRepository.save(schedule);
        eventPublisher.publishEvent(new ScheduleRequested(
            schedule.id(),
            schedule.startTime(),
            schedule.endTime(),
            schedule.title(),
            schedule.createdAt()
        ));
        return new ScheduleResult(id);
    }
}
