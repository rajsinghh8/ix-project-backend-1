package com.eventforge.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public final class ScheduleItemDto {
 private ScheduleItemDto() { }
 @Schema(description="Schedule item create or update payload")
 public record Request(@Schema(example="Welcome reception") @NotBlank @Size(max=160) String title, @Schema(example="Opening remarks") @Size(max=2000) String description,
  @Schema(example="2025-12-31T18:00:00Z") @NotNull Instant startTime, @Schema(example="2025-12-31T19:00:00Z") @NotNull Instant endTime, @Schema(example="Main hall") @Size(max=300) String location) {
  @AssertTrue(message = "endTime must be after startTime") public boolean isTimeRangeValid() { return startTime == null || endTime == null || endTime.isAfter(startTime); }
 }
 public record Response(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id, UUID eventId, @Schema(example="Welcome reception") String title, @Schema(example="Opening remarks") String description, @Schema(example="2025-12-31T18:00:00Z") Instant startTime, @Schema(example="2025-12-31T19:00:00Z") Instant endTime, @Schema(example="Main hall") String location) { static Response from(ScheduleItem item) { return new Response(item.getId(), item.getEvent().getId(), item.getTitle(), item.getDescription(), item.getStartTime(), item.getEndTime(), item.getLocation()); } }
}
