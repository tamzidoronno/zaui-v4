package com.thundashop.zauiactivity.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddZauiActivityToWebBookingDto {
    private String optionId;
    private String availabilityId;
    private List<Unit> units;
    private String pmsBookingId;
}
