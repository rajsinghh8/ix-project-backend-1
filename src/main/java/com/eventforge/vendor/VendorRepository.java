package com.eventforge.vendor;
import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*;
public interface VendorRepository extends JpaRepository<Vendor,UUID>{@Query("select v from Vendor v where v.event.id=:eventId and v.event.owner.id=:ownerId") Page<Vendor> findAllOwned(UUID eventId,UUID ownerId,Pageable pageable); @Modifying @Query("delete from Vendor v where v.event.id=:eventId") void deleteByEventId(@org.springframework.data.repository.query.Param("eventId") UUID eventId);}
