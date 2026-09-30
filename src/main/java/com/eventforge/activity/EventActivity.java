package com.eventforge.activity;
import com.eventforge.event.Event; import com.eventforge.domain.User; import jakarta.persistence.*; import java.time.*; import java.util.UUID;
@Entity @Table(name="event_activities") public class EventActivity {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="event_id",nullable=false,updatable=false) private Event event; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="actor_id",updatable=false) private User actor;
 @Column(nullable=false,length=120,updatable=false) private String action; @Column(length=2000,updatable=false) private String details; @Column(nullable=false,updatable=false) private Instant createdAt;
 protected EventActivity(){} public EventActivity(Event event,User actor,String action,String details){this.event=event;this.actor=actor;this.action=action;this.details=details;this.createdAt=Instant.now();}
 public UUID getId(){return id;} public Event getEvent(){return event;} public User getActor(){return actor;} public String getAction(){return action;} public String getDetails(){return details;} public Instant getCreatedAt(){return createdAt;}
}
