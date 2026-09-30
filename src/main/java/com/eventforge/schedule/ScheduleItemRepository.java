package com.eventforge.schedule;

import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, UUID> {
    @Query("select s from ScheduleItem s where s.event.id = :eventId and s.event.owner.id = :ownerId")
    Page<ScheduleItem> findAllOwned(UUID eventId, UUID ownerId, Pageable pageable);
    @Query("select count(s) > 0 from ScheduleItem s where s.event.id = :eventId and s.id <> :excludedId and s.startTime < :endTime and s.endTime > :startTime")
    boolean overlaps(UUID eventId, UUID excludedId, Instant startTime, Instant endTime);
    @Query("select count(s) > 0 from ScheduleItem s where s.event.id = :eventId and s.startTime < :endTime and s.endTime > :startTime")
    boolean overlapsNew(UUID eventId, Instant startTime, Instant endTime);
    @Modifying @Query("delete from ScheduleItem s where s.event.id = :eventId")
    void deleteByEventId(@org.springframework.data.repository.query.Param("eventId") UUID eventId);
}
