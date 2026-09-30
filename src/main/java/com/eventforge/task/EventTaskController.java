package com.eventforge.task;

import jakarta.validation.Valid;
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
    @GetMapping
    public Page<EventTaskDtos.TaskResponse> list(@PathVariable UUID eventId, Pageable pageable) {
        return service.list(eventId, pageable);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventTaskDtos.TaskResponse create(@PathVariable UUID eventId,
                                              @Valid @RequestBody EventTaskDtos.TaskRequest request) {
        return service.create(eventId, request);
    }
    @PutMapping("/{taskId}")
    public EventTaskDtos.TaskResponse update(@PathVariable UUID eventId, @PathVariable UUID taskId,
                                              @Valid @RequestBody EventTaskDtos.TaskRequest request) {
        return service.update(eventId, taskId, request);
    }
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID eventId, @PathVariable UUID taskId) { service.delete(eventId, taskId); }
}
