package schedulingengine.scheduling.adapters.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request body for creating a schedule. Start and end are accepted as ISO-8601 local date-time
 * (e.g. "2025-02-20T09:00:00") and are interpreted as UTC when no offset is provided.
 */
public record CreateScheduleRequest(
    @NotNull LocalDateTime start,
    @NotNull LocalDateTime end,
    @NotBlank String title
) {}
