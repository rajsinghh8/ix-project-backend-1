package com.eventforge.budget;

import com.eventforge.common.EventOwnership;
import com.eventforge.event.Event;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BudgetService {
 private final BudgetCategoryRepository categories; private final ExpenseRepository expenses; private final EventOwnership events;
 public BudgetService(BudgetCategoryRepository categories,ExpenseRepository expenses,EventOwnership events){this.categories=categories;this.expenses=expenses;this.events=events;}
 @Transactional(readOnly=true) public List<BudgetDtos.CategoryResponse> categories(UUID owner,UUID eventId){events.owned(eventId,owner);return categories.findByEventIdOrderByNameAsc(eventId).stream().map(BudgetDtos.CategoryResponse::from).toList();}
 @Transactional public BudgetDtos.CategoryResponse createCategory(UUID owner,UUID eventId,BudgetDtos.CategoryRequest r){Event e=events.lockedOwned(eventId,owner);return BudgetDtos.CategoryResponse.from(categories.save(new BudgetCategory(e,r.name(),r.description())));}
 @Transactional public BudgetDtos.CategoryResponse updateCategory(UUID owner,UUID eventId,UUID id,BudgetDtos.CategoryRequest r){events.lockedOwned(eventId,owner);BudgetCategory c=category(eventId,id);c.setName(r.name());c.setDescription(r.description());return BudgetDtos.CategoryResponse.from(c);}
 @Transactional public void deleteCategory(UUID owner,UUID eventId,UUID id){events.lockedOwned(eventId,owner);categories.delete(category(eventId,id));}
 @Transactional(readOnly=true) public Page<BudgetDtos.ExpenseResponse> listExpenses(UUID owner,UUID eventId,Pageable page){events.owned(eventId,owner);return expenses.findByEventId(eventId,page).map(BudgetDtos.ExpenseResponse::from);}
 @Transactional public BudgetDtos.ExpenseResponse createExpense(UUID owner,UUID eventId,BudgetDtos.ExpenseRequest r){Event e=events.lockedOwned(eventId,owner);BudgetCategory c=category(eventId,r.categoryId());ensureBudget(e,expenses.totalForEvent(eventId).add(r.amount()));return BudgetDtos.ExpenseResponse.from(expenses.save(new Expense(e,c,r.description(),r.amount(),r.expenseDate())));}
 @Transactional public BudgetDtos.ExpenseResponse updateExpense(UUID owner,UUID eventId,UUID id,BudgetDtos.ExpenseRequest r){Event e=events.lockedOwned(eventId,owner);Expense x=expense(eventId,id);BudgetCategory c=category(eventId,r.categoryId());ensureBudget(e,expenses.totalForEvent(eventId).subtract(x.getAmount()).add(r.amount()));x.setCategory(c);x.setDescription(r.description());x.setAmount(r.amount());x.setExpenseDate(r.expenseDate());return BudgetDtos.ExpenseResponse.from(x);}
 @Transactional public void deleteExpense(UUID owner,UUID eventId,UUID id){events.lockedOwned(eventId,owner);expenses.delete(expense(eventId,id));}
 private void ensureBudget(Event e,BigDecimal total){if(e.getBudgetLimit()!=null && total.compareTo(e.getBudgetLimit())>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Expense total exceeds event budget");}
 private BudgetCategory category(UUID eventId,UUID id){BudgetCategory c=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Budget category not found"));if(!c.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Budget category not found");return c;}
 private Expense expense(UUID eventId,UUID id){Expense x=expenses.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Expense not found"));if(!x.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Expense not found");return x;}
}
