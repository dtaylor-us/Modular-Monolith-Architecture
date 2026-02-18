package schedulingengine.scheduling.application;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import schedulingengine.scheduling.ScheduleRequested;
import schedulingengine.scheduling.domain.ScheduleRequest;
import schedulingengine.scheduling.domain.ScheduleView;

@Service
class ScheduleApplicationService implements ScheduleCommandService, ScheduleQueryService {

    private final ScheduleRequestRepository scheduleRequestRepository;
    private final ScheduleViewRepository scheduleViewRepository;
    private final ApplicationEventPublisher eventPublisher;

    ScheduleApplicationService(ScheduleRequestRepository scheduleRequestRepository,
                               ScheduleViewRepository scheduleViewRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.scheduleRequestRepository = scheduleRequestRepository;
        this.scheduleViewRepository = scheduleViewRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<ScheduleRequest> findById(UUID id) {
        return Optional.ofNullable(scheduleRequestRepository.findById(id));
    }

    @Override
    public Optional<ScheduleView> findViewByRequestId(UUID requestId) {
        return Optional.ofNullable(scheduleViewRepository.findByRequestId(requestId));
    }

    @Override
    @Transactional
    public ScheduleResult createSchedule(CreateScheduleCommand command) {
        UUID id = UUID.randomUUID();
        ScheduleRequest request = ScheduleRequest.create(
            id,
            command.title(),
            command.earliestStart(),
            command.latestEnd(),
            command.durationMinutes(),
            command.preferredStart(),
            command.priority()
        );
        scheduleRequestRepository.save(request);
        scheduleViewRepository.save(new ScheduleView(
            id,
            ScheduleView.STATUS_REQUESTED,
            null,
            null,
            request.createdAt()
        ));
        eventPublisher.publishEvent(new ScheduleRequested(
            request.id(),
            request.title(),
            request.earliestStart(),
            request.latestEnd(),
            request.durationMinutes(),
            request.preferredStart(),
            request.priority(),
            request.createdAt()
        ));
        return new ScheduleResult(id, ScheduleRequest.STATUS_REQUESTED);
    }
}
