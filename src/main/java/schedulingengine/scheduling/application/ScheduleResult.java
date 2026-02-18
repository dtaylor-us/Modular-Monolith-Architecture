package schedulingengine.scheduling.application;

import java.util.UUID;

public record ScheduleResult(UUID requestId, String status) {}
