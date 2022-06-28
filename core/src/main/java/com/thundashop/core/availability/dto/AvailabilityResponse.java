package com.thundashop.core.availability.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author Naim Murad (naim)
 * @since 6/15/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityResponse {

    private Date start;
    private Date end;
    private long rooms;
    private long adults;
    private long children;
    private String discountCode;
    private List<Availability> availabilities;
}
