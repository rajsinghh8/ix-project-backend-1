package com.eventforge.vendor;
import io.swagger.v3.oas.annotations.Operation;
import com.eventforge.common.CurrentUser; import jakarta.validation.Valid; import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/events/{eventId}/vendors") public class VendorController{
 private final VendorService service; private final CurrentUser currentUser; public VendorController(VendorService service,CurrentUser currentUser){this.service=service;this.currentUser=currentUser;}
 @Operation(summary="List vendors") @GetMapping public Page<VendorDto.Response> list(@PathVariable UUID eventId,Pageable pageable){return service.list(eventId,currentUser.id(),pageable);}
 @PostMapping public ResponseEntity<VendorDto.Response> create(@PathVariable UUID eventId,@Valid @RequestBody VendorDto.Request r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId,currentUser.id(),r));}
 @PutMapping("/{vendorId}") public VendorDto.Response update(@PathVariable UUID eventId,@PathVariable UUID vendorId,@Valid @RequestBody VendorDto.Request r){return service.update(eventId,vendorId,currentUser.id(),r);}
 @DeleteMapping("/{vendorId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID eventId,@PathVariable UUID vendorId){service.delete(eventId,vendorId,currentUser.id());}
 @GetMapping("/{vendorId}/payments") public Page<VendorDto.PaymentResponse> payments(@PathVariable UUID eventId,@PathVariable UUID vendorId,Pageable pageable){return service.listPayments(eventId,vendorId,currentUser.id(),pageable);}
 @PostMapping("/{vendorId}/payments") public ResponseEntity<VendorDto.PaymentResponse> pay(@PathVariable UUID eventId,@PathVariable UUID vendorId,@Valid @RequestBody VendorDto.PaymentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createPayment(eventId,vendorId,currentUser.id(),r));}
 @DeleteMapping("/{vendorId}/payments/{paymentId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deletePayment(@PathVariable UUID eventId,@PathVariable UUID vendorId,@PathVariable UUID paymentId){service.deletePayment(eventId,vendorId,paymentId,currentUser.id());}
 @GetMapping("/{vendorId}/payments/summary") public VendorDto.PaymentSummary summary(@PathVariable UUID eventId,@PathVariable UUID vendorId){return service.summary(eventId,vendorId,currentUser.id());}
}
