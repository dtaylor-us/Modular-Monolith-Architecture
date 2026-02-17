package schedulingengine.scheduling.application;

/**
 * Port for schedule creation. Used by the web adapter.
 */
public interface ScheduleCommandService {

    ScheduleResult createSchedule(CreateScheduleCommand command);
}
