package com.eventforge.task;

import com.eventforge.activity.EventActivityService;
import com.eventforge.domain.User;
import com.eventforge.event.Event;
import com.eventforge.event.EventRepository;
import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
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
public class EventTaskService {
    private final EventRepository events;
    private final EventTaskRepository tasks;
    private final EventActivityService activities;
    private final NotificationService notifications;

    public EventTaskService(EventRepository events, EventTaskRepository tasks, EventActivityService activities,
                            NotificationService notifications) {
        this.events = events;
        this.tasks = tasks;
        this.activities = activities;
        this.notifications = notifications;
    }

    public Page<EventTaskDtos.TaskResponse> list(UUID eventId, String search, TaskStatus status,
                                                 LocalDate deadlineFrom, LocalDate deadlineTo, Pageable pageable) {
        requireEvent(eventId);
        Instant from = deadlineFrom == null ? null : deadlineFrom.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = deadlineTo == null ? null : deadlineTo.plusDays(1).atStartOfDay(ZoneOffset.UTC).minusNanos(1)
                .toInstant();
        return tasks.searchOwned(eventId, currentUserId(), normalized(search), status, from, to, pageable)
                .map(this::response);
    }

    public Page<EventTaskDtos.TaskResponse> list(UUID eventId, Pageable pageable) {
        return list(eventId, null, null, null, null, pageable);
    }

    @Transactional
    public EventTaskDtos.TaskResponse create(UUID eventId, EventTaskDtos.TaskRequest request) {
        Event event = requireEvent(eventId);
        Set<EventTask> dependencies = dependencies(eventId, request.dependencyIds(), null);
        validateCompletion(request.status(), dependencies);
        User assignee = assignee(event, request.assigneeId());
        EventTask task = tasks.save(new EventTask(event, assignee, request.title(), request.description(), request.priority(),
                request.deadline(), request.status(), dependencies));
        activityAndNotification(task, "TASK_CREATED", "Task created: ");
        return response(task);
    }

    @Transactional
    public EventTaskDtos.TaskResponse update(UUID eventId, UUID taskId, EventTaskDtos.TaskRequest request) {
        EventTask task = ownedTask(eventId, taskId);
        Set<EventTask> dependencies = dependencies(eventId, request.dependencyIds(), taskId);
        validateCompletion(request.status(), dependencies);
        task.update(assignee(task.getEvent(), request.assigneeId()), request.title(), request.description(),
                request.priority(), request.deadline(), request.status(), dependencies);
        activityAndNotification(task, "TASK_UPDATED", "Task updated: ");
        return response(task);
    }

    @Transactional
    public EventTaskDtos.TaskResponse complete(UUID eventId, UUID taskId) {
        EventTask task = ownedTask(eventId, taskId);
        Set<EventTask> dependencies = new LinkedHashSet<>(task.getDependencies());
        validateCompletion(TaskStatus.COMPLETED, dependencies);
        task.update(task.getAssignee(), task.getTitle(), task.getDescription(), task.getPriority(), task.getDeadline(),
                TaskStatus.COMPLETED, dependencies);
        activityAndNotification(task, "TASK_COMPLETED", "Task completed: ");
        return response(task);
    }

    @Transactional
    public void delete(UUID eventId, UUID taskId) {
        EventTask task = ownedTask(eventId, taskId);
        activityAndNotification(task, "TASK_DELETED", "Task deleted: ");
        tasks.delete(task);
    }

    private Event requireEvent(UUID eventId) {
        return events.findByIdAndOwner_Id(eventId, currentUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private EventTask ownedTask(UUID eventId, UUID taskId) {
        return tasks.findByIdAndEvent_IdAndEvent_Owner_Id(taskId, eventId, currentUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private Set<EventTask> dependencies(UUID eventId, Set<UUID> ids, UUID taskId) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        Set<EventTask> result = new LinkedHashSet<>();
        for (UUID id : ids) {
            if (id.equals(taskId)) throw new ResponseStatusException(HttpStatus.CONFLICT, "A task cannot depend on itself");
            result.add(ownedTask(eventId, id));
        }
        return result;
    }

    private User assignee(Event event, UUID assigneeId) {
        if (assigneeId == null) return null;
        if (!assigneeId.equals(currentUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee must be the event owner");
        }
        return event.getOwner();
    }

    private void validateCompletion(TaskStatus status, Set<EventTask> dependencies) {
        if (status == TaskStatus.COMPLETED && dependencies.stream().anyMatch(t -> t.getStatus() != TaskStatus.COMPLETED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Incomplete task dependencies prevent completion");
        }
    }

    private void activityAndNotification(EventTask task, String action, String messagePrefix) {
        Event event = task.getEvent();
        activities.recordActivity(event, event.getOwner(), action, messagePrefix + task.getTitle());
        notifications.recordForEventOwner(event, NotificationType.DEADLINE, messagePrefix + task.getTitle());
    }

    private EventTaskDtos.TaskResponse response(EventTask task) {
        Set<UUID> dependencyIds = task.getDependencies().stream().map(EventTask::getId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        UUID assigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();
        return new EventTaskDtos.TaskResponse(task.getId(), task.getEvent().getId(), assigneeId, task.getTitle(),
                task.getDescription(), task.getPriority(), task.getDeadline(), task.getStatus(), dependencyIds);
    }

    private String normalized(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private UUID currentUserId() {
        try { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated principal"); }
    }
}
