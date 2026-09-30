package com.eventforge.activity;
import com.eventforge.event.*; import com.eventforge.domain.User; import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
@Service @Transactional public class EventActivityService {
 private final EventActivityRepository activities; private final EventRepository events; public EventActivityService(EventActivityRepository activities,EventRepository events){this.activities=activities;this.events=events;}
 /** Public integration point; activity entries have no update or delete operation. */ public EventActivityDto recordActivity(Event event,User actor,String action,String details){if(event==null||action==null||action.isBlank())throw new IllegalArgumentException("event and action are required");return EventActivityDto.from(activities.save(new EventActivity(event,actor,action,details)));}
 @Transactional(readOnly=true) public Page<EventActivityDto> list(UUID eventId,UUID ownerId,Pageable pageable){event(eventId,ownerId);return activities.findAllOwned(eventId,ownerId,pageable).map(EventActivityDto::from);}
 private Event event(UUID id,UUID owner){return events.findById(id).filter(e->e.getOwner().getId().equals(owner)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Event not found"));}
}
