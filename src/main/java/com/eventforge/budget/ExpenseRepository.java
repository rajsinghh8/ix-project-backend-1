package com.eventforge.budget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
 @Query("select e from Expense e where e.event.id = :eventId " +
        "and (:categoryId is null or e.category.id = :categoryId) " +
        "and (:search is null or lower(e.description) like lower(concat('%', :search, '%'))) " +
        "and (:dateFrom is null or e.expenseDate >= :dateFrom) " +
        "and (:dateTo is null or e.expenseDate <= :dateTo)")
 Page<Expense> findFiltered(@Param("eventId") UUID eventId, @Param("categoryId") UUID categoryId,
                            @Param("search") String search, @Param("dateFrom") LocalDate dateFrom,
                            @Param("dateTo") LocalDate dateTo, Pageable pageable);
 @Query("select coalesce(sum(e.amount), 0) from Expense e where e.event.id = :eventId")
 BigDecimal totalForEvent(@Param("eventId") UUID eventId);
 @org.springframework.data.jpa.repository.Modifying @Query("delete from Expense e where e.event.id = :eventId") void deleteByEventId(@Param("eventId") UUID eventId);
}
