package com.thundashop.services.zauiactivityservice;

import java.util.List;
import java.util.Optional;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

public interface IZauiActivityService {
    ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) throws NotUniqueDataException;

    ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo);

    Optional<ZauiActivity> getZauiActivityById(String Id, SessionInfo sessionInfo);

    void fetchZauiActivities(SessionInfo sessionInfo, ZauiActivityConfig zauiActivityConfig, String currency);

    List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) throws ZauiException;

    PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker) throws ZauiException;

    void cancelActivityFromBooking(BookingZauiActivityItem activityItem) throws ZauiException;

    Optional<BookingZauiActivityItem>getBookingZauiActivityItemByAddonId(String addonId, SessionInfo sessionInfo);
}
