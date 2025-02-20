package com.thundashop.services.zauiactivityservice;

import java.util.List;
import java.util.Optional;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsOrderCreateRow;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.repository.exceptions.NotUniqueDataException;
import com.thundashop.core.common.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.zauiactivity.dto.*;

public interface IZauiActivityService {
    ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) throws NotUniqueDataException;

    ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo);

    Optional<ZauiActivity> getZauiActivityById(String Id, SessionInfo sessionInfo);

    void fetchZauiActivities(SessionInfo sessionInfo, ZauiActivityConfig zauiActivityConfig, String currency);

    ZauiActivity getZauiActivityByOptionId(String optionId, SessionInfo sessionInfo);

    List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) throws ZauiException;

    PmsBooking addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker,
            ZauiActivityConfig config, SessionInfo sessionInfo) throws ZauiException;

    PmsBooking addActivityToPmsBooking(BookingZauiActivityItem activityItem, OctoBooking octoBooking,
            PmsBooking booking, SessionInfo sessionInfo) throws ZauiException;

    OctoBooking confirmOctoBooking(BookingZauiActivityItem activityItem, PmsBooking booking, User booker,
            ZauiActivityConfig config)
            throws ZauiException;

    void cancelActivityFromBooking(BookingZauiActivityItem activityItem, ZauiActivityConfig config)
            throws ZauiException;

    void cancelAllActivitiesFromBooking(PmsBooking booking, ZauiActivityConfig config);

    Optional<BookingZauiActivityItem> getBookingZauiActivityItemByAddonId(String addonId, SessionInfo sessionInfo);

    double getPrecisedPrice(double price, Integer precision);

    List<UnitItemReserveRequest> mapUnitsForBooking(List<Unit> units);

    PmsBooking addActivityToWebBooking(AddZauiActivityToWebBookingDto activity, PmsBooking booking,
            ZauiActivityConfig config, SessionInfo sessionInfo) throws ZauiException;

    BookingZauiActivityItem mapActivityToBookingZauiActivityItem(OctoBooking octoBooking, SessionInfo sessionInfo)
            throws ZauiException;

    PmsOrderCreateRow createOrderCreateRowForZauiActivities(List<BookingZauiActivityItem> activityItems);

    PmsBooking removeActivityFromBooking(String activityItemId, PmsBooking booking);

    boolean isAllActivityCancelled(List<BookingZauiActivityItem> activities);

    void restrictGoToBookingWithActivities(PmsBooking booking) throws ZauiException;
}
