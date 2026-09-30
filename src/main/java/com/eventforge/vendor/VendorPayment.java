package com.eventforge.vendor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vendor_payments")
public class VendorPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Instant paidAt;

    @Column(length = 1000)
    private String note;

    protected VendorPayment() {
    }

    public VendorPayment(Vendor vendor, BigDecimal amount, Instant paidAt, String note) {
        this.vendor = vendor;
        this.amount = amount;
        this.paidAt = paidAt;
        this.note = note;
    }

    public UUID getId() { return id; }
    public Vendor getVendor() { return vendor; }
    public BigDecimal getAmount() { return amount; }
    public Instant getPaidAt() { return paidAt; }
    public String getNote() { return note; }
}
