package com.thundashop.services.bookingservice;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;

import java.util.List;

public interface IPmsBookingService {
    PmsBooking getPmsBookingById(String id, SessionInfo sessionInfo);

    List<PmsBooking> getGotoBookings(SessionInfo sessionInfo);

    PmsBooking getPmsBookingByZauiActivityItemId(String activityItemId, SessionInfo sessionInfo);

    PmsBooking getPmsBookingByAddonId(String addonId, SessionInfo sessionInfo);
}
