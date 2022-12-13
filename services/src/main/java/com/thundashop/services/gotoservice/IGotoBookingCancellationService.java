package com.thundashop.services.gotoservice;

import com.thundashop.core.pmsmanager.PmsConfiguration;

import java.util.Date;

public interface IGotoBookingCancellationService {
    String getCancellationDeadLine(String checkin, int cutoffHour, PmsConfiguration config) throws Exception;
    Date getCancellationDeadLine(Date checkin, int cutoffHour, PmsConfiguration config) throws Exception;

    void notifyGotoAboutCancellation(String url, String authKey, String reservationId) throws Exception;
}
