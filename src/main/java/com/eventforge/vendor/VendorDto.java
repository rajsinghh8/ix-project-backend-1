package com.eventforge.vendor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class VendorDto {
    private VendorDto() {
    }

    @Schema(description = "Vendor create or update payload")
    public record Request(
            @Schema(example = "Acme Catering") @NotBlank @Size(max = 160) String name,
            @Schema(example = "Catering") @NotBlank @Size(max = 100) String serviceType,
            @Schema(example = "2000.00") @NotNull @PositiveOrZero BigDecimal cost,
            @Schema(example = "Morgan Lee, morgan@acme.test, +15551234567") @NotBlank @Size(max = 1000) String contactDetails,
            @Schema(example = "CONFIRMED") @NotNull VendorBookingStatus bookingStatus) {
    }

    @Schema(description = "Vendor payment payload")
    public record PaymentRequest(
            @Schema(example = "500.00") @NotNull @Positive BigDecimal amount,
            @Schema(example = "2025-12-01T10:15:30Z") @NotNull Instant paidAt,
            @Schema(example = "Deposit") @Size(max = 1000) String note) {
    }

    public record Response(
            @Schema(example = "11111111-1111-1111-1111-111111111111") UUID id,
            UUID eventId,
            @Schema(example = "Acme Catering") String name,
            @Schema(example = "Catering") String serviceType,
            @Schema(example = "2000.00") BigDecimal cost,
            @Schema(example = "Morgan Lee, morgan@acme.test, +15551234567") String contactDetails,
            @Schema(example = "CONFIRMED") VendorBookingStatus bookingStatus,
            @Schema(example = "500.00") BigDecimal paidAmount,
            @Schema(example = "1500.00") BigDecimal outstandingAmount) {
        static Response from(Vendor vendor, BigDecimal paidAmount) {
            BigDecimal paid = paidAmount == null ? BigDecimal.ZERO : paidAmount;
            return new Response(vendor.getId(), vendor.getEvent().getId(), vendor.getName(),
                    vendor.getServiceType(), vendor.getCost(), vendor.getContactDetails(),
                    vendor.getBookingStatus(), paid, vendor.getCost().subtract(paid).max(BigDecimal.ZERO));
        }
    }

    public record PaymentResponse(
            @Schema(example = "11111111-1111-1111-1111-111111111111") UUID id,
            UUID vendorId,
            @Schema(example = "500.00") BigDecimal amount,
            @Schema(example = "2025-12-01T10:15:30Z") Instant paidAt,
            @Schema(example = "Deposit") String note) {
        static PaymentResponse from(VendorPayment payment) {
            return new PaymentResponse(payment.getId(), payment.getVendor().getId(), payment.getAmount(),
                    payment.getPaidAt(), payment.getNote());
        }
    }

    public record PaymentSummary(
            UUID vendorId,
            @Schema(example = "2000.00") BigDecimal cost,
            @Schema(example = "500.00") BigDecimal paidAmount,
            @Schema(example = "1500.00") BigDecimal outstandingAmount,
            @Schema(example = "1") long paymentCount) {
    }
}
