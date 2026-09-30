package com.eventforge.notification;

import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientId(UUID recipientId, Pageable pageable);
    Optional<Notification> findByIdAndRecipientId(UUID id, UUID recipientId);
    boolean existsByTask_IdAndTypeAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            UUID taskId, NotificationType type, LocalDateTime start, LocalDateTime end);
    @Modifying
    @Query("delete from Notification n where n.event.id = :eventId")
    void deleteByEventId(@Param("eventId") UUID eventId);
}
