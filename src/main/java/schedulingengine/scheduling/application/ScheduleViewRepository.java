package schedulingengine.scheduling.application;

import schedulingengine.scheduling.domain.ScheduleView;

/**
 * Port for persisting and loading schedule views.
 */
public interface ScheduleViewRepository {

    void save(ScheduleView view);

    ScheduleView findByRequestId(java.util.UUID requestId);
}
