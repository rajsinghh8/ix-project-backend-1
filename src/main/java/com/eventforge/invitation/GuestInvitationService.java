package com.eventforge.invitation;

import com.eventforge.activity.EventActivityService;
import com.eventforge.common.EventOwnership;
import com.eventforge.event.Event;
import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GuestInvitationService {
 private final GuestInvitationRepository invitations; private final EventOwnership events;
 private final EventActivityService activities; private final NotificationService notifications;
 public GuestInvitationService(GuestInvitationRepository invitations,EventOwnership events,EventActivityService activities,NotificationService notifications){this.invitations=invitations;this.events=events;this.activities=activities;this.notifications=notifications;}
 @Transactional(readOnly=true) public Page<GuestInvitationDtos.Response> list(UUID owner,UUID eventId,Pageable pageable){events.owned(eventId,owner);return invitations.findByEventId(eventId,pageable).map(GuestInvitationDtos.Response::from);}
 @Transactional public GuestInvitationDtos.Response create(UUID owner,UUID eventId,GuestInvitationDtos.Create request){Event event=events.lockedOwned(eventId,owner);GuestInvitation invitation=invitations.save(new GuestInvitation(event,request.name(),request.email(),request.phone()));record(event,"INVITATION_CREATED","Invitation created for "+invitation.getName());return GuestInvitationDtos.Response.from(invitation);}
 @Transactional public GuestInvitationDtos.Response update(UUID owner,UUID eventId,UUID id,GuestInvitationDtos.Update request){Event event=events.lockedOwned(eventId,owner);GuestInvitation g=guest(eventId,id);g.setName(request.name());g.setEmail(request.email());g.setPhone(request.phone());record(event,"INVITATION_UPDATED","Invitation updated for "+g.getName());return GuestInvitationDtos.Response.from(g);}
 @Transactional public GuestInvitationDtos.Response changeStatus(UUID owner,UUID eventId,UUID id,GuestInvitationDtos.Status request){
  Event event=events.lockedOwned(eventId,owner); GuestInvitation g=guest(eventId,id); InvitationStatus before=g.getStatus(), after=request.status();
  if(before!=InvitationStatus.ACCEPTED && after==InvitationStatus.ACCEPTED){
   int current=event.getConfirmedGuestCount();
   if(event.getCapacity()!=null && current>=event.getCapacity()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Event capacity has been reached");
   event.setConfirmedGuestCount(current+1);
  } else if(before==InvitationStatus.ACCEPTED && after!=InvitationStatus.ACCEPTED) event.setConfirmedGuestCount(Math.max(0,event.getConfirmedGuestCount()-1));
  g.setStatus(after);record(event,"INVITATION_STATUS_CHANGED","Invitation response for "+g.getName()+": "+after);return GuestInvitationDtos.Response.from(g);
 }
 @Transactional public void delete(UUID owner,UUID eventId,UUID id){Event event=events.lockedOwned(eventId,owner);GuestInvitation g=guest(eventId,id);String name=g.getName();if(g.getStatus()==InvitationStatus.ACCEPTED)event.setConfirmedGuestCount(Math.max(0,event.getConfirmedGuestCount()-1));invitations.delete(g);record(event,"INVITATION_DELETED","Invitation deleted for "+name);}
 private void record(Event event,String action,String details){activities.recordActivity(event,event.getOwner(),action,details);notifications.recordForEventOwner(event,NotificationType.INVITATION,details);}
 private GuestInvitation guest(UUID eventId,UUID id){GuestInvitation g=invitations.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Invitation not found"));if(!g.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invitation not found");return g;}
}
