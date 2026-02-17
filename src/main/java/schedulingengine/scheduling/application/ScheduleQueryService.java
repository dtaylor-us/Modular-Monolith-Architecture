package schedulingengine.scheduling.application;

import java.util.Optional;
import java.util.UUID;

import schedulingengine.scheduling.domain.Schedule;

/**
 * Port for querying schedules. Used by the web adapter.
 */
public interface ScheduleQueryService {

    Optional<Schedule> findById(UUID id);
}
