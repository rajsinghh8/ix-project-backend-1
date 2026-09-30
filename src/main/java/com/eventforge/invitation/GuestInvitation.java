package com.eventforge.invitation;

import com.eventforge.event.Event;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "guest_invitations", uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "email"}))
public class GuestInvitation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "event_id", nullable = false) private Event event;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, length = 254) private String email;
    @Column(length = 40) private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private InvitationStatus status = InvitationStatus.PENDING;
    protected GuestInvitation() {}
    public GuestInvitation(Event event, String name, String email, String phone) { this.event=event; this.name=name; this.email=email; this.phone=phone; }
    public UUID getId(){return id;} public Event getEvent(){return event;} public String getName(){return name;} public String getEmail(){return email;} public String getPhone(){return phone;} public InvitationStatus getStatus(){return status;}
    public void setName(String v){name=v;} public void setEmail(String v){email=v;} public void setPhone(String v){phone=v;} public void setStatus(InvitationStatus v){status=v;}
}