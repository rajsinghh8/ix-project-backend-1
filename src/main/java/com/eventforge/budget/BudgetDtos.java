package com.eventforge.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class BudgetDtos {
 private BudgetDtos() { }
 @Schema(description = "Budget category create or update payload")
 public record CategoryRequest(@Schema(example="Catering") @NotBlank @Size(max=100) String name, @Schema(example="Food and drinks") @Size(max=500) String description, @Schema(example="1200.00") @NotNull @DecimalMin(value="0.0", inclusive=true) @Digits(integer=17,fraction=2) BigDecimal plannedAmount) { }
 public record CategoryResponse(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id, @Schema(example="Catering") String name, @Schema(example="Food and drinks") String description, @Schema(example="1200.00") BigDecimal plannedAmount) { public static CategoryResponse from(BudgetCategory c){return new CategoryResponse(c.getId(),c.getName(),c.getDescription(),c.getPlannedAmount());} }
 @Schema(description = "Expense create or update payload")
 public record ExpenseRequest(UUID categoryId, @Schema(example="Deposit") @NotBlank @Size(max=140) String description, @Schema(example="450.00") @NotNull @DecimalMin(value="0.01") @Digits(integer=17,fraction=2) BigDecimal amount, @Schema(example="2025-12-01") @NotNull LocalDate expenseDate) { }
 public record ExpenseResponse(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id, UUID categoryId, @Schema(example="Catering") String categoryName, @Schema(example="Deposit") String description, @Schema(example="450.00") BigDecimal amount, @Schema(example="2025-12-01") LocalDate expenseDate) { public static ExpenseResponse from(Expense e){return new ExpenseResponse(e.getId(),e.getCategory().getId(),e.getCategory().getName(),e.getDescription(),e.getAmount(),e.getExpenseDate());} }
}
