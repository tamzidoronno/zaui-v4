package com.thundashop.repository.pmsbookingrepository;

import java.util.List;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IPmsBookingRepository extends IRepository<PmsBooking> {
    List<PmsBooking> getGotoBookings(SessionInfo sessionInfo);
}
