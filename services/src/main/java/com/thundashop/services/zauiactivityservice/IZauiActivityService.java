package com.thundashop.services.zauiactivityservice;

import com.thundashop.core.pmsbookingprocess.BookerInformation;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.BookingZauiActivityItem;
import com.thundashop.zauiactivity.dto.OctoBooking;
import com.thundashop.zauiactivity.dto.ZauiActivity;
import com.thundashop.zauiactivity.dto.ZauiActivityConfig;

import java.util.List;
import java.util.Optional;

public interface IZauiActivityService {
    ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo);

    ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo);

    Optional<ZauiActivity> getZauiActivityById(String Id, SessionInfo sessionInfo);

    void fetchZauiActivities(SessionInfo sessionInfo, String currency) throws ZauiException;

    List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) throws ZauiException;

    PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker) throws ZauiException;

    void cancelActivityFromBooking(BookingZauiActivityItem activityItem) throws ZauiException;

    Optional<BookingZauiActivityItem>getBookingZauiActivityItemByAddonId(String addonId, SessionInfo sessionInfo);
}
