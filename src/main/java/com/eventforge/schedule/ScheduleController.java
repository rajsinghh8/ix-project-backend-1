package com.eventforge.schedule;

import com.eventforge.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events/{eventId}/schedule-items")
public class ScheduleController {
    private final ScheduleService service;
    private final CurrentUser currentUser;

    public ScheduleController(ScheduleService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @Operation(summary = "List schedule items")
    @GetMapping
    public Page<ScheduleItemDto.Response> list(@PathVariable UUID eventId, Pageable pageable) {
        return service.list(eventId, currentUser.id(), pageable);
    }

    @Operation(summary = "Create a schedule item")
    @PostMapping
    public ResponseEntity<ScheduleItemDto.Response> create(
            @PathVariable UUID eventId, @Valid @RequestBody ScheduleItemDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId, currentUser.id(), request));
    }

    @Operation(summary = "Update a schedule item")
    @PutMapping("/{id}")
    public ScheduleItemDto.Response update(
            @PathVariable UUID eventId, @PathVariable UUID id, @Valid @RequestBody ScheduleItemDto.Request request) {
        return service.update(eventId, id, currentUser.id(), request);
    }

    @Operation(summary = "Delete a schedule item")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID eventId, @PathVariable UUID id) {
        service.delete(eventId, id, currentUser.id());
    }
}
