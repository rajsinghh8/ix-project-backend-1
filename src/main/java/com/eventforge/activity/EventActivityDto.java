package com.eventforge.activity;
import java.time.Instant; import java.util.UUID;
public record EventActivityDto(UUID id,UUID eventId,UUID actorId,String action,String details,Instant createdAt){static EventActivityDto from(EventActivity a){return new EventActivityDto(a.getId(),a.getEvent().getId(),a.getActor()==null?null:a.getActor().getId(),a.getAction(),a.getDetails(),a.getCreatedAt());}}
