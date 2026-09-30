package com.eventforge.schedule;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.time.Instant;
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
    @Column(name = "start_time", nullable = false) private Instant startTime;
    @Column(name = "end_time", nullable = false) private Instant endTime;
    @Column(length = 300) private String location;
    protected ScheduleItem() { }
    public ScheduleItem(Event event, String title, String description, Instant startTime, Instant endTime, String location) {
        this.event = event; this.title = title; this.description = description; this.startTime = startTime; this.endTime = endTime; this.location = location;
    }
    public void update(String title, String description, Instant startTime, Instant endTime, String location) {
        this.title = title; this.description = description; this.startTime = startTime; this.endTime = endTime; this.location = location;
    }
    public UUID getId() { return id; } public Event getEvent() { return event; } public String getTitle() { return title; }
    public String getDescription() { return description; } public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; } public String getLocation() { return location; }
}
