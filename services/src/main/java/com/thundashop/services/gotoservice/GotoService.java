package com.thundashop.services.gotoservice;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thundashop.services.bookingservice.IPmsBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.LongTermDeal;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.repository.gotohubrepository.IGotoConfigRepository;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.pmspricing.IPmsPricingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GotoService implements IGotoService {
    @Autowired
    private IPmsPricingService pmsPricingService;

    @Autowired
    private IGotoConfigRepository gotoConfigRepository;

    @Autowired
    private IPmsBookingService pmsBookingService;

    @Override
    public GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo) {
        try {
            return gotoConfigRepository.getGotoConfiguration(sessionInfo);
        } catch (Exception ex) {
            log.error("Failed to get gotohub configuration. Actual exception: {}", ex);
            return null;
        }
    }

    @Override
    public List<LongTermDeal> getLongTermDeals(SessionInfo sessionInfo) {
        PmsPricing pricing = pmsPricingService.getByCodeOrDefaultCode("", sessionInfo);
        return pricing.longTermDeal.keySet()
                .stream()
                .filter(Objects::nonNull)
                .map(numOfDays -> new LongTermDeal(numOfDays, pricing.longTermDeal.get(numOfDays)))
                .sorted(Comparator.comparingInt(LongTermDeal::getNumbOfDays))
                .collect(Collectors.toList());
    }

    @Override
    public List<PmsBooking> getUnpaidGotoBookings(int autoDeletionTime, SessionInfo sessionInfo) {
        return pmsBookingService.getGotoBookings(sessionInfo).stream()
                .filter(booking -> booking.getActiveRooms() != null && !booking.getActiveRooms().isEmpty())
                .filter(booking -> (booking.orderIds == null || booking.orderIds.isEmpty())
                        && booking.isOlderThan(autoDeletionTime))
                .collect(Collectors.toList());
    }
}
