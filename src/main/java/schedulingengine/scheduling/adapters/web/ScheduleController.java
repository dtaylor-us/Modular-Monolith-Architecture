package schedulingengine.scheduling.adapters.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import schedulingengine.scheduling.application.CreateScheduleCommand;
import schedulingengine.scheduling.application.ScheduleCommandService;
import schedulingengine.scheduling.application.ScheduleQueryService;
import schedulingengine.scheduling.application.ScheduleResult;
import schedulingengine.scheduling.domain.ScheduleRequest;
import schedulingengine.scheduling.domain.ScheduleView;

@RestController
@RequestMapping("/api/schedules")
class ScheduleController {

    private final ScheduleCommandService commandService;
    private final ScheduleQueryService queryService;

    ScheduleController(ScheduleCommandService commandService, ScheduleQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    ResponseEntity<ScheduleResponse> create(@Valid @RequestBody CreateScheduleRequest request) {
        var earliestStart = request.earliestStart().toInstant(ZoneOffset.UTC);
        var latestEnd = request.latestEnd().toInstant(ZoneOffset.UTC);
        var preferredStart = request.preferredStart() != null
            ? request.preferredStart().toInstant(ZoneOffset.UTC)
            : null;
        ScheduleResult result = commandService.createSchedule(new CreateScheduleCommand(
            request.title(),
            earliestStart,
            latestEnd,
            request.durationMinutes(),
            preferredStart,
            request.priority()
        ));
        return queryService.findById(result.requestId())
            .map(r -> toResponse(r, queryService.findViewByRequestId(r.id())))
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body))
            .orElse(ResponseEntity.status(HttpStatus.CREATED).body(new ScheduleResponse(
                result.requestId(),
                request.title(),
                result.status(),
                earliestStart,
                latestEnd,
                request.durationMinutes(),
                preferredStart,
                request.priority(),
                null,
                null,
                null
            )));
    }

    @GetMapping("/{id}")
    ResponseEntity<ScheduleResponse> getById(@PathVariable UUID id) {
        return queryService.findById(id)
            .map(r -> toResponse(r, queryService.findViewByRequestId(r.id())))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private static ScheduleResponse toResponse(ScheduleRequest request, Optional<ScheduleView> view) {
        String status = view.map(ScheduleView::status).orElse(request.status());
        Instant optStart = view.flatMap(v -> Optional.ofNullable(v.optimizedStart())).orElse(null);
        Instant optEnd = view.flatMap(v -> Optional.ofNullable(v.optimizedEnd())).orElse(null);
        return new ScheduleResponse(
            request.id(),
            request.title(),
            status,
            request.earliestStart(),
            request.latestEnd(),
            request.durationMinutes(),
            request.preferredStart(),
            request.priority(),
            request.createdAt(),
            optStart,
            optEnd
        );
    }
}
