package com.thundashop.repository.pmsbookingrepository;

import java.util.List;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IPmsBookingRepository extends IRepository<PmsBooking> {
    PmsBooking getPmsBookingById(String id, SessionInfo sessionInfo);
    List<PmsBooking> getGotoBookings(SessionInfo sessionInfo);

    PmsBooking getPmsBookingByAddonId(String addonId, SessionInfo sessionInfo);

    PmsBooking getPmsBookingByZauiActivityItemId(String activityItemId, SessionInfo sessionInfo);
}
