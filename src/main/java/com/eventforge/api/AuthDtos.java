package com.eventforge.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

/** Authentication API payloads. Password values are accepted only in requests. */
public final class AuthDtos {
    private AuthDtos() { }

    @Schema(description = "Credentials used to create an account")
    public record RegisterRequest(
            @Schema(example = "Alex Planner") @NotBlank @Size(max = 120) String name,
            @Schema(example = "alex@example.com") @NotBlank @Email @Size(max = 320) String email,
            @Schema(example = "secure-password") @NotBlank @Size(min = 8, max = 72) String password) { }
    @Schema(description = "Credentials used to obtain a bearer token")
    public record LoginRequest(@Schema(example = "alex@example.com") @NotBlank @Email @Size(max = 320) String email,
                               @Schema(example = "secure-password") @NotBlank @Size(max = 72) String password) { }
    public record UserResponse(@Schema(example = "11111111-1111-1111-1111-111111111111") UUID id,
                               @Schema(example = "Alex Planner") String name, @Schema(example = "alex@example.com") String email,
                               @Schema(example = "2025-01-01T00:00:00Z") Instant createdAt) { }
    @Schema(description = "Successful authentication response")
    public record AuthResponse(@Schema(example = "eyJhbGciOiJIUzI1NiJ9...") String token,
                               @Schema(example = "Bearer") String tokenType, UserResponse user) { }
}
