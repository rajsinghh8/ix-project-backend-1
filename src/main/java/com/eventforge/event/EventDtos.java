package com.eventforge.event;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class EventDtos {
    private EventDtos() { }

    public record EventRequest(
            @NotBlank String name,
            @NotNull @FutureOrPresent LocalDate eventDate,
            @NotBlank String venue,
            @NotNull @Min(1) Integer capacity,
            @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal budgetLimit,
            @NotNull EventStatus status,
            String description) { }

    public record BudgetSummary(BigDecimal budgetLimit, BigDecimal planned, BigDecimal actual,
                                BigDecimal remaining) { }

    public record EventResponse(UUID id, String name, LocalDate eventDate, String venue,
                                Integer capacity, Integer confirmedGuestCount, BigDecimal budgetLimit,
                                EventStatus status, String description, BudgetSummary budgetSummary) { }
}
