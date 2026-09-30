package com.eventforge.common;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EventOwnership {
 private final EntityManager entityManager;
 public EventOwnership(EntityManager entityManager){this.entityManager=entityManager;}
 /** A write lock serializes capacity and aggregate-budget decisions for one event. */
 public Event lockedOwned(UUID eventId, UUID userId) {
  Event event=entityManager.find(Event.class,eventId,LockModeType.PESSIMISTIC_WRITE);
  if(event==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");
  if(!event.getOwner().getId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not own this event");
  return event;
 }
 public Event owned(UUID eventId, UUID userId) {
  Event event=entityManager.find(Event.class,eventId);
  if(event==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found");
  if(!event.getOwner().getId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You do not own this event");
  return event;
 }
}
