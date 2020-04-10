/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConference;
import com.thundashop.core.pmsmanager.PmsConferenceEvent;
import com.thundashop.core.pmsmanager.PmsConferenceManager;
import com.thundashop.core.pmsmanager.PmsGuests;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsInvoiceManagerNew;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsRoomPaymentSummary;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@GetShopSession
@Component
public class PgaManager extends GetShopSessionBeanNamed implements IPgaManager {
    
    @Autowired
    private PmsManager pmsManager;
    
    @Autowired
    private PmsConferenceManager pmsConferenceManager;
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private BookingEngine bookingEngine;
    
    
    @Override
    public PmsConference getConference(String token) {
        PmsBooking booking = getBooking(token);
        
        if (booking != null) {
            return pmsConferenceManager.getConference(booking.conferenceId);
        }
        
        return null;
    }

    @Override
    public List<PmsBookingRooms> getRooms(String token) {
        PmsBooking booking = getBooking(token);
        
        if (booking == null)
            return new ArrayList();
        
        return booking.getAllRooms();
    }

    @Override
    public PmsBooking getBooking(String token) {
        PmsBooking booking = pmsManager.getAllBookingsFlat()
                .stream()
                .filter(o -> o.token.equals(token))
                .findAny()
                .orElse(null);
        
        if (booking == null) {
            PmsBookingRooms room = getRoom(token);
            if (room != null) {
                booking = pmsManager.getBookingFromRoomSecure(room.pmsBookingRoomId);
            }
        } 
        
        if (booking != null) {
            PmsBooking cloned = cloneIt(booking);
            if (!cloned.token.equals(token)) {
                cloned.rooms.removeIf(o -> !o.token.equals(token));
            }
            
            return cloned;
        }
        
        return null;
    }
    
    public <T> T cloneIt(T object) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(object);
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          
          T res = (T) ois.readObject();
          baos.close();
          oos.close();
          bais.close();
          ois.close();
          
          return res;
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
    }

    private PmsBookingRooms getRoom(String token) {
        return pmsManager.getAllBookingsFlat()
                .stream()
                .flatMap(o -> o.getAllRooms().stream())
                .filter(o -> o.token.equals(token))
                .findAny()
                .orElse(null);
    }

    @Override
    public User getUser(String token) {
        PmsBooking booking = getBooking(token);
        if (booking == null) {
            return null;
        }
        
        return userManager.getUserById(booking.userId);
    }
   
    @Override
    public List<PmsConferenceEvent> getEvents(String token) {
        PmsConference conference = getConference(token);
        
        if (conference == null) {
            return new ArrayList();
        }
        
        return pmsConferenceManager.getConferenceEvents(conference.id);
    }

    @Override
    public String getBookingItemTypeName(String bookingItemTypeId) {
        BookingItemType type = bookingEngine.getBookingItemType(bookingItemTypeId);
        
        if (type != null) {
            return type.name;
        }
        
        return "Unkown";
    }

    @Override
    public void updateGuests(String token, PgaBooking booking) {
        PmsBooking pmsBooking = getBooking(token);
        
        if (pmsBooking == null)
            return;
        
        PmsBooking bookingFromPmsManager = pmsManager.getBookingUnsecure(pmsBooking.id);
        PmsBookingRooms room = bookingFromPmsManager.getRoom(booking.pmsBookingRoomId);
        if (room != null) {
            String newguestinfo = "";
            
            for (PmsGuests guest : room.guests) {
                PmsGuests pgaGuest = booking.getGuest(guest.guestId);
                if (pgaGuest != null) {
                    guest.email = pgaGuest.email;
                    guest.name = pgaGuest.name;
                    guest.phone = pgaGuest.phone;
                    guest.prefix = pgaGuest.prefix;
                    guest.passportId = pgaGuest.passportId;
                    
                    
                    if (guest.name != null) {
                        newguestinfo += guest.name + " - ";
                    }
                    if (guest.email != null) {
                        newguestinfo += guest.email + " - ";
                    }
                    if (guest.prefix != null) {
                        newguestinfo += "+" + guest.prefix;
                    }
                    if (guest.phone != null) {
                        newguestinfo += guest.phone + " - ";
                    }
                    if (guest.passportId != null) {
                        newguestinfo += guest.passportId + " - ";
                    }
                    newguestinfo += "<br>";
                }
            }
            
            
            invoiceManager.saveBookingWithImpersonate(bookingFromPmsManager);
            
            boolean startedImpersonation = invoiceManager.startImpersonationOfSystemScheduler();
            pmsManager.logEntry("Guest has updated his information trough PGA link, new guest information: " + newguestinfo, pmsBooking.id, room.bookingItemId, room.pmsBookingRoomId, "pga");
            if (startedImpersonation) {
                invoiceManager.stopImpersonation();
            }
        }
    }
    
    
    @Autowired
    public PmsInvoiceManager invoiceManager;
    
    @Override
    public List<PmsRoomPaymentSummary> getSummaries(String token) {
        
        PmsBooking booking = getBooking(token);
        
        if (booking == null) {
            return new ArrayList();
        }
    
        List<PmsRoomPaymentSummary> summaries = new ArrayList();
        
        boolean startedImpersonation = invoiceManager.startImpersonationOfSystemScheduler();
        
        PmsBooking bookingFromPmsManager = pmsManager.getBooking(booking.id);
        
        bookingFromPmsManager.rooms.stream().forEach(room -> {
            if (!hasRoom(room.pmsBookingRoomId, booking)) {
                return;
            }
            
            invoiceManager.deleteOrCreditExistingOrders(bookingFromPmsManager, room);
            PmsRoomPaymentSummary summary = pmsManager.getSummary(booking.id, room.pmsBookingRoomId);
            summaries.add(summary);
        });
        
        pmsManager.saveBooking(bookingFromPmsManager);
        
        if (startedImpersonation) {
            invoiceManager.stopImpersonation();
        }
        
        return summaries;
    }

    private boolean hasRoom(String pmsBookingRoomId, PmsBooking booking) {
        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                return true;
            }
        }
        
        return false;
    }
}