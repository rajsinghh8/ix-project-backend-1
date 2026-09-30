package com.eventforge.task;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTaskRepository extends JpaRepository<EventTask, UUID> {
    Page<EventTask> findByEvent_IdAndEvent_Owner_Id(UUID eventId, UUID ownerId, Pageable pageable);
    Optional<EventTask> findByIdAndEvent_IdAndEvent_Owner_Id(UUID id, UUID eventId, UUID ownerId);
}
