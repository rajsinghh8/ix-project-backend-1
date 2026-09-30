package com.eventforge.budget;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="budget_categories", uniqueConstraints=@UniqueConstraint(columnNames={"event_id","name"}))
public class BudgetCategory {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="event_id", nullable=false) private Event event;
 @Column(nullable=false,length=100) private String name;
 @Column(length=500) private String description;
 protected BudgetCategory(){}
 public BudgetCategory(Event event,String name,String description){this.event=event;this.name=name;this.description=description;}
 public UUID getId(){return id;} public Event getEvent(){return event;} public String getName(){return name;} public String getDescription(){return description;}
 public void setName(String name){this.name=name;} public void setDescription(String description){this.description=description;}
}