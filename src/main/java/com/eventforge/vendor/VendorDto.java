package com.eventforge.vendor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public final class VendorDto {
 private VendorDto() { }
 @Schema(description="Vendor create or update payload") public record Request(@Schema(example="Acme Catering") @NotBlank @Size(max=160) String name,@Schema(example="Catering") @Size(max=100) String category,@Schema(example="Morgan Lee") @Size(max=160) String contactName,@Schema(example="morgan@acme.test") @Email @Size(max=160) String email,@Schema(example="+15551234567") @Size(max=50) String phone,@Schema(example="2000.00") @PositiveOrZero BigDecimal contractedAmount) { }
 @Schema(description="Vendor payment payload") public record PaymentRequest(@Schema(example="500.00") @NotNull @Positive BigDecimal amount,@Schema(example="2025-12-01") @NotNull LocalDate paidOn,@Schema(example="Deposit") @Size(max=1000) String note) { }
 public record Response(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id,UUID eventId,@Schema(example="Acme Catering") String name,@Schema(example="Catering") String category,@Schema(example="Morgan Lee") String contactName,@Schema(example="morgan@acme.test") String email,@Schema(example="+15551234567") String phone,@Schema(example="2000.00") BigDecimal contractedAmount){static Response from(Vendor v){return new Response(v.getId(),v.getEvent().getId(),v.getName(),v.getCategory(),v.getContactName(),v.getEmail(),v.getPhone(),v.getContractedAmount());}}
 public record PaymentResponse(@Schema(example="11111111-1111-1111-1111-111111111111") UUID id,UUID vendorId,@Schema(example="500.00") BigDecimal amount,@Schema(example="2025-12-01") LocalDate paidOn,@Schema(example="Deposit") String note){static PaymentResponse from(VendorPayment p){return new PaymentResponse(p.getId(),p.getVendor().getId(),p.getAmount(),p.getPaidOn(),p.getNote());}}
 public record PaymentSummary(UUID vendorId,@Schema(example="2000.00") BigDecimal contractedAmount,@Schema(example="500.00") BigDecimal paidAmount,@Schema(example="1500.00") BigDecimal outstandingAmount,@Schema(example="1") long paymentCount) { }
}
