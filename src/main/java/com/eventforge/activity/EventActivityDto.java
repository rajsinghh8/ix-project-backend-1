package com.eventforge.activity;
import java.time.LocalDateTime; import java.util.UUID;
public record EventActivityDto(UUID id,UUID eventId,UUID actorId,String action,String details,LocalDateTime createdAt){static EventActivityDto from(EventActivity a){return new EventActivityDto(a.getId(),a.getEvent().getId(),a.getActor()==null?null:a.getActor().getId(),a.getAction(),a.getDetails(),a.getCreatedAt());}}
