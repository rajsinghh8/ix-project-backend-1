package com.eventforge.vendor;

import com.eventforge.common.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events/{eventId}/vendors")
public class VendorController {
    private final VendorService service;
    private final CurrentUser currentUser;

    public VendorController(VendorService service, CurrentUser currentUser) {
        this.service = service;
        this.currentUser = currentUser;
    }

    @Operation(summary = "List vendors", description = "Each vendor includes paidAmount and outstandingAmount aggregated from its payments.")
    @GetMapping
    public Page<VendorDto.Response> list(@PathVariable UUID eventId, Pageable pageable) {
        return service.list(eventId, currentUser.id(), pageable);
    }

    @PostMapping
    public ResponseEntity<VendorDto.Response> create(@PathVariable UUID eventId, @Valid @RequestBody VendorDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId, currentUser.id(), request));
    }

    @PutMapping("/{vendorId}")
    public VendorDto.Response update(@PathVariable UUID eventId, @PathVariable UUID vendorId,
                                     @Valid @RequestBody VendorDto.Request request) {
        return service.update(eventId, vendorId, currentUser.id(), request);
    }

    @DeleteMapping("/{vendorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID eventId, @PathVariable UUID vendorId) {
        service.delete(eventId, vendorId, currentUser.id());
    }

    @GetMapping("/{vendorId}/payments")
    public Page<VendorDto.PaymentResponse> payments(@PathVariable UUID eventId, @PathVariable UUID vendorId, Pageable pageable) {
        return service.listPayments(eventId, vendorId, currentUser.id(), pageable);
    }

    @PostMapping("/{vendorId}/payments")
    public ResponseEntity<VendorDto.PaymentResponse> pay(@PathVariable UUID eventId, @PathVariable UUID vendorId,
                                                          @Valid @RequestBody VendorDto.PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPayment(eventId, vendorId, currentUser.id(), request));
    }

    @DeleteMapping("/{vendorId}/payments/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable UUID eventId, @PathVariable UUID vendorId, @PathVariable UUID paymentId) {
        service.deletePayment(eventId, vendorId, paymentId, currentUser.id());
    }

    @GetMapping("/{vendorId}/payments/summary")
    public VendorDto.PaymentSummary summary(@PathVariable UUID eventId, @PathVariable UUID vendorId) {
        return service.summary(eventId, vendorId, currentUser.id());
    }
}
