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
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pkkcontrol.PkkControlData;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import static java.lang.Math.random;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
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
    public PmsPricing prices = new PmsPricing();
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    MessageManager messageManager;

    @Autowired
    UserManager userManager;
         
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking) dataCommon;
                bookings.put(booking.id, booking);
            }
            if (dataCommon instanceof PmsPricing) {
                prices = (PmsPricing) dataCommon;
            }
        }
    }

    @Override
    public void setSession(Session session) {
        super.setSession(session); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    @Override
    public List<Room> getAllRoomTypes(Date start, Date end) {
        List<Room> result = new ArrayList();
        List<BookingItemType> allGroups = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : allGroups) {
            if(!type.visibleForBooking) {
                continue;
            }
            Room room = new Room();
            room.type = type;
            room.price = calculatePrice(type.id, start, end);
            result.add(room);
        }
        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        booking.sessionId = getSession().id;
        saveObject(booking);
        for(PmsBookingRooms room : booking.rooms) {
            room.price = calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end);
        }
        bookings.put(booking.id, booking);
    }

    @Override
    public PmsBooking getCurrentBooking() {
        if(getSession() == null) {
            System.out.println("Warning, no session set yet");
        }
        PmsBooking result = findBookingForSession();
        if(result == null) {
            return startBooking();
        }
        return result;
    }

    @Override
    public PmsBooking startBooking() {
        PmsBooking currentBooking = findBookingForSession();
        
        if(currentBooking != null) {
            deleteBooking(currentBooking, true);
        }
        
        PmsBooking booking = new PmsBooking();
        
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
        if(currentBooking.contactData.name.isEmpty()) {
            result.put("contact_name", 1);
        }
        
        if(currentBooking.contactData.type == 1) {
            if(currentBooking.contactData.birthday.isEmpty()) {
                result.put("contact_birthday", 1);
            }
        } else {
            if(currentBooking.contactData.orgid.isEmpty()) {
                result.put("contact_orgid", 1);
            }
        }
        
        
        return result;
    }

    private HashMap<String, String> validatePhone(String phone, String countryCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String prefix = "";
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
        phone = phone.replace("++", "+");
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

    /**
     * @return Error codes returned to the ui in case of failure.
     */
    @Override
    public Integer completeCurrentBooking() {
        PmsBooking booking = getCurrentBooking();
        Integer result = 0;
        if(!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }
        if(booking.userId != null && !booking.userId.isEmpty()) {
            if(!hasAccessUser(booking.userId)) {
                return -3;
            }
        }

        if(booking.rooms.isEmpty()) {
            return -4;
        }
        
        booking.priceType = prices.defaultPriceType;
        
        List<Booking> bookingsToAdd = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            Booking bookingToAdd = new Booking();
            bookingToAdd.startDate = room.date.start;
            if(room.date.end == null) {
                room.date.end = createInifinteDate();
            }
            bookingToAdd.endDate = room.date.end;
            bookingToAdd.bookingItemTypeId = room.bookingItemTypeId;
            bookingToAdd.externalReference = room.pmsBookingRoomId;

            bookingsToAdd.add(bookingToAdd);
        }
        try {
            if(bookingEngine.canAdd(bookingsToAdd)) {
                bookingEngine.addBookings(bookingsToAdd);
                booking.attachBookingItems(bookingsToAdd);
                booking.sessionId = null;
                if(booking.userId == null || booking.userId.isEmpty()) {
                    booking.userId = createUser(booking).id;
                }
                saveBooking(booking);
            } else {
                result = -2;
            }
            
        }catch(Exception e) {
            messageManager.sendErrorNotification("Unknown booking exception occured for booking id: " + booking.id);
            e.printStackTrace();
            result = -1;
        }
        

        return result;
    }

    private Date createInifinteDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 100);
        return cal.getTime();
    }

    private PmsBooking findBookingForSession() {
        if(getSession() == null) {
            return null;
        }
        String sessionId = getSession().id;
        if(sessionId == null) {
            return null;
        }
        
        for(PmsBooking booking : bookings.values()) {
            if(booking.sessionId != null && booking.sessionId.equals(sessionId)) {
                return booking;
            }
        }
        return null;
    }

    private User createUser(PmsBooking booking) {
        SecureRandom random = new SecureRandom();
        
        User user = new User();
        user.address = new Address();
        user.address.address = booking.contactData.address;
        user.address.city = booking.contactData.city;
        user.address.postCode = booking.contactData.postalCode;
        user.emailAddress = booking.contactData.email;
        user.cellPhone = booking.contactData.phone;
        user.prefix = booking.contactData.prefix;
        user.password = new BigInteger(130, random).toString(32);
        user.fullName = booking.contactData.name;
        if(booking.contactData.type == 1) {
            user.birthDay = booking.contactData.birthday;
        } else {
            user.birthDay = booking.contactData.orgid;
        }
        userManager.createUser(user);
        return user;
    }

    private boolean hasAccessUser(String userId) {
        User loggedOn = userManager.getLoggedOnUser();
        if(loggedOn.isAdministrator() || loggedOn.id.equals(userId)) {
            return true;
        }
        return false;
    }

    @Override
    public List<PmsBooking> getAllBookings(PmsBookingFilter filter) {
        if(filter.state == null) {
            filter.state = 0;
        }
        
        List<PmsBooking> result = new ArrayList();
        if(filter.state == 0) {
            result = new ArrayList(bookings.values());
        } else {
            for(PmsBooking booking : bookings.values()) {
                if(booking.state.equals(filter.state)) {
                    result.add(booking);
                }
            }
        }
        
        List<PmsBooking> finalized = finalizeList(result);
        checkForIncosistentBookings();
        return finalized;
    }

    private List<PmsBooking> finalizeList(List<PmsBooking> result) {
        List<PmsBooking> finalized = new ArrayList();
        for(PmsBooking toReturn : result) {
            toReturn = finalize(toReturn);
            if(toReturn != null) {
                finalized.add(toReturn);
            }
        }
        return finalized;
    }

    @Override
    public PmsBooking getBooking(String bookingId) {
        PmsBooking booking = bookings.get(bookingId);
        if(booking == null) {
            return null;
        }
        return finalize(booking);
    }

    private PmsBooking finalize(PmsBooking booking) {
        if(booking.rooms == null) {
            System.out.println("Removing booking due to no rooms");
            deleteBooking(booking, false);
            return null;
        }
        
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingId != null) {
                room.booking = bookingEngine.getBooking(room.bookingId);
                if(room.booking.bookingItemTypeId != null) {
                    room.bookingItemTypeId = room.booking.bookingItemTypeId;
                }
            }
        }
        
        //make sure the booking is sane.
        List<PmsBookingRooms> toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(room.booking == null) {
                toRemove.add(room);
            }
        }
        
        if(!toRemove.isEmpty()) {
            booking.rooms.removeAll(toRemove);
            saveObject(booking);
        }
        if(booking.rooms.isEmpty()) {
            System.out.println("Removing booking after finalizing");
            deleteBooking(booking, false);
            return null;
        }
        
        return booking;
    }

    private void deleteBooking(PmsBooking booking, boolean onInit) {
        if(!onInit) {
            System.out.println("Booking are being deleted");
        }
        bookings.remove(booking.id);
        deleteObject(booking);
    }

    private void checkForIncosistentBookings() {
        List<String> added = new ArrayList();
        for(PmsBooking booking : bookings.values()) {
            if(booking.rooms != null) {
                for(PmsBookingRooms room : booking.rooms) {
                    if(room.bookingId != null) {
                        added.add(room.bookingId);
                    }
                }
            }
        }
        
        List<Booking> allBookings = bookingEngine.getAllBookings();
        for(Booking booking : allBookings) {
            if(!added.contains(booking.id)) {
                bookingEngine.deleteBooking(booking.id);
            }
        }
    }

    @Override
    public String setNewRoomType(String roomId, String bookingId, String newType) {
        PmsBooking booking = getBooking(bookingId);
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            bookingEngine.changeTypeOnBooking(room.bookingId, newType);
            room.bookingItemTypeId = newType;
            saveBooking(booking);
        }catch(BookingEngineException ex) {
            return ex.getMessage();
        }
        return "";

    }

    @Override
    public String changeDates(String roomId, String bookingId, Date start, Date end) {
         PmsBooking booking = getBooking(bookingId);
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            bookingEngine.changeDatesOnBooking(bookingId, start, end);
            room.date.start = start;
            room.date.end = end;
            saveBooking(booking);
        }catch(BookingEngineException ex) {
            return ex.getMessage();
        }
        return "";
    }


    @Override
    public void saveBooking(PmsBooking booking) throws ErrorException {
        if(booking.id == null || booking.id.isEmpty() || getBooking(booking.id) == null) {
            throw new ErrorException(1000015);
        }
        validatePhoneNumbers(booking);
        bookings.put(booking.id, booking);
        saveObject(booking);
    }

    private void validatePhoneNumbers(PmsBooking booking) {
        for(PmsBookingRooms room : booking.rooms) {
            for(PmsGuests guest : room.guests) {
                HashMap<String, String> result = validatePhone("+" + guest.prefix + "" + guest.phone, "no");
                if(result != null) {
                    guest.prefix = result.get("prefix");
                    guest.phone = result.get("phone");
                } else {
                    guest.prefix = "47";
                    guest.phone = "";
                }
            }
        }
    }

    @Override
    public String setBookingItem(String roomId, String bookingId, String itemId) {
        PmsBooking booking = getBooking(bookingId);
        if(booking == null) {
            return "Booking does not exists";
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            if(room == null) {
                return "Room does not exists";
            }
            bookingEngine.changeBookingItemOnBooking(room.bookingId, itemId);
        }catch(BookingEngineException ex) {
            return ex.getMessage();
        }
        return "";
    }

    @Override
    public PmsPricing getPrices(Date start, Date end) {
        return prices;
    }

    @Override
    public PmsPricing setPrices(PmsPricing newPrices) {
        prices.defaultPriceType = newPrices.defaultPriceType;
        for(String typeId : newPrices.specifiedPrices.keySet()) {
            HashMap<String, Double> priceMap = newPrices.specifiedPrices.get(typeId);
            for(String date : priceMap.keySet()) {
                HashMap<String, Double> existingPriceRange = prices.specifiedPrices.get(typeId);
                if(existingPriceRange == null) {
                    existingPriceRange = new HashMap();
                    prices.specifiedPrices.put(typeId, existingPriceRange);
                }
                existingPriceRange.put(date, priceMap.get(date));
            }
        }
        saveObject(prices);
        
        return prices;
    }


    private Double calculatePrice(String typeId, Date start, Date end) {
        HashMap<String, Double> priceRange = prices.specifiedPrices.get(typeId);
        if(priceRange == null) {
            return 0.0;
        }
        
        Double defaultPrice = priceRange.get("default");
        if(defaultPrice == null) {
            defaultPrice = 0.0;
        }
        
        if(prices.defaultPriceType != 1) {
            return defaultPrice;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = 0;
        Double total = 0.0;
        while(true) {
            days++;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateToUse = formatter.format(cal.getTime());
            if(priceRange.get(dateToUse) != null) {
                total += priceRange.get(dateToUse);
            } else {
                total += defaultPrice;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        
        total = total / days;
        
        return total;
    }

    
}
