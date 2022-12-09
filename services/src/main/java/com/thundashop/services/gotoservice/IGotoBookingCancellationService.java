package com.thundashop.services.gotoservice;

public interface IGotoBookingCancellationService {
    void notifyGotoAboutCancellation(String url, String authKey, String reservationId);
}
