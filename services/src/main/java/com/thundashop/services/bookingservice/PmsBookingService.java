package com.thundashop.services.bookingservice;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmsBookingService implements IPmsBookingService{
    private final IPmsBookingRepository pmsBookingRepository;
    @Autowired
    public PmsBookingService(IPmsBookingRepository pmsBookingRepository){
        this.pmsBookingRepository = pmsBookingRepository;
    }
    @Override
    public PmsBooking getPmsBookingById(String id, SessionInfo sessionInfo) {
        return pmsBookingRepository.getPmsBookingById(id, sessionInfo);
    }

    @Override
    public List<PmsBooking> getGotoBookings(SessionInfo sessionInfo) {
        return pmsBookingRepository.getGotoBookings(sessionInfo);
    }
}
