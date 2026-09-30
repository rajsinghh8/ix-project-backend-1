package com.eventforge.budget;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, UUID> {
    List<BudgetCategory> findByEventIdOrderByNameAsc(UUID eventId);

    @Query("select coalesce(sum(c.plannedAmount), 0) from BudgetCategory c where c.event.id = :eventId")
    BigDecimal totalPlannedForEvent(@Param("eventId") UUID eventId);
    @org.springframework.data.jpa.repository.Modifying @Query("delete from BudgetCategory c where c.event.id = :eventId") void deleteByEventId(@Param("eventId") UUID eventId);
}
