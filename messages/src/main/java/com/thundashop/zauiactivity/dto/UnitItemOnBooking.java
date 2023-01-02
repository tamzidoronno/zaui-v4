package com.thundashop.zauiactivity.dto;

import lombok.Data;

@Data
public class UnitItemOnBooking {
    private String uuid;
    private String unitId;
    private Object resellerReference;
    private Object supplierReference;
    private Ticket ticket;
}
