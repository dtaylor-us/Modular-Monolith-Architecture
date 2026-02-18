package schedulingengine.scheduling.application;

import java.time.Instant;

public record CreateScheduleCommand(
    String title,
    Instant earliestStart,
    Instant latestEnd,
    int durationMinutes,
    Instant preferredStart,
    String priority
) {}
