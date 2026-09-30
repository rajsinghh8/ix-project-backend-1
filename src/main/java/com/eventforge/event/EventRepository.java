package com.eventforge.event;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("""
            select e from Event e
            where e.owner.id = :ownerId
              and (:search is null or lower(e.name) like lower(concat('%', :search, '%'))
                   or lower(e.venue) like lower(concat('%', :search, '%'))
                   or lower(coalesce(e.description, '')) like lower(concat('%', :search, '%')))
              and (:status is null or e.status = :status)
              and (:dateFrom is null or e.eventDate >= :dateFrom)
              and (:dateTo is null or e.eventDate <= :dateTo)
            """)
    Page<Event> searchOwned(@Param("ownerId") UUID ownerId, @Param("search") String search,
                            @Param("status") EventStatus status, @Param("dateFrom") LocalDate dateFrom,
                            @Param("dateTo") LocalDate dateTo, Pageable pageable);

    Optional<Event> findByIdAndOwner_Id(UUID id, UUID ownerId);
}
