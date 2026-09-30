package com.eventforge.event;

import com.eventforge.activity.EventActivityRepository;
import com.eventforge.activity.EventActivityService;
import com.eventforge.budget.BudgetCategoryRepository;
import com.eventforge.budget.ExpenseRepository;
import com.eventforge.invitation.GuestInvitationRepository;
import com.eventforge.notification.NotificationRepository;
import com.eventforge.schedule.ScheduleItemRepository;
import com.eventforge.task.EventTaskRepository;
import com.eventforge.vendor.VendorPaymentRepository;
import com.eventforge.vendor.VendorRepository;
import com.eventforge.domain.User;
import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final BudgetCategoryRepository categories;
    private final ExpenseRepository expenses;
    private final EventActivityService activities;
    private final NotificationService notifications;
    private final EventTaskRepository tasks;
    private final GuestInvitationRepository invitations;
    private final ScheduleItemRepository scheduleItems;
    private final VendorRepository vendors;
    private final VendorPaymentRepository vendorPayments;
    private final NotificationRepository notificationRepository;
    private final EventActivityRepository activityRepository;
    private final EntityManager entityManager;

    public EventService(EventRepository events, BudgetCategoryRepository categories, ExpenseRepository expenses,
                        EventActivityService activities, NotificationService notifications, EventTaskRepository tasks,
                        GuestInvitationRepository invitations, ScheduleItemRepository scheduleItems, VendorRepository vendors,
                        VendorPaymentRepository vendorPayments, NotificationRepository notificationRepository,
                        EventActivityRepository activityRepository, EntityManager entityManager) {
        this.events = events;
        this.categories = categories;
        this.expenses = expenses;
        this.activities = activities;
        this.notifications = notifications;
        this.tasks = tasks;
        this.invitations = invitations;
        this.scheduleItems = scheduleItems;
        this.vendors = vendors;
        this.vendorPayments = vendorPayments;
        this.notificationRepository = notificationRepository;
        this.activityRepository = activityRepository;
        this.entityManager = entityManager;
    }

    public Page<EventDtos.EventResponse> list(String search, EventStatus status, LocalDate dateFrom, LocalDate dateTo,
                                               Pageable pageable) {
        return events.searchOwned(currentUserId(), normalized(search), status, dateFrom, dateTo, pageable)
                .map(this::response);
    }

    public Page<EventDtos.EventResponse> list(Pageable pageable) {
        return list(null, null, null, null, pageable);
    }

    public EventDtos.EventResponse get(UUID id) { return response(owned(id)); }

    @Transactional
    public EventDtos.EventResponse create(EventDtos.EventRequest request) {
        User owner = entityManager.getReference(User.class, currentUserId());
        Event event = events.save(new Event(owner, request.name(), request.eventDate(), request.venue(),
                request.capacity(), request.budgetLimit(), request.status(), request.description()));
        activities.recordActivity(event, owner, "EVENT_CREATED", "Created event: " + event.getName());
        notifications.recordForEventOwner(event, NotificationType.BUDGET, "Event created: " + event.getName());
        return response(event);
    }

    @Transactional
    public EventDtos.EventResponse update(UUID id, EventDtos.EventRequest request) {
        Event event = owned(id);
        if (request.capacity() < event.getConfirmedGuestCount()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Capacity cannot be below confirmed guest count");
        }
        event.update(request.name(), request.eventDate(), request.venue(), request.capacity(), request.budgetLimit(),
                request.status(), request.description());
        activities.recordActivity(event, event.getOwner(), "EVENT_UPDATED", "Updated event: " + event.getName());
        notifications.recordForEventOwner(event, NotificationType.BUDGET, "Event updated: " + event.getName());
        return response(event);
    }

    @Transactional
    public void delete(UUID id) {
        Event event = owned(id); // Preserve the ownership check before any destructive operation.
        UUID eventId = event.getId();
        // Child rows are explicitly removed in FK dependency order; do not rely on provider cascades.
        notificationRepository.deleteByEventId(eventId);
        activityRepository.deleteByEventId(eventId);
        tasks.deleteDependenciesByEventId(eventId);
        tasks.deleteByEventId(eventId);
        invitations.deleteByEventId(eventId);
        expenses.deleteByEventId(eventId);
        categories.deleteByEventId(eventId);
        scheduleItems.deleteByEventId(eventId);
        vendorPayments.deleteByEventId(eventId);
        vendors.deleteByEventId(eventId);
        events.delete(event);
    }

    private Event owned(UUID id) {
        return events.findByIdAndOwner_Id(id, currentUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private EventDtos.EventResponse response(Event event) {
        BigDecimal planned = valueOrZero(categories.totalPlannedForEvent(event.getId()));
        BigDecimal actual = valueOrZero(expenses.totalForEvent(event.getId()));
        BigDecimal budgetLimit = valueOrZero(event.getBudgetLimit());
        EventDtos.BudgetSummary budget = new EventDtos.BudgetSummary(budgetLimit, planned, actual,
                budgetLimit.subtract(actual));
        return new EventDtos.EventResponse(event.getId(), event.getName(), event.getEventDate(), event.getVenue(),
                event.getCapacity(), event.getConfirmedGuestCount(), event.getBudgetLimit(), event.getStatus(),
                event.getDescription(), budget);
    }

    private String normalized(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private BigDecimal valueOrZero(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private UUID currentUserId() {
        try { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated principal"); }
    }
}
