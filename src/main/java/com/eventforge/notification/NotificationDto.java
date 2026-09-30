package com.eventforge.notification;
import java.time.LocalDateTime; import java.util.UUID;
public record NotificationDto(UUID id,UUID eventId,NotificationType type,String message,boolean read,LocalDateTime createdAt){static NotificationDto from(Notification n){return new NotificationDto(n.getId(),n.getEvent().getId(),n.getType(),n.getMessage(),n.isRead(),n.getCreatedAt());}}
