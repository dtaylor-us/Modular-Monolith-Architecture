package schedulingengine.scheduling.application;

import java.time.Instant;

public record CreateScheduleCommand(
    Instant startTime,
    Instant endTime,
    String title
) {}
