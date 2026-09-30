package com.eventforge.budget;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="expenses")
public class Expense {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="event_id",nullable=false) private Event event;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="category_id",nullable=false) private BudgetCategory category;
 @Column(nullable=false,length=140) private String description;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
 @Column(nullable=false) private LocalDate expenseDate;
 protected Expense(){}
 public Expense(Event event,BudgetCategory category,String description,BigDecimal amount,LocalDate expenseDate){this.event=event;this.category=category;this.description=description;this.amount=amount;this.expenseDate=expenseDate;}
 public UUID getId(){return id;} public Event getEvent(){return event;} public BudgetCategory getCategory(){return category;} public String getDescription(){return description;} public BigDecimal getAmount(){return amount;} public LocalDate getExpenseDate(){return expenseDate;}
 public void setCategory(BudgetCategory v){category=v;} public void setDescription(String v){description=v;} public void setAmount(BigDecimal v){amount=v;} public void setExpenseDate(LocalDate v){expenseDate=v;}
}