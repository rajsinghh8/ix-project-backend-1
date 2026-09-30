package com.eventforge.event;

import jakarta.validation.Valid;
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
    @GetMapping
    public Page<EventDtos.EventResponse> list(Pageable pageable) { return service.list(pageable); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtos.EventResponse create(@Valid @RequestBody EventDtos.EventRequest request) {
        return service.create(request);
    }
    @GetMapping("/{id}")
    public EventDtos.EventResponse get(@PathVariable UUID id) { return service.get(id); }
    @PutMapping("/{id}")
    public EventDtos.EventResponse update(@PathVariable UUID id, @Valid @RequestBody EventDtos.EventRequest request) {
        return service.update(id, request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
