package com.eventforge.budget;

import com.eventforge.activity.EventActivityService;
import com.eventforge.common.EventOwnership;
import com.eventforge.event.Event;
import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BudgetService {
 private final BudgetCategoryRepository categories; private final ExpenseRepository expenses; private final EventOwnership events;
 private final EventActivityService activities; private final NotificationService notifications;
 public BudgetService(BudgetCategoryRepository categories, ExpenseRepository expenses, EventOwnership events, EventActivityService activities, NotificationService notifications){this.categories=categories;this.expenses=expenses;this.events=events;this.activities=activities;this.notifications=notifications;}
 @Transactional(readOnly=true) public List<BudgetDtos.CategoryResponse> categories(UUID owner,UUID eventId){events.owned(eventId,owner);return categories.findByEventIdOrderByNameAsc(eventId).stream().map(BudgetDtos.CategoryResponse::from).toList();}
 @Transactional public BudgetDtos.CategoryResponse createCategory(UUID owner,UUID eventId,BudgetDtos.CategoryRequest r){Event e=events.lockedOwned(eventId,owner);BudgetCategory c=categories.save(new BudgetCategory(e,r.name(),r.description(),r.plannedAmount()));record(e,"BUDGET_CATEGORY_CREATED","Budget category created: "+c.getName());return BudgetDtos.CategoryResponse.from(c);}
 @Transactional public BudgetDtos.CategoryResponse updateCategory(UUID owner,UUID eventId,UUID id,BudgetDtos.CategoryRequest r){Event e=events.lockedOwned(eventId,owner);BudgetCategory c=category(eventId,id);c.setName(r.name());c.setDescription(r.description());c.setPlannedAmount(r.plannedAmount());record(e,"BUDGET_CATEGORY_UPDATED","Budget category updated: "+c.getName());return BudgetDtos.CategoryResponse.from(c);}
 @Transactional public void deleteCategory(UUID owner,UUID eventId,UUID id){Event e=events.lockedOwned(eventId,owner);BudgetCategory c=category(eventId,id);String name=c.getName();categories.delete(c);record(e,"BUDGET_CATEGORY_DELETED","Budget category deleted: "+name);}
 @Transactional(readOnly=true) public Page<BudgetDtos.ExpenseResponse> listExpenses(UUID owner,UUID eventId,UUID categoryId,String search,LocalDate dateFrom,LocalDate dateTo,Pageable page){events.owned(eventId,owner);return expenses.findFiltered(eventId,categoryId,blankToNull(search),dateFrom,dateTo,page).map(BudgetDtos.ExpenseResponse::from);}
 @Transactional public BudgetDtos.ExpenseResponse createExpense(UUID owner,UUID eventId,BudgetDtos.ExpenseRequest r){Event e=events.lockedOwned(eventId,owner);BudgetCategory c=category(eventId,r.categoryId());ensureBudget(e,expenses.totalForEvent(eventId).add(r.amount()));Expense x=expenses.save(new Expense(e,c,r.description(),r.amount(),r.expenseDate()));record(e,"EXPENSE_CREATED","Expense created: "+x.getDescription());return BudgetDtos.ExpenseResponse.from(x);}
 @Transactional public BudgetDtos.ExpenseResponse updateExpense(UUID owner,UUID eventId,UUID id,BudgetDtos.ExpenseRequest r){Event e=events.lockedOwned(eventId,owner);Expense x=expense(eventId,id);BudgetCategory c=category(eventId,r.categoryId());ensureBudget(e,expenses.totalForEvent(eventId).subtract(x.getAmount()).add(r.amount()));x.setCategory(c);x.setDescription(r.description());x.setAmount(r.amount());x.setExpenseDate(r.expenseDate());record(e,"EXPENSE_UPDATED","Expense updated: "+x.getDescription());return BudgetDtos.ExpenseResponse.from(x);}
 @Transactional public void deleteExpense(UUID owner,UUID eventId,UUID id){Event e=events.lockedOwned(eventId,owner);Expense x=expense(eventId,id);String description=x.getDescription();expenses.delete(x);record(e,"EXPENSE_DELETED","Expense deleted: "+description);}
 private void record(Event event,String action,String details){activities.recordActivity(event,event.getOwner(),action,details);notifications.recordForEventOwner(event,NotificationType.BUDGET,details);}
 private String blankToNull(String value){return value==null||value.isBlank()?null:value;}
 private void ensureBudget(Event e,BigDecimal total){if(e.getBudgetLimit()!=null && total.compareTo(e.getBudgetLimit())>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Expense total exceeds event budget");}
 private BudgetCategory category(UUID eventId,UUID id){BudgetCategory c=categories.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Budget category not found"));if(!c.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Budget category not found");return c;}
 private Expense expense(UUID eventId,UUID id){Expense x=expenses.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Expense not found"));if(!x.getEvent().getId().equals(eventId))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Expense not found");return x;}
}
