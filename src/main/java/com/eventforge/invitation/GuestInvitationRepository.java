package com.eventforge.invitation;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface GuestInvitationRepository extends JpaRepository<GuestInvitation,UUID>{ Page<GuestInvitation> findByEventId(UUID eventId, Pageable pageable); }
