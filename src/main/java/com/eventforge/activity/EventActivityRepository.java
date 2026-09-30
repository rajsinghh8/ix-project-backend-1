package com.eventforge.activity;
import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*;
public interface EventActivityRepository extends JpaRepository<EventActivity,UUID>{@Query("select a from EventActivity a where a.event.id=:eventId and a.event.owner.id=:ownerId") Page<EventActivity> findAllOwned(UUID eventId,UUID ownerId,Pageable pageable);}
