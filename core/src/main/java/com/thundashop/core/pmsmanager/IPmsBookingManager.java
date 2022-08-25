package com.thundashop.core.pmsmanager;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.Optional;

public interface IPmsBookingManager {

    PmsBooking save(PmsBooking booking);

    Optional<PmsBooking> findBySessionId(String sessionId);

    PmsBooking findByBookingId(String bookingId);

    PmsBooking findByPmsBookingRoomId(String pmsBookingRoomId);

    Pair<PmsBooking, PmsBookingRooms> addBookingItemType(String bookingId, String type, Date start, Date end, String guestInfoFromRoom,
                                                         boolean addToBookingEngine);

}
