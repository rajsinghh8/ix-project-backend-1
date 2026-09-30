package com.eventforge.schedule;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public final class ScheduleItemDto {
    private ScheduleItemDto() { }
    public record Request(@NotBlank @Size(max = 160) String title, @Size(max = 2000) String description,
                          @NotNull LocalDateTime startTime, @NotNull LocalDateTime endTime, @Size(max = 300) String location) {
        @AssertTrue(message = "endTime must be after startTime") public boolean isTimeRangeValid() {
            return startTime == null || endTime == null || endTime.isAfter(startTime);
        }
    }
    public record Response(UUID id, UUID eventId, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location) {
        static Response from(ScheduleItem item) { return new Response(item.getId(), item.getEvent().getId(), item.getTitle(), item.getDescription(), item.getStartTime(), item.getEndTime(), item.getLocation()); }
    }
}
