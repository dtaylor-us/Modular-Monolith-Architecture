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

import java.time.ZoneOffset;
import java.util.UUID;

import schedulingengine.scheduling.application.CreateScheduleCommand;
import schedulingengine.scheduling.application.ScheduleCommandService;
import schedulingengine.scheduling.application.ScheduleQueryService;
import schedulingengine.scheduling.application.ScheduleResult;
import schedulingengine.scheduling.domain.Schedule;

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
        var startInstant = request.start().toInstant(ZoneOffset.UTC);
        var endInstant = request.end().toInstant(ZoneOffset.UTC);
        ScheduleResult result = commandService.createSchedule(new CreateScheduleCommand(
            startInstant,
            endInstant,
            request.title()
        ));
        return queryService.findById(result.scheduleId())
            .map(ScheduleController::toResponse)
            .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body))
            .orElse(ResponseEntity.status(HttpStatus.CREATED).body(new ScheduleResponse(
                result.scheduleId(), startInstant, endInstant, request.title(), null
            )));
    }

    @GetMapping("/{id}")
    ResponseEntity<ScheduleResponse> getById(@PathVariable UUID id) {
        return queryService.findById(id)
            .map(ScheduleController::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private static ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
            schedule.id(),
            schedule.startTime(),
            schedule.endTime(),
            schedule.title(),
            schedule.createdAt()
        );
    }
}
