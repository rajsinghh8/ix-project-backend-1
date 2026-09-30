package com.eventforge.event;

import com.eventforge.domain.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 200)
    private String name;
    @Column(nullable = false)
    private LocalDate eventDate;
    @Column(nullable = false, length = 300)
    private String venue;
    @Column(nullable = false)
    private Integer capacity;
    @Column(nullable = false)
    private Integer confirmedGuestCount = 0;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budgetLimit;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status = EventStatus.DRAFT;
    @Column(length = 4000)
    private String description;

    protected Event() { }

    public Event(User owner, String name, LocalDate eventDate, String venue, Integer capacity,
                 BigDecimal budgetLimit, EventStatus status, String description) {
        this.owner = owner;
        this.name = name;
        this.eventDate = eventDate;
        this.venue = venue;
        this.capacity = capacity;
        this.budgetLimit = budgetLimit;
        this.status = status == null ? EventStatus.DRAFT : status;
        this.description = description;
    }

    public UUID getId() { return id; }
    public User getOwner() { return owner; }
    public String getName() { return name; }
    public LocalDate getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public Integer getCapacity() { return capacity; }
    public Integer getConfirmedGuestCount() { return confirmedGuestCount; }
    public void setConfirmedGuestCount(Integer confirmedGuestCount) { this.confirmedGuestCount = confirmedGuestCount; }
    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public EventStatus getStatus() { return status; }
    public String getDescription() { return description; }

    public void update(String name, LocalDate eventDate, String venue, Integer capacity,
                       BigDecimal budgetLimit, EventStatus status, String description) {
        this.name = name;
        this.eventDate = eventDate;
        this.venue = venue;
        this.capacity = capacity;
        this.budgetLimit = budgetLimit;
        this.status = status;
        this.description = description;
    }
}
