package com.eventforge.vendor;

import com.eventforge.event.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, length = 100)
    private String serviceType;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal cost;

    @Column(nullable = false, length = 1000)
    private String contactDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VendorBookingStatus bookingStatus;

    protected Vendor() {
    }

    public Vendor(Event event, String name, String serviceType, BigDecimal cost,
                  String contactDetails, VendorBookingStatus bookingStatus) {
        this.event = event;
        update(name, serviceType, cost, contactDetails, bookingStatus);
    }

    public void update(String name, String serviceType, BigDecimal cost,
                       String contactDetails, VendorBookingStatus bookingStatus) {
        this.name = name;
        this.serviceType = serviceType;
        this.cost = cost;
        this.contactDetails = contactDetails;
        this.bookingStatus = bookingStatus;
    }

    public UUID getId() { return id; }
    public Event getEvent() { return event; }
    public String getName() { return name; }
    public String getServiceType() { return serviceType; }
    public BigDecimal getCost() { return cost; }
    public String getContactDetails() { return contactDetails; }
    public VendorBookingStatus getBookingStatus() { return bookingStatus; }
}
