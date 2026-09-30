package com.eventforge.vendor;
import java.math.BigDecimal; import java.util.*; import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*;
public interface VendorPaymentRepository extends JpaRepository<VendorPayment,UUID>{
 @Query("select p from VendorPayment p where p.vendor.id=:vendorId and p.vendor.event.owner.id=:ownerId") Page<VendorPayment> findAllOwned(UUID vendorId,UUID ownerId,Pageable pageable);
 @Query("select coalesce(sum(p.amount),0) from VendorPayment p where p.vendor.id=:vendorId") BigDecimal paidTotal(UUID vendorId);
 long countByVendorId(UUID vendorId);
 @Modifying @Query("delete from VendorPayment p where p.vendor.id=:vendorId") void deleteByVendorId(UUID vendorId);
 @Modifying @Query("delete from VendorPayment p where p.vendor.event.id=:eventId") void deleteByEventId(@org.springframework.data.repository.query.Param("eventId") UUID eventId);
}
