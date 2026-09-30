package com.eventforge.budget;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory,UUID>{ List<BudgetCategory> findByEventIdOrderByNameAsc(UUID eventId); }
