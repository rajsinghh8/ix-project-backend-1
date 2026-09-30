package com.eventforge.notification;
import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<Notification,UUID>{Page<Notification> findByRecipientId(UUID recipientId,Pageable pageable); Optional<Notification> findByIdAndRecipientId(UUID id,UUID recipientId);}
