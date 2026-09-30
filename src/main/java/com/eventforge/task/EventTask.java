package com.eventforge.task;

import com.eventforge.event.Event;
import com.eventforge.domain.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "event_tasks")
public class EventTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
    @Column(nullable = false, length = 250)
    private String title;
    @Column(length = 4000)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;
    private Instant deadline;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;
    @ManyToMany
    @JoinTable(name = "event_task_dependencies", joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id"))
    private Set<EventTask> dependencies = new HashSet<>();

    protected EventTask() { }
    public EventTask(Event event, User assignee, String title, String description, TaskPriority priority,
                     Instant deadline, TaskStatus status, Set<EventTask> dependencies) {
        this.event = event; this.assignee = assignee; this.title = title; this.description = description;
        this.priority = priority; this.deadline = deadline;
        this.status = status == null ? TaskStatus.TODO : status;
        if (dependencies != null) this.dependencies.addAll(dependencies);
    }
    public UUID getId() { return id; }
    public Event getEvent() { return event; }
    public User getAssignee() { return assignee; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskPriority getPriority() { return priority; }
    public Instant getDeadline() { return deadline; }
    public TaskStatus getStatus() { return status; }
    public Set<EventTask> getDependencies() { return dependencies; }
    public void update(User assignee, String title, String description, TaskPriority priority, Instant deadline,
                       TaskStatus status, Set<EventTask> dependencies) {
        this.assignee = assignee; this.title = title; this.description = description; this.priority = priority;
        this.deadline = deadline; this.status = status; this.dependencies.clear(); this.dependencies.addAll(dependencies);
    }
}
