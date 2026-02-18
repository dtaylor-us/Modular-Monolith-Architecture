package schedulingengine.scheduling.adapters.persistence;

import org.springframework.stereotype.Component;

import schedulingengine.scheduling.application.ScheduleRequestRepository;
import schedulingengine.scheduling.domain.ScheduleRequest;

@Component
class ScheduleRequestRepositoryAdapter implements ScheduleRequestRepository {

    private final ScheduleRequestJpaRepository jpaRepository;

    ScheduleRequestRepositoryAdapter(ScheduleRequestJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScheduleRequest save(ScheduleRequest request) {
        jpaRepository.save(new ScheduleRequestEntity(
            request.id(),
            request.title(),
            request.earliestStart(),
            request.latestEnd(),
            request.durationMinutes(),
            request.preferredStart(),
            request.priority(),
            request.createdAt(),
            request.status()
        ));
        return request;
    }

    @Override
    public ScheduleRequest findById(java.util.UUID id) {
        return jpaRepository.findById(id)
            .map(e -> new ScheduleRequest(
                e.getId(),
                e.getTitle(),
                e.getEarliestStart(),
                e.getLatestEnd(),
                e.getDurationMinutes(),
                e.getPreferredStart(),
                e.getPriority(),
                e.getCreatedAt(),
                e.getStatus()
            ))
            .orElse(null);
    }
}
