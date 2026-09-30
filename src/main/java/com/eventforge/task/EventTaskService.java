package com.eventforge.task;

import com.eventforge.event.Event;
import com.eventforge.event.EventRepository;
import com.eventforge.domain.User;
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;
    public EventTaskService(EventRepository events, EventTaskRepository tasks, EntityManager entityManager) {
        this.events = events; this.tasks = tasks; this.entityManager = entityManager;
    }
    public Page<EventTaskDtos.TaskResponse> list(UUID eventId, Pageable pageable) {
        requireEvent(eventId);
        return tasks.findByEvent_IdAndEvent_Owner_Id(eventId, currentUserId(), pageable).map(this::response);
    }
    @Transactional
    public EventTaskDtos.TaskResponse create(UUID eventId, EventTaskDtos.TaskRequest request) {
        Event event = requireEvent(eventId);
        Set<EventTask> dependencies = dependencies(eventId, request.dependencyIds(), null);
        validateCompletion(request.status(), dependencies);
        User assignee = assignee(event, request.assigneeId());
        return response(tasks.save(new EventTask(event, assignee, request.title(), request.description(), request.priority(),
                request.deadline(), request.status(), dependencies)));
    }
    @Transactional
    public EventTaskDtos.TaskResponse update(UUID eventId, UUID taskId, EventTaskDtos.TaskRequest request) {
        EventTask task = ownedTask(eventId, taskId);
        Set<EventTask> dependencies = dependencies(eventId, request.dependencyIds(), taskId);
        validateCompletion(request.status(), dependencies);
        task.update(assignee(task.getEvent(), request.assigneeId()), request.title(), request.description(),
                request.priority(), request.deadline(), request.status(), dependencies);
        return response(task);
    }
    @Transactional
    public void delete(UUID eventId, UUID taskId) { tasks.delete(ownedTask(eventId, taskId)); }
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
    private EventTaskDtos.TaskResponse response(EventTask task) {
        Set<UUID> dependencyIds = task.getDependencies().stream().map(EventTask::getId)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
        UUID assigneeId = task.getAssignee() == null ? null : task.getAssignee().getId();
        return new EventTaskDtos.TaskResponse(task.getId(), task.getEvent().getId(), assigneeId, task.getTitle(),
                task.getDescription(), task.getPriority(), task.getDeadline(), task.getStatus(), dependencyIds);
    }
    private UUID currentUserId() {
        try { return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName()); }
        catch (Exception ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated principal"); }
    }
}
