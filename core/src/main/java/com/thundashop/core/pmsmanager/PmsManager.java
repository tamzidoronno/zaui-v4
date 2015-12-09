package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.messagemanager.MessageManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsManager extends GetShopSessionBeanNamed implements IPmsManager {

    public HashMap<String, PmsBooking> bookings = new HashMap();
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    MessageManager messageManager;

    @Override
    public List<Room> getAllRoomTypes(long start, long end) {
        List<Room> result = new ArrayList();
        List<BookingItemType> allGroups = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : allGroups) {
            Room room = new Room();
            room.type = type;
            room.price = 1.0;
            result.add(room);
        }
        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        PmsBooking result = bookings.get(getSession().id);
        if(result != null) {
            if(!result.id.equals(booking.id)) {
                throw new Exception("Invalid booking update");
            }
        }
        
        booking.sessionId = getSession().id;
        saveObject(booking);
        bookings.put(booking.sessionId, booking);
    }

    @Override
    public PmsBooking getCurrentBooking() {
        if(getSession() == null) {
            System.out.println("Warning, no session set yet");
        }
        PmsBooking result = bookings.get(getSession().id);
        if(result == null) {
            return startBooking();
        }
        return result;
    }

    @Override
    public PmsBooking startBooking() {
        PmsBooking currentBooking = bookings.get(getSession().id);
        
        bookings.remove(getSession().id);
        if(currentBooking != null) {
            deleteObject(currentBooking);
        }
        
        PmsBooking booking = new PmsBooking();
        
        PmsBookingDateRange range = new PmsBookingDateRange();
        range.start = new Date();
        booking.dates.add(range);
        
        try {
            setBooking(booking);
        } catch (Exception ex) {
            Logger.getLogger(PmsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booking;
    }

    /**
     * 1. Invalid
     */
    @Override
    public HashMap<String, Integer> validateCurrentBooking() {
        PmsBooking currentBooking = getCurrentBooking();
        HashMap<String,Integer> result = new HashMap();
        
        //First check if the contact data is fine.
        for(PmsBookingRooms room : currentBooking.rooms) {
            Integer offset = 0;
            for(PmsGuests guest : room.guests) {
                if(guest.name == null || !guest.name.contains(" ")) {
                    result.put("room_" + room.pmsBookingRoomId + "_" + offset + "_name", 1);
                }
                if(guest.email == null || !guest.email.contains("@")) {
                    result.put("room_" + room.pmsBookingRoomId + "_" + offset + "_email", 1);
                }

                HashMap<String, String> phoneNumber = validatePhone("+" + guest.prefix + guest.phone, "NO");
                if(guest.name == null || phoneNumber == null) {
                    result.put("room_" + room.pmsBookingRoomId + "_" + offset + "_phone", 1);
                } else {
                    guest.phone = phoneNumber.get("phone");
                    guest.prefix = phoneNumber.get("prefix");
                }
                offset++;
            }
        }
        
        //Validate the contact data
        if(currentBooking.contactData.city.isEmpty()) {
            result.put("contact_city", 1);
        }
        if(currentBooking.contactData.postalCode.isEmpty()) {
            result.put("contact_postalCode", 1);
        }
        if(currentBooking.contactData.email.isEmpty()) {
            result.put("contact_email", 1);
        }
        if(currentBooking.contactData.address.isEmpty()) {
            result.put("contact_address", 1);
        }
        
        if(currentBooking.contactData.type == 1) {
            if(currentBooking.contactData.birthday.isEmpty()) {
                result.put("contact_birthday", 1);
            }
        } else {
            if(currentBooking.contactData.name.isEmpty()) {
                result.put("contact_name", 1);
            }
            if(currentBooking.contactData.orgid.isEmpty()) {
                result.put("contact_orgid", 1);
            }
        }
        
        
        return result;
    }

    private HashMap<String, String> validatePhone(String phone, String countryCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String prefix = "";
        try {
            PhoneNumber phonecheck = phoneUtil.parse(phone, countryCode);
            if (!phoneUtil.isValidNumber(phonecheck)) {
                String phone2 = phone;
                if (phone.startsWith("0000")) {
                    phone2 = phone.substring(4);
                } else if (phone.startsWith("000")) {
                    phone2 = phone.substring(3);
                } else if (phone.startsWith("00")) {
                    phone2 = phone.substring(2);
                }

                phonecheck = phoneUtil.parse(phone2, countryCode);
                prefix = phonecheck.getCountryCode() + "";
                phone = phonecheck.getNationalNumber() + "";

                if (!phoneUtil.isValidNumber(phonecheck)) {
                    phone2 = "00" + phone;
                    phonecheck = phoneUtil.parse(phone2, countryCode);

                    if (!phoneUtil.isValidNumber(phonecheck)) {
                        if(phone.length() == 10 && phone.startsWith("07")) {
                            phone = phone.substring(1);
                            prefix = "46";
                        } else if (phone.length() == 9 && phone.startsWith("7")) {
                            prefix = "46";
                        } else {
                            return null;
                        }
                    } else {
                        prefix = phonecheck.getCountryCode() + "";
                        phone = phonecheck.getNationalNumber() + "";
                    }
                } else {
                    prefix = phonecheck.getCountryCode() + "";
                    phone = phonecheck.getNationalNumber() + "";
                }
            } else {
                prefix = phonecheck.getCountryCode() + "";
                phone = phonecheck.getNationalNumber() + "";
            }
        } catch (NumberParseException e) {
            return null;
        }

        
        HashMap<String,String> result = new HashMap();
        result.put("prefix", prefix);
        result.put("phone", phone);
        return result;
    }

    @Override
    public String completeCurrentBooking() {
        PmsBooking booking = getCurrentBooking();
        
        List<Booking> bookingsToAdd = new ArrayList();
        for(PmsBookingDateRange dates : booking.dates) {
            for(PmsBookingRooms room : booking.rooms) {
                System.out.println("TEST");
                Booking bookingToAdd = new Booking();
                bookingToAdd.startDate = dates.start;
                if(dates.end == null) {
                    dates.end = createInifinteDate();
                }
                bookingToAdd.endDate = dates.end;
                bookingToAdd.bookingItemTypeId = room.bookingItemTypeId;
                bookingsToAdd.add(bookingToAdd);
            }
        }
        try {
            bookingEngine.addBookings(bookingsToAdd);
        }catch(Exception e) {
            messageManager.sendErrorNotification("Unknown booking exception occured for booking id: " + booking.id);
        }
        
        return null;
    }

    private Date createInifinteDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 100);
        return cal.getTime();
    }
    
}
