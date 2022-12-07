package com.thundashop.services.gotoservice;

import java.util.List;

import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.LongTermDeal;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.utils.SessionInfo;

public interface IGotoService {
    GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo);

    List<PmsBooking> getUnpaidGotoBookings(int autoDeletionTime, SessionInfo sessionInfo);

    List<LongTermDeal> getLongTermDeals(SessionInfo sessionInfo);
}
