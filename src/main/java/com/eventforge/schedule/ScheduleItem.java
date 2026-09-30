package com.eventforge.schedule;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "schedule_items", indexes = @Index(name = "idx_schedule_event_time", columnList = "event_id,start_time,end_time"))
public class ScheduleItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @Column(nullable = false, length = 160) private String title;
    @Column(length = 2000) private String description;
    @Column(name = "start_time", nullable = false) private LocalDateTime startTime;
    @Column(name = "end_time", nullable = false) private LocalDateTime endTime;
    @Column(length = 300) private String location;
    protected ScheduleItem() { }
    public ScheduleItem(Event event, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location) {
        this.event = event; this.title = title; this.description = description; this.startTime = startTime; this.endTime = endTime; this.location = location;
    }
    public void update(String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location) {
        this.title = title; this.description = description; this.startTime = startTime; this.endTime = endTime; this.location = location;
    }
    public UUID getId() { return id; } public Event getEvent() { return event; } public String getTitle() { return title; }
    public String getDescription() { return description; } public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; } public String getLocation() { return location; }
}
