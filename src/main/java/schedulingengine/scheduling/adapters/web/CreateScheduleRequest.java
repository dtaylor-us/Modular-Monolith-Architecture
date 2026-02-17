package schedulingengine.scheduling.adapters.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateScheduleRequest(
    @NotNull Instant start,
    @NotNull Instant end,
    @NotBlank String title
) {}
