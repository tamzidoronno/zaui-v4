package com.thundashop.core.availability.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Naim Murad (naim)
 * @since 6/15/22
 */
@Data
public class AvailabilityResponse {

    private LocalDate start;
    private LocalDate end;
    private long rooms;
    private long adults;
    private long children;
    private String discountCode;
    private List<Availability> availabilities;
}
