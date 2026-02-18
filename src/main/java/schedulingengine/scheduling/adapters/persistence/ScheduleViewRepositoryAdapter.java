package schedulingengine.scheduling.adapters.persistence;

import org.springframework.stereotype.Component;

import schedulingengine.scheduling.application.ScheduleViewRepository;
import schedulingengine.scheduling.domain.ScheduleView;

@Component
class ScheduleViewRepositoryAdapter implements ScheduleViewRepository {

    private final ScheduleViewJpaRepository jpaRepository;

    ScheduleViewRepositoryAdapter(ScheduleViewJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(ScheduleView view) {
        jpaRepository.findById(view.requestId()).ifPresentOrElse(
            e -> {
                e.setStatus(view.status());
                e.setOptimizedStart(view.optimizedStart());
                e.setOptimizedEnd(view.optimizedEnd());
                e.setUpdatedAt(view.updatedAt());
                jpaRepository.save(e);
            },
            () -> jpaRepository.save(new ScheduleViewEntity(
                view.requestId(),
                view.status(),
                view.optimizedStart(),
                view.optimizedEnd(),
                view.updatedAt()
            ))
        );
    }

    @Override
    public ScheduleView findByRequestId(java.util.UUID requestId) {
        return jpaRepository.findById(requestId)
            .map(e -> new ScheduleView(
                e.getRequestId(),
                e.getStatus(),
                e.getOptimizedStart(),
                e.getOptimizedEnd(),
                e.getUpdatedAt()
            ))
            .orElse(null);
    }
}
