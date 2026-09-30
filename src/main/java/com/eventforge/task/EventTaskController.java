package com.eventforge.task;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/tasks")
public class EventTaskController {
    private final EventTaskService service;
    public EventTaskController(EventTaskService service) { this.service = service; }
    @Operation(summary = "List event tasks")
    @GetMapping
    public Page<EventTaskDtos.TaskResponse> list(
            @PathVariable UUID eventId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) LocalDate deadlineFrom,
            @RequestParam(required = false) LocalDate deadlineTo,
            Pageable pageable) {
        return service.list(eventId, search, status, deadlineFrom, deadlineTo, pageable);
    }
    @Operation(summary = "Create an event task")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventTaskDtos.TaskResponse create(@PathVariable UUID eventId,
                                              @Valid @RequestBody EventTaskDtos.TaskRequest request) {
        return service.create(eventId, request);
    }
    @Operation(summary = "Update an event task")
    @PutMapping("/{taskId}")
    public EventTaskDtos.TaskResponse update(@PathVariable UUID eventId, @PathVariable UUID taskId,
                                              @Valid @RequestBody EventTaskDtos.TaskRequest request) {
        return service.update(eventId, taskId, request);
    }
    @Operation(summary = "Complete a task after its dependencies")
    @PatchMapping("/{taskId}/complete")
    public EventTaskDtos.TaskResponse complete(@PathVariable UUID eventId, @PathVariable UUID taskId) {
        return service.complete(eventId, taskId);
    }
    @Operation(summary = "Delete an event task")
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID eventId, @PathVariable UUID taskId) { service.delete(eventId, taskId); }
}
