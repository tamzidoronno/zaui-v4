package com.thundashop.repository.pmsmanager;

import java.util.Optional;

import com.thundashop.core.pmsmanager.ConferenceData;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IConferenceDataRepository extends IRepository<ConferenceData> {
    Optional<ConferenceData> findByBookingId(String bookingId, SessionInfo sessionInfo);    
}
