package com.eventforge.vendor;
import com.eventforge.domain.User; import jakarta.validation.Valid; import java.util.UUID; import org.springframework.data.domain.*; import org.springframework.http.*; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/events/{eventId}/vendors") public class VendorController{
 private final VendorService service; public VendorController(VendorService service){this.service=service;}
 @GetMapping public Page<VendorDto.Response> list(@PathVariable UUID eventId,@AuthenticationPrincipal User user,Pageable pageable){return service.list(eventId,user.getId(),pageable);}
 @PostMapping public ResponseEntity<VendorDto.Response> create(@PathVariable UUID eventId,@AuthenticationPrincipal User user,@Valid @RequestBody VendorDto.Request r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(eventId,user.getId(),r));}
 @PutMapping("/{vendorId}") public VendorDto.Response update(@PathVariable UUID eventId,@PathVariable UUID vendorId,@AuthenticationPrincipal User user,@Valid @RequestBody VendorDto.Request r){return service.update(eventId,vendorId,user.getId(),r);}
 @DeleteMapping("/{vendorId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID eventId,@PathVariable UUID vendorId,@AuthenticationPrincipal User user){service.delete(eventId,vendorId,user.getId());}
 @GetMapping("/{vendorId}/payments") public Page<VendorDto.PaymentResponse> payments(@PathVariable UUID eventId,@PathVariable UUID vendorId,@AuthenticationPrincipal User user,Pageable pageable){return service.listPayments(eventId,vendorId,user.getId(),pageable);}
 @PostMapping("/{vendorId}/payments") public ResponseEntity<VendorDto.PaymentResponse> pay(@PathVariable UUID eventId,@PathVariable UUID vendorId,@AuthenticationPrincipal User user,@Valid @RequestBody VendorDto.PaymentRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.createPayment(eventId,vendorId,user.getId(),r));}
 @DeleteMapping("/{vendorId}/payments/{paymentId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deletePayment(@PathVariable UUID eventId,@PathVariable UUID vendorId,@PathVariable UUID paymentId,@AuthenticationPrincipal User user){service.deletePayment(eventId,vendorId,paymentId,user.getId());}
 @GetMapping("/{vendorId}/payments/summary") public VendorDto.PaymentSummary summary(@PathVariable UUID eventId,@PathVariable UUID vendorId,@AuthenticationPrincipal User user){return service.summary(eventId,vendorId,user.getId());}
}
