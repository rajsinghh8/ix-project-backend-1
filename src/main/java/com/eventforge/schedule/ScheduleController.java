package com.eventforge.schedule;

import com.eventforge.domain.User;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/events/{eventId}/schedule")
public class ScheduleController {
    private final ScheduleService service; public ScheduleController(ScheduleService service) { this.service = service; }
    @GetMapping public Page<ScheduleItemDto.Response> list(@PathVariable UUID eventId, @AuthenticationPrincipal User user, Pageable pageable) { return service.list(eventId, user.getId(), pageable); }
    @PostMapping public ResponseEntity<ScheduleItemDto.Response> create(@PathVariable UUID eventId, @AuthenticationPrincipal User user, @Valid @RequestBody ScheduleItemDto.Request request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId, user.getId(), request)); }
    @PutMapping("/{id}") public ScheduleItemDto.Response update(@PathVariable UUID eventId, @PathVariable UUID id, @AuthenticationPrincipal User user, @Valid @RequestBody ScheduleItemDto.Request request) { return service.update(eventId, id, user.getId(), request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID eventId, @PathVariable UUID id, @AuthenticationPrincipal User user) { service.delete(eventId, id, user.getId()); }
}
