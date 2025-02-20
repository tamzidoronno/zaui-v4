package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.repository.pmsmanager.IConferenceDataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;


@Component
@GetShopSession
public class ConferenceDataManager extends ManagerBase {

    private final IConferenceDataRepository conferenceDataRepository;
    private final UserManager userManager;

    @Autowired
    public ConferenceDataManager(IConferenceDataRepository conferenceDataRepository, UserManager userManager) {
        this.conferenceDataRepository = conferenceDataRepository;
        this.userManager = userManager;
    }

    public ConferenceData save(ConferenceData conferenceData) {
        return conferenceDataRepository.save(conferenceData, getSessionInfo());
    }

    public ConferenceData findByBookingId(String bookingId) {
        ConferenceData conferenceData = conferenceDataRepository.findByBookingId(bookingId, getStoreIdInfo())
                .orElse(new ConferenceData());
        conferenceData.bookingId = bookingId;
        return conferenceData;
    }

    public void finalize(ConferenceData data, PmsBooking booking) {
        // TODO remove booking param, use instead PmsBookingManager
        if (booking != null) {
            data.attendeesCount = booking.getTotalGuestCount();
            data.date = booking.getStartDate();
            if (booking.userId != null && !booking.userId.isEmpty() && userManager.getUserById(booking.bookedByUserId) != null) {
                data.nameOfEvent = userManager.getUserById(booking.userId).fullName;
            }
        }
        for(ConferenceDataDay day : data.days) {
            Collections.sort(day.conferences, new Comparator<ConferenceDataRow>() {
                @Override
                public int compare(ConferenceDataRow o1, ConferenceDataRow o2) {
                    if(o1 == null || o2 == null || o1.from == null || o2.from == null) {
                        return 0;
                    }
                    return o1.from.compareTo(o2.from);
                }
            });

        }

    }
}
