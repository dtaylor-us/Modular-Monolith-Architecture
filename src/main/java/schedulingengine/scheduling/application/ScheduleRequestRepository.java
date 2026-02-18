package schedulingengine.scheduling.application;

import schedulingengine.scheduling.domain.ScheduleRequest;

/**
 * Port for persisting schedule requests.
 */
public interface ScheduleRequestRepository {

    ScheduleRequest save(ScheduleRequest request);

    ScheduleRequest findById(java.util.UUID id);
}
