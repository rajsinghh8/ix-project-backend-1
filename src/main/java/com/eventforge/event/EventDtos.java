package com.eventforge.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class EventDtos {
    private EventDtos() { }
    @Schema(description = "Event create or update payload")
    public record EventRequest(@Schema(example = "Product launch") @NotBlank String name,
            @Schema(example = "2025-12-31") @NotNull @FutureOrPresent LocalDate eventDate,
            @Schema(example = "Hall A") @NotBlank String venue, @Schema(example = "100") @NotNull @Min(1) Integer capacity,
            @Schema(example = "5000.00") @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal budgetLimit,
            @Schema(example = "PLANNED") @NotNull EventStatus status, @Schema(example = "Annual launch party") String description) { }
    public record BudgetSummary(@Schema(example = "5000.00") BigDecimal budgetLimit, @Schema(example = "1200.00") BigDecimal planned,
                                @Schema(example = "450.00") BigDecimal actual, @Schema(example = "4550.00") BigDecimal remaining) { }
    @Schema(description = "Owned event response")
    public record EventResponse(@Schema(example = "11111111-1111-1111-1111-111111111111") UUID id, @Schema(example = "Product launch") String name,
            @Schema(example = "2025-12-31") LocalDate eventDate, @Schema(example = "Hall A") String venue, @Schema(example = "100") Integer capacity,
            @Schema(example = "0") Integer confirmedGuestCount, @Schema(example = "5000.00") BigDecimal budgetLimit,
            @Schema(example = "PLANNED") EventStatus status, @Schema(example = "Annual launch party") String description, BudgetSummary budgetSummary) { }
}
