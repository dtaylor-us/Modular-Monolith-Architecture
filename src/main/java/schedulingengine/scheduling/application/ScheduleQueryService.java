package schedulingengine.scheduling.application;

import java.util.Optional;
import java.util.UUID;

import schedulingengine.scheduling.domain.ScheduleRequest;
import schedulingengine.scheduling.domain.ScheduleView;

/**
 * Port for querying schedule requests and views. Used by the web adapter.
 */
public interface ScheduleQueryService {

    Optional<ScheduleRequest> findById(UUID id);

    Optional<ScheduleView> findViewByRequestId(UUID requestId);
}
