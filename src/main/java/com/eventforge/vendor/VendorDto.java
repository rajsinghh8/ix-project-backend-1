package com.eventforge.vendor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public final class VendorDto {
 private VendorDto(){}
 public record Request(@NotBlank @Size(max=160) String name,@Size(max=100) String category,@Size(max=160) String contactName,@Email @Size(max=160) String email,@Size(max=50) String phone,@PositiveOrZero BigDecimal contractedAmount){}
 public record PaymentRequest(@NotNull @Positive BigDecimal amount,@NotNull LocalDate paidOn,@Size(max=1000) String note){}
 public record Response(UUID id,UUID eventId,String name,String category,String contactName,String email,String phone,BigDecimal contractedAmount){static Response from(Vendor v){return new Response(v.getId(),v.getEvent().getId(),v.getName(),v.getCategory(),v.getContactName(),v.getEmail(),v.getPhone(),v.getContractedAmount());}}
 public record PaymentResponse(UUID id,UUID vendorId,BigDecimal amount,LocalDate paidOn,String note){static PaymentResponse from(VendorPayment p){return new PaymentResponse(p.getId(),p.getVendor().getId(),p.getAmount(),p.getPaidOn(),p.getNote());}}
 public record PaymentSummary(UUID vendorId,BigDecimal contractedAmount,BigDecimal paidAmount,BigDecimal outstandingAmount,long paymentCount){}
}
