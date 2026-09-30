package com.eventforge.vendor;

import com.eventforge.activity.EventActivityService;
import com.eventforge.event.Event;
import com.eventforge.event.EventRepository;
import com.eventforge.notification.NotificationService;
import com.eventforge.notification.NotificationType;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class VendorService {
    private final VendorRepository vendors;
    private final VendorPaymentRepository payments;
    private final EventRepository events;
    private final EventActivityService activities;
    private final NotificationService notifications;

    public VendorService(VendorRepository vendors, VendorPaymentRepository payments, EventRepository events,
                         EventActivityService activities, NotificationService notifications) {
        this.vendors = vendors;
        this.payments = payments;
        this.events = events;
        this.activities = activities;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public Page<VendorDto.Response> list(UUID eventId, UUID owner, Pageable page) {
        return vendors.findAllOwned(eventId, owner, page)
                .map(vendor -> response(vendor, payments.paidTotal(vendor.getId())));
    }

    public VendorDto.Response create(UUID eventId, UUID owner, VendorDto.Request request) {
        Vendor vendor = vendors.save(new Vendor(event(eventId, owner), request.name(), request.serviceType(),
                request.cost(), request.contactDetails(), request.bookingStatus()));
        record(vendor.getEvent(), "Vendor created", vendor.getName(), NotificationType.VENDOR);
        return response(vendor, BigDecimal.ZERO);
    }

    public VendorDto.Response update(UUID eventId, UUID id, UUID owner, VendorDto.Request request) {
        Vendor vendor = vendor(eventId, id, owner);
        vendor.update(request.name(), request.serviceType(), request.cost(), request.contactDetails(), request.bookingStatus());
        record(vendor.getEvent(), "Vendor updated", vendor.getName(), NotificationType.VENDOR);
        return response(vendor, payments.paidTotal(vendor.getId()));
    }

    public void delete(UUID eventId, UUID id, UUID owner) {
        Vendor vendor = vendor(eventId, id, owner);
        Event event = vendor.getEvent();
        String name = vendor.getName();
        payments.deleteByVendorId(vendor.getId());
        vendors.delete(vendor);
        record(event, "Vendor deleted", name, NotificationType.VENDOR);
    }

    @Transactional(readOnly = true)
    public Page<VendorDto.PaymentResponse> listPayments(UUID eventId, UUID vendorId, UUID owner, Pageable page) {
        vendor(eventId, vendorId, owner);
        return payments.findAllOwned(vendorId, owner, page).map(VendorDto.PaymentResponse::from);
    }

    public VendorDto.PaymentResponse createPayment(UUID eventId, UUID vendorId, UUID owner, VendorDto.PaymentRequest request) {
        Vendor vendor = vendor(eventId, vendorId, owner);
        VendorPayment payment = payments.save(new VendorPayment(vendor, request.amount(), request.paidAt(), request.note()));
        record(vendor.getEvent(), "Vendor payment created", vendor.getName(), NotificationType.PAYMENT);
        return VendorDto.PaymentResponse.from(payment);
    }

    public void deletePayment(UUID eventId, UUID vendorId, UUID paymentId, UUID owner) {
        Vendor vendor = vendor(eventId, vendorId, owner);
        VendorPayment payment = payments.findById(paymentId)
                .filter(candidate -> candidate.getVendor().getId().equals(vendorId)
                        && candidate.getVendor().getEvent().getOwner().getId().equals(owner))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        payments.delete(payment);
        record(vendor.getEvent(), "Vendor payment deleted", vendor.getName(), NotificationType.PAYMENT);
    }

    @Transactional(readOnly = true)
    public VendorDto.PaymentSummary summary(UUID eventId, UUID vendorId, UUID owner) {
        Vendor vendor = vendor(eventId, vendorId, owner);
        BigDecimal paid = payments.paidTotal(vendorId);
        return new VendorDto.PaymentSummary(vendorId, vendor.getCost(), paid,
                vendor.getCost().subtract(paid).max(BigDecimal.ZERO), payments.countByVendorId(vendorId));
    }

    private VendorDto.Response response(Vendor vendor, BigDecimal paidAmount) {
        return VendorDto.Response.from(vendor, paidAmount);
    }

    private void record(Event event, String action, String details, NotificationType type) {
        activities.recordActivity(event, event.getOwner(), action, details);
        notifications.recordForEventOwner(event, type, action + ": " + details);
    }

    private Event event(UUID id, UUID owner) {
        return events.findById(id).filter(event -> event.getOwner().getId().equals(owner))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    private Vendor vendor(UUID eventId, UUID id, UUID owner) {
        return vendors.findById(id)
                .filter(vendor -> vendor.getEvent().getId().equals(eventId)
                        && vendor.getEvent().getOwner().getId().equals(owner))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
    }
}
