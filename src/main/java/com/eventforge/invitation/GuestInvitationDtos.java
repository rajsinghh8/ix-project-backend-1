package com.eventforge.invitation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.UUID;

public final class GuestInvitationDtos {
 private GuestInvitationDtos() { }
 @Schema(description="Guest invitation create payload")
 public record Create(@Schema(example="Taylor Guest") @NotBlank @Size(max=120) String name, @Schema(example="taylor@example.com") @NotBlank @Email @Size(max=254) String email, @Schema(example="+15551234567") @Size(max=40) String phone) { }
 @Schema(description="Guest invitation update payload")
 public record Update(@Schema(example="Taylor Guest") @NotBlank @Size(max=120) String name, @Schema(example="taylor@example.com") @NotBlank @Email @Size(max=254) String email, @Schema(example="+15551234567") @Size(max=40) String phone) { }
 public record Status(@Schema(example="ACCEPTED") @NotNull InvitationStatus status) { }
 public record Response(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id, @Schema(example="Taylor Guest") String name, @Schema(example="taylor@example.com") String email, @Schema(example="+15551234567") String phone, @Schema(example="PENDING") InvitationStatus status) { public static Response from(GuestInvitation g){return new Response(g.getId(),g.getName(),g.getEmail(),g.getPhone(),g.getStatus());} }
}
