package com.thundashop.core.availability.dto;

import com.thundashop.core.pmsmanager.BrowserVersion;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Naim Murad (naim)
 * @since 6/15/22
 */
@Data
public class AvailabilityRequest implements Serializable {

    private Date start;
    private Date end;
    private int rooms;
    private int adults;
    private int children;
    private String discountCode;
    private String language;
    public BrowserVersion browser;

    public long getGuests() {
        return getAdults() + getChildren();
    }

    public int getNumberOfDays() {
        return PmsBookingRooms.getNumberOfDays(start, end);
    }
}
