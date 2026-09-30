package com.eventforge.invitation;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GuestInvitationRepository extends JpaRepository<GuestInvitation,UUID>{ Page<GuestInvitation> findByEventId(UUID eventId, Pageable pageable); @org.springframework.data.jpa.repository.Modifying @org.springframework.data.jpa.repository.Query("delete from GuestInvitation i where i.event.id=:eventId") void deleteByEventId(@org.springframework.data.repository.query.Param("eventId") UUID eventId); }
