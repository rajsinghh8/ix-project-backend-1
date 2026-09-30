package com.eventforge.notification;
import com.eventforge.event.Event; import com.eventforge.domain.User; import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException;
@Service @Transactional public class NotificationService {
 private final NotificationRepository notifications; public NotificationService(NotificationRepository notifications){this.notifications=notifications;}
 /** Public integration point for feature services; recipients are explicitly supplied. */ public NotificationDto recordNotification(Event event,User recipient,NotificationType type,String message){if(event==null||recipient==null||type==null||message==null||message.isBlank())throw new IllegalArgumentException("event, recipient, type and message are required");return NotificationDto.from(notifications.save(new Notification(event,recipient,type,message)));}
 /** Convenience integration point for notifications belonging to an event owner. */ public NotificationDto recordForEventOwner(Event event,NotificationType type,String message){return recordNotification(event,event.getOwner(),type,message);}
 @Transactional(readOnly=true) public Page<NotificationDto> list(UUID userId,Pageable pageable){return notifications.findByRecipientId(userId,pageable).map(NotificationDto::from);}
 public NotificationDto markRead(UUID id,UUID userId){Notification n=notifications.findByIdAndRecipientId(id,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found"));n.markRead();return NotificationDto.from(n);}
}
