package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.List;

public class PmsBookingWithConferenceDto{
    public PmsBooking booking;
    public PmsConference conference;
    public List<PmsConferenceEvent> events = new ArrayList<>();

    public PmsBookingWithConferenceDto(){}

    public PmsBookingWithConferenceDto(PmsBooking booking, PmsConference conference, List<PmsConferenceEvent> events){
        this.booking = booking;
        this.conference = conference;
        this.events = events;
    }
}