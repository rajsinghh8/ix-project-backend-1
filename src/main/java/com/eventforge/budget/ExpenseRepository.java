package com.eventforge.budget;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface ExpenseRepository extends JpaRepository<Expense,UUID>{
 Page<Expense> findByEventId(UUID eventId, Pageable pageable);
 @Query("select coalesce(sum(e.amount), 0) from Expense e where e.event.id = :eventId") BigDecimal totalForEvent(@Param("eventId") UUID eventId);
}
