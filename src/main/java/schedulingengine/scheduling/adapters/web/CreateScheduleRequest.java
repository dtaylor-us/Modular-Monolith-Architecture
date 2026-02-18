package schedulingengine.scheduling.adapters.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request body for creating a schedule request. Times are ISO-8601 local date-time (e.g. "2026-02-18T09:00:00")
 * interpreted as UTC when no offset is provided.
 */
public record CreateScheduleRequest(
    String title,
    @NotNull LocalDateTime earliestStart,
    @NotNull LocalDateTime latestEnd,
    @NotNull @Min(1) @Max(24 * 60) int durationMinutes,
    LocalDateTime preferredStart,
    String priority
) {}
