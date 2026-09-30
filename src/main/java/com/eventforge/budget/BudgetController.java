package com.eventforge.budget;

import com.eventforge.common.CurrentUser;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/budget")
public class BudgetController {
 private final BudgetService service; private final CurrentUser currentUser;
 public BudgetController(BudgetService service,CurrentUser currentUser){this.service=service;this.currentUser=currentUser;}
 @GetMapping("/categories") public List<BudgetDtos.CategoryResponse> categories(@PathVariable UUID eventId){return service.categories(currentUser.id(),eventId);}
 @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED) public BudgetDtos.CategoryResponse createCategory(@PathVariable UUID eventId,@Valid @RequestBody BudgetDtos.CategoryRequest request){return service.createCategory(currentUser.id(),eventId,request);}
 @PutMapping("/categories/{categoryId}") public BudgetDtos.CategoryResponse updateCategory(@PathVariable UUID eventId,@PathVariable UUID categoryId,@Valid @RequestBody BudgetDtos.CategoryRequest request){return service.updateCategory(currentUser.id(),eventId,categoryId,request);}
 @DeleteMapping("/categories/{categoryId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteCategory(@PathVariable UUID eventId,@PathVariable UUID categoryId){service.deleteCategory(currentUser.id(),eventId,categoryId);}
 @GetMapping("/expenses") public Page<BudgetDtos.ExpenseResponse> expenses(@PathVariable UUID eventId,@PageableDefault(size=20,sort="expenseDate",direction=Sort.Direction.DESC) Pageable pageable){return service.listExpenses(currentUser.id(),eventId,pageable);}
 @PostMapping("/expenses") @ResponseStatus(HttpStatus.CREATED) public BudgetDtos.ExpenseResponse createExpense(@PathVariable UUID eventId,@Valid @RequestBody BudgetDtos.ExpenseRequest request){return service.createExpense(currentUser.id(),eventId,request);}
 @PutMapping("/expenses/{expenseId}") public BudgetDtos.ExpenseResponse updateExpense(@PathVariable UUID eventId,@PathVariable UUID expenseId,@Valid @RequestBody BudgetDtos.ExpenseRequest request){return service.updateExpense(currentUser.id(),eventId,expenseId,request);}
 @DeleteMapping("/expenses/{expenseId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteExpense(@PathVariable UUID eventId,@PathVariable UUID expenseId){service.deleteExpense(currentUser.id(),eventId,expenseId);}
}
