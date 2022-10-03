package com.thundashop.core.pmsmanager;

import org.springframework.beans.BeanUtils;

public class PmsBookingWithConferenceDto extends PmsBooking{
    public PmsConferenceWithEvents conference;

    public PmsBookingWithConferenceDto(PmsBooking booking){
        BeanUtils.copyProperties(booking, this);
    }
}