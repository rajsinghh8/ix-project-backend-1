package com.eventforge.schedule;

import com.eventforge.event.Event;
import com.eventforge.event.EventRepository;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ScheduleService {
    private final ScheduleItemRepository items; private final EventRepository events;
    public ScheduleService(ScheduleItemRepository items, EventRepository events) { this.items = items; this.events = events; }
    @Transactional(readOnly = true) public Page<ScheduleItemDto.Response> list(UUID eventId, UUID ownerId, Pageable pageable) { return items.findAllOwned(eventId, ownerId, pageable).map(ScheduleItemDto.Response::from); }
    public ScheduleItemDto.Response create(UUID eventId, UUID ownerId, ScheduleItemDto.Request request) {
        Event event = event(eventId, ownerId); ensureNoOverlap(eventId, null, request); return ScheduleItemDto.Response.from(items.save(new ScheduleItem(event, request.title(), request.description(), request.startTime(), request.endTime(), request.location())));
    }
    public ScheduleItemDto.Response update(UUID eventId, UUID id, UUID ownerId, ScheduleItemDto.Request request) {
        ScheduleItem item = item(eventId, id, ownerId); ensureNoOverlap(eventId, id, request); item.update(request.title(), request.description(), request.startTime(), request.endTime(), request.location()); return ScheduleItemDto.Response.from(item);
    }
    public void delete(UUID eventId, UUID id, UUID ownerId) { items.delete(item(eventId, id, ownerId)); }
    private void ensureNoOverlap(UUID eventId, UUID id, ScheduleItemDto.Request request) { boolean overlaps = id == null ? items.overlapsNew(eventId, request.startTime(), request.endTime()) : items.overlaps(eventId, id, request.startTime(), request.endTime()); if (overlaps) throw new ResponseStatusException(HttpStatus.CONFLICT, "Schedule item overlaps an existing item"); }
    private Event event(UUID id, UUID owner) { return events.findById(id).filter(e -> e.getOwner().getId().equals(owner)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found")); }
    private ScheduleItem item(UUID eventId, UUID id, UUID owner) { return items.findById(id).filter(s -> s.getEvent().getId().equals(eventId) && s.getEvent().getOwner().getId().equals(owner)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule item not found")); }
}
