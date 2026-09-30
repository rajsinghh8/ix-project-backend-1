package com.eventforge.invitation;

import jakarta.validation.constraints.*;
import java.util.UUID;

public final class GuestInvitationDtos {
 private GuestInvitationDtos() {}
 public record Create(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=254) String email, @Size(max=40) String phone) {}
 public record Update(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=254) String email, @Size(max=40) String phone) {}
 public record Status(@NotNull InvitationStatus status) {}
 public record Response(UUID id, String name, String email, String phone, InvitationStatus status) {
  public static Response from(GuestInvitation g){return new Response(g.getId(),g.getName(),g.getEmail(),g.getPhone(),g.getStatus());}
 }
}
