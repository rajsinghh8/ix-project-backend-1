package com.eventforge.invitation;

import com.eventforge.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/guests")
public class GuestInvitationController {
 private final GuestInvitationService service; private final CurrentUser currentUser;
 public GuestInvitationController(GuestInvitationService service,CurrentUser currentUser){this.service=service;this.currentUser=currentUser;}
 @Operation(summary="List event invitations") @GetMapping public Page<GuestInvitationDtos.Response> list(@PathVariable UUID eventId,@PageableDefault(size=20,sort="name") Pageable pageable){return service.list(currentUser.id(),eventId,pageable);}
 @Operation(summary="Create invitation") @PostMapping @ResponseStatus(HttpStatus.CREATED) public GuestInvitationDtos.Response create(@PathVariable UUID eventId,@Valid @RequestBody GuestInvitationDtos.Create request){return service.create(currentUser.id(),eventId,request);}
 @PutMapping("/{invitationId}") public GuestInvitationDtos.Response update(@PathVariable UUID eventId,@PathVariable UUID invitationId,@Valid @RequestBody GuestInvitationDtos.Update request){return service.update(currentUser.id(),eventId,invitationId,request);}
 @PatchMapping("/{invitationId}/status") public GuestInvitationDtos.Response status(@PathVariable UUID eventId,@PathVariable UUID invitationId,@Valid @RequestBody GuestInvitationDtos.Status request){return service.changeStatus(currentUser.id(),eventId,invitationId,request);}
 @DeleteMapping("/{invitationId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID eventId,@PathVariable UUID invitationId){service.delete(currentUser.id(),eventId,invitationId);}
}
