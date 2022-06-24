package com.thundashop.core.availability.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Naim Murad (naim)
 * @since 6/15/22
 */
@Data
public class AvailabilityRequest implements Serializable {

    private LocalDate start;
    private LocalDate end;
    private long rooms;
    private long adults;
    private long children;
    private String discountCode;

    public long getGuests() {
        return getAdults() + getChildren();
    }
}
