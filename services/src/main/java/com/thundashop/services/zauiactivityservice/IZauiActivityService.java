package com.thundashop.services.zauiactivityservice;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;


public interface IZauiActivityService {
    ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo);

    void importZauiActivities(Integer supplierId, SessionInfo sessionInfo) throws ZauiException;

    void setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo);

    void addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking) throws ZauiException;
}
