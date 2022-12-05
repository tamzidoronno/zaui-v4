package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.List;

@Data
public class OctoBookingReserve {
    private String id;
    private String uuid;
    private String supplierReference;
    private String status;
    private String resellerReference;
    private String utcCreatedAt;
    private String utcUpdatedAt;
    private String utcExpiresAt;
    private String utcRedeemedAt;
    private String utcConfirmedAt;
    private String productId;
    private String freesale;
    private String availabilityId;
    private String notes;
    private String optionId;
    private OctoProductAvailability availability;
    private Contact contact;
    private List<String> deliveryMethods = null;
    private List<String> voucher = null;
    private List<UnitItemReserveResponse> unitItems = null;
    private Cancellation cancellation;
}

class UnitItemReserveResponse {
    private String uuid;
    private String unitId;
    private Object resellerReference;
    private Object supplierReference;
    private Ticket ticket;
}
class Contact {
    private String fullName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private List<Object> locales = null;
    private String country;
    private String notes;
}
class DeliveryOption {
    private String deliveryFormat;
    private String deliveryValue;
}

class Ticket {
    private String redemptionMethod;
    private String utcDeliveredAt;
    private String utcRedeemedAt;
    private List<DeliveryOption> deliveryOptions = null;
}
class Cancellation {
    private Boolean cancelled;
    private String utcCancelledAt;
}

