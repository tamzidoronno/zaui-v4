package com.thundashop.services.gotoservice;

import com.thundashop.core.gotohub.dto.GotoBookingRequest;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoHoldBookingService {
    PmsBooking getBooking(GotoBookingRequest gotoBooking, PmsBooking pmsBooking, PmsConfiguration config,
                          SessionInfo zauiActivityManagerSession) throws Exception;
}
