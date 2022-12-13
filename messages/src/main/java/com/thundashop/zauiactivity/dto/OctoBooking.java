package com.thundashop.zauiactivity.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
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
    private List<String> voucher = null;
    private List<UnitItemOnBooking> unitItems = null;
    private Cancellation cancellation;
    private Pricing pricing = null;
}

@Data
class UnitItemOnBooking {
    private String uuid;
    private String unitId;
    private Object resellerReference;
    private Object supplierReference;
    private Ticket ticket;
}

@Data
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

