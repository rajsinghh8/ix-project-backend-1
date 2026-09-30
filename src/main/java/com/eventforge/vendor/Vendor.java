package com.eventforge.vendor;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name = "vendors")
public class Vendor {
 @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
 @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name="event_id", nullable=false) private Event event;
 @Column(nullable=false,length=160) private String name; @Column(length=100) private String category; @Column(length=160) private String contactName;
 @Column(length=160) private String email; @Column(length=50) private String phone; @Column(precision=14,scale=2) private BigDecimal contractedAmount;
 protected Vendor(){} public Vendor(Event event,String name,String category,String contactName,String email,String phone,BigDecimal contractedAmount){this.event=event; update(name,category,contactName,email,phone,contractedAmount);}
 public void update(String name,String category,String contactName,String email,String phone,BigDecimal contractedAmount){this.name=name;this.category=category;this.contactName=contactName;this.email=email;this.phone=phone;this.contractedAmount=contractedAmount;}
 public UUID getId(){return id;} public Event getEvent(){return event;} public String getName(){return name;} public String getCategory(){return category;} public String getContactName(){return contactName;} public String getEmail(){return email;} public String getPhone(){return phone;} public BigDecimal getContractedAmount(){return contractedAmount;}
}
