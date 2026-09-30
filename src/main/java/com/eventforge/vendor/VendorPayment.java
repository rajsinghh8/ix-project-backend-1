package com.eventforge.vendor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Entity @Table(name="vendor_payments")
public class VendorPayment {
 @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="vendor_id",nullable=false) private Vendor vendor;
 @Column(nullable=false,precision=14,scale=2) private BigDecimal amount; @Column(nullable=false) private LocalDate paidOn; @Column(length=1000) private String note;
 protected VendorPayment(){} public VendorPayment(Vendor vendor,BigDecimal amount,LocalDate paidOn,String note){this.vendor=vendor;this.amount=amount;this.paidOn=paidOn;this.note=note;}
 public UUID getId(){return id;} public Vendor getVendor(){return vendor;} public BigDecimal getAmount(){return amount;} public LocalDate getPaidOn(){return paidOn;} public String getNote(){return note;}
}
