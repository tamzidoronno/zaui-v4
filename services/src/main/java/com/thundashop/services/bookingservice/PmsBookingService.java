package com.thundashop.services.bookingservice;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class PmsBookingService implements IPmsBookingService{
    private final IPmsBookingRepository pmsBookingRepository;

    @Override
    public PmsBooking getPmsBookingById(String id, SessionInfo sessionInfo) {
        return pmsBookingRepository.getPmsBookingById(id, sessionInfo);
    }

    @Override
    public List<PmsBooking> getGotoBookings(SessionInfo sessionInfo) {
        return pmsBookingRepository.getGotoBookings(sessionInfo);
    }
}
