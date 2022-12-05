package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.List;

@Data
public class OctoBookingConfirm {
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
    private List<Voucher> voucher = null;
    private List<UnitItem> unitItems = null;
    private Cancellation cancellation;
    private Pricing pricing;
}

class Voucher {
    private String redemptionMethod;
    private List<DeliveryOption> deliveryOptions = null;
    private String utcDeliveredAt;
    private String utcRedeemedAt;
}

class UnitItem {
    private String unitId;
}
