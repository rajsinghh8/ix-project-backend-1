package com.eventforge.invitation;

import com.eventforge.common.EventOwnership;
import com.eventforge.event.Event;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GuestInvitationService {
 private final GuestInvitationRepository invitations; private final EventOwnership events;
 public GuestInvitationService(GuestInvitationRepository invitations,EventOwnership events){this.invitations=invitations;this.events=events;}
 @Transactional(readOnly=true) public Page<GuestInvitationDtos.Response> list(UUID owner,UUID eventId,Pageable pageable){events.owned(eventId,owner);return invitations.findByEventId(eventId,pageable).map(GuestInvitationDtos.Response::from);}
 @Transactional public GuestInvitationDtos.Response create(UUID owner,UUID eventId,GuestInvitationDtos.Create request){Event event=events.lockedOwned(eventId,owner);return GuestInvitationDtos.Response.from(invitations.save(new GuestInvitation(event,request.name(),request.email(),request.phone())));}
 @Transactional public GuestInvitationDtos.Response update(UUID owner,UUID eventId,UUID id,GuestInvitationDtos.Update request){events.lockedOwned(eventId,owner);GuestInvitation g=guest(eventId,id);g.setName(request.name());g.setEmail(request.email());g.setPhone(request.phone());return GuestInvitationDtos.Response.from(g);}
 @Transactional public GuestInvitationDtos.Response changeStatus(UUID owner,UUID eventId,UUID id,GuestInvitationDtos.Status request){
  Event event=events.lockedOwned(eventId,owner); GuestInvitation g=guest(eventId,id); InvitationStatus before=g.getStatus(), after=request.status();
  if(before!=InvitationStatus.ACCEPTED && after==InvitationStatus.ACCEPTED){
   int current=event.getConfirmedGuestCount();
   if(event.getCapacity()!=null && current>=event.getCapacity()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Event capacity has been reached");
   event.setConfirmedGuestCount(current+1);
  } else if(before==InvitationStatus.ACCEPTED && after!=InvitationStatus.ACCEPTED) event.setConfirmedGuestCount(Math.max(0,event.getConfirmedGuestCount()-1));
  g.setStatus(after); return GuestInvitationDtos.Response.from(g);
 }
 @Transactional public void delete(UUID owner,UUID eventId,UUID id){Event event=events.lockedOwned(eventId,owner);GuestInvitation g=guest(eventId,id);if(g.getStatus()==InvitationStatus.ACCEPTED)event.setConfirmedGuestCount(Math.max(0,event.getConfirmedGuestCount()-1));invitations.delete(g);}
 private GuestInvitation guest(UUID eventId,UUID id){GuestInvitation g=invitations.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Invitation not found"));if(!g.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Invitation not found");return g;}
}
