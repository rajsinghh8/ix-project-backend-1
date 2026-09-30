package com.eventforge.task;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventTaskRepository extends JpaRepository<EventTask, UUID> {
    @Query("""
            select t from EventTask t
            where t.event.id = :eventId and t.event.owner.id = :ownerId
              and (:search is null or lower(t.title) like lower(concat('%', :search, '%'))
                   or lower(coalesce(t.description, '')) like lower(concat('%', :search, '%')))
              and (:status is null or t.status = :status)
              and (:deadlineFrom is null or t.deadline >= :deadlineFrom)
              and (:deadlineTo is null or t.deadline <= :deadlineTo)
            """)
    Page<EventTask> searchOwned(@Param("eventId") UUID eventId, @Param("ownerId") UUID ownerId,
                                @Param("search") String search, @Param("status") TaskStatus status,
                                @Param("deadlineFrom") Instant deadlineFrom,
                                @Param("deadlineTo") Instant deadlineTo, Pageable pageable);

    Optional<EventTask> findByIdAndEvent_IdAndEvent_Owner_Id(UUID id, UUID eventId, UUID ownerId);

    @Query("select t from EventTask t where t.deadline is not null and t.status <> :completed and t.deadline > :now and t.deadline <= :approachingBy")
    java.util.List<EventTask> findApproachingDeadlines(@Param("now") Instant now, @Param("approachingBy") Instant approachingBy, @Param("completed") TaskStatus completed);

    @Query("select t from EventTask t where t.deadline is not null and t.status <> :completed and t.deadline <= :now")
    java.util.List<EventTask> findOverdueDeadlines(@Param("now") Instant now, @Param("completed") TaskStatus completed);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "delete from event_task_dependencies where task_id in (select id from event_tasks where event_id = :eventId) or dependency_id in (select id from event_tasks where event_id = :eventId)", nativeQuery = true)
    void deleteDependenciesByEventId(@Param("eventId") UUID eventId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("delete from EventTask t where t.event.id = :eventId")
    void deleteByEventId(@Param("eventId") UUID eventId);
}
