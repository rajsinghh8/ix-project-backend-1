package com.eventforge.event;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    public EventController(EventService service) { this.service = service; }
    @Operation(summary = "List the caller's events")
    @GetMapping
    public Page<EventDtos.EventResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            Pageable pageable) {
        return service.list(search, status, dateFrom, dateTo, pageable);
    }
    @Operation(summary = "Create an event")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtos.EventResponse create(@Valid @RequestBody EventDtos.EventRequest request) {
        return service.create(request);
    }
    @Operation(summary = "Get an owned event")
    @GetMapping("/{id}")
    public EventDtos.EventResponse get(@PathVariable UUID id) { return service.get(id); }
    @Operation(summary = "Update an owned event")
    @PutMapping("/{id}")
    public EventDtos.EventResponse update(@PathVariable UUID id, @Valid @RequestBody EventDtos.EventRequest request) {
        return service.update(id, request);
    }
    @Operation(summary = "Delete an owned event")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
