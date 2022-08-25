package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.repository.pmsmanager.PmsBookingRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@GetShopSession
public class PmsBookingManager extends ManagerBase implements IPmsBookingManager {

    private final PmsBookingRepository repository;
    private final StoreManager storeManager;

    @Autowired
    public PmsBookingManager(PmsBookingRepository repository, StoreManager storeManager) {
        this.repository = repository;
        this.storeManager = storeManager;
    }

    @Override
    public PmsBooking save(PmsBooking booking) {
        return repository.save(booking, getSessionInfo());
    }

    @Override
    public Optional<PmsBooking> findBySessionId(String sessionId) {
        return repository.findBySessionId(sessionId, getSessionInfo());
    }

    @Override
    public PmsBooking findByBookingId(String bookingId) {
        return repository.findById(bookingId, getSessionInfo()).orElse(null);
    }

    @Override
    public PmsBooking findByPmsBookingRoomId(String pmsBookingRoomId) {
        throw new UnsupportedOperationException("findByPmsBookingRoomId");
    }

    @Override
    public Pair<PmsBooking, PmsBookingRooms> addBookingItemType(String bookingId, String type, Date start, Date end, String guestInfoFromRoom,
                                                                boolean addToBookingEngine) {
        PmsBooking booking = findByBookingId(bookingId);

        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = type;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;

        PmsGuests guest = new PmsGuests();
        guest.prefix = storeManager.getPrefix();
        guest.name = "";

        room.guests.add(guest);
        booking.rooms.add(room);

        return Pair.of(booking, room);
    }
}
