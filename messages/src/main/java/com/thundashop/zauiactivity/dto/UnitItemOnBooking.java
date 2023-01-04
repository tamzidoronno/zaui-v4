package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class UnitItemOnBooking {
    private String uuid;
    private String unitId;
    private String resellerReference;
    private String supplierReference;
    private Ticket ticket;
}
