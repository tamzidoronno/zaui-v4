package com.thundashop.repository.pmsmanager;

import java.util.Optional;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.repository.baserepository.IRepository;
import com.thundashop.repository.utils.SessionInfo;

public interface IPmsBookingRepository extends IRepository<PmsBooking>{
    Optional<PmsBooking> findBySessionId(String sessionId, SessionInfo sessionInfo);
    Optional<PmsBooking> findByPmsBookingRoomId(String pmsBookingRoomId, SessionInfo sessionInfo);
}
