package com.thundashop.zauiactivity.dto;

import java.util.List;

import lombok.Data;

@Data
public class OctoBooking {
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
    private List<Ticket> voucher = null;
    private List<UnitItemOnBooking> unitItems = null;
    private Cancellation cancellation;
    private Pricing pricing = null;
}

@Data
class DeliveryOption {
    private String deliveryFormat;
    private String deliveryValue;
}

@Data
class Ticket {
    private String redemptionMethod;
    private String utcDeliveredAt;
    private String utcRedeemedAt;
    private List<DeliveryOption> deliveryOptions = null;
}

@Data
class Cancellation {
    private Boolean cancelled;
    private String utcCancelledAt;
}

