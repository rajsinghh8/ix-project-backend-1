package com.eventforge.event;

import com.eventforge.domain.User;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class EventService {
    private final EventRepository events;
    private final EntityManager entityManager;
    public EventService(EventRepository events, EntityManager entityManager) {
        this.events = events;
        this.entityManager = entityManager;
    }
    public Page<EventDtos.EventResponse> list(Pageable pageable) {
        return events.findByOwner_Id(currentUserId(), pageable).map(this::response);
    }
    public EventDtos.EventResponse get(UUID id) { return response(owned(id)); }
    @Transactional
    public EventDtos.EventResponse create(EventDtos.EventRequest request) {
        User owner = entityManager.getReference(User.class, currentUserId());
        return response(events.save(new Event(owner, request.name(), request.eventDate(), request.venue(),
                request.capacity(), request.budgetLimit(), request.status(), request.description())));
    }
    @Transactional
    public EventDtos.EventResponse update(UUID id, EventDtos.EventRequest request) {
        Event event = owned(id);
        if (request.capacity() < event.getConfirmedGuestCount()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Capacity cannot be below confirmed guest count");
        }
        event.update(request.name(), request.eventDate(), request.venue(), request.capacity(), request.budgetLimit(),
                request.status(), request.description());
        return response(event);
    }
    @Transactional
    public void delete(UUID id) { events.delete(owned(id)); }
    private Event owned(UUID id) {
        return events.findByIdAndOwner_Id(id, currentUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }
    private EventDtos.EventResponse response(Event event) {
        BigDecimal zero = BigDecimal.ZERO;
        EventDtos.BudgetSummary budget = new EventDtos.BudgetSummary(event.getBudgetLimit(), zero, zero,
                event.getBudgetLimit());
        return new EventDtos.EventResponse(event.getId(), event.getName(), event.getEventDate(), event.getVenue(),
                event.getCapacity(), event.getConfirmedGuestCount(), event.getBudgetLimit(), event.getStatus(),
                event.getDescription(), budget);
    }
    private UUID currentUserId() {
        try { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated principal"); }
    }
}
