package com.eventforge.activity;
import io.swagger.v3.oas.annotations.Operation;
import com.eventforge.common.CurrentUser; import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/events/{eventId}/activities") public class EventActivityController {private final EventActivityService service;private final CurrentUser currentUser;public EventActivityController(EventActivityService service,CurrentUser currentUser){this.service=service;this.currentUser=currentUser;}@Operation(summary="List the event audit trail") @GetMapping public Page<EventActivityDto> list(@PathVariable UUID eventId,Pageable pageable){return service.list(eventId,currentUser.id(),pageable);}}
