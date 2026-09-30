package com.eventforge.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class EventTaskDtos {
    private EventTaskDtos() { }
    public record TaskRequest(@NotBlank String title, String description, @NotNull TaskPriority priority,
                              Instant deadline, @NotNull TaskStatus status, UUID assigneeId,
                              Set<UUID> dependencyIds) { }
    public record TaskResponse(UUID id, UUID eventId, UUID assigneeId, String title, String description,
                               TaskPriority priority, Instant deadline, TaskStatus status,
                               Set<UUID> dependencyIds) { }
}
