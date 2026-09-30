package com.eventforge.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class EventTaskDtos {
    private EventTaskDtos() { }
    @Schema(description = "Task create or update payload; dependencies must be tasks in the event")
    public record TaskRequest(@Schema(example = "Book venue") @NotBlank String title, @Schema(example = "Confirm the contract") String description,
            @Schema(example = "HIGH") @NotNull TaskPriority priority, @Schema(example = "2025-12-01T12:00:00Z") Instant deadline,
            @Schema(example = "TODO") @NotNull TaskStatus status, UUID assigneeId, Set<UUID> dependencyIds) { }
    @Schema(description = "Task response")
    public record TaskResponse(@Schema(example = "11111111-1111-1111-1111-111111111111") UUID id, UUID eventId, UUID assigneeId,
            @Schema(example = "Book venue") String title, @Schema(example = "Confirm the contract") String description,
            @Schema(example = "HIGH") TaskPriority priority, @Schema(example = "2025-12-01T12:00:00Z") Instant deadline,
            @Schema(example = "TODO") TaskStatus status, Set<UUID> dependencyIds) { }
}
