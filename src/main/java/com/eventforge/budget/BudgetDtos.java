package com.eventforge.budget;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class BudgetDtos {
 private BudgetDtos(){}
 public record CategoryRequest(@NotBlank @Size(max=100) String name, @Size(max=500) String description, @NotNull @DecimalMin(value="0.0", inclusive=true) @Digits(integer=17,fraction=2) BigDecimal plannedAmount) {}
 public record CategoryResponse(UUID id,String name,String description,BigDecimal plannedAmount){ public static CategoryResponse from(BudgetCategory c){return new CategoryResponse(c.getId(),c.getName(),c.getDescription(),c.getPlannedAmount());} }
 public record ExpenseRequest(@NotNull UUID categoryId,@NotBlank @Size(max=140) String description,@NotNull @DecimalMin(value="0.01") @Digits(integer=17,fraction=2) BigDecimal amount,@NotNull LocalDate expenseDate) {}
 public record ExpenseResponse(UUID id,UUID categoryId,String categoryName,String description,BigDecimal amount,LocalDate expenseDate){ public static ExpenseResponse from(Expense e){return new ExpenseResponse(e.getId(),e.getCategory().getId(),e.getCategory().getName(),e.getDescription(),e.getAmount(),e.getExpenseDate());} }
}
