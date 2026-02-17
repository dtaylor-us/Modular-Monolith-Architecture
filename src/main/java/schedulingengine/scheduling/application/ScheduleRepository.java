package schedulingengine.scheduling.application;

import schedulingengine.scheduling.domain.Schedule;

/**
 * Port for persisting schedules. Implemented by persistence adapter.
 */
public interface ScheduleRepository {

    Schedule save(Schedule schedule);

    Schedule findById(java.util.UUID id);
}
