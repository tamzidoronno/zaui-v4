package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.RegistrationRules;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Summary;
import biweekly.util.Duration;
import biweekly.util.Recurrence;
import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.arx.ArxManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsManager extends GetShopSessionBeanNamed implements IPmsManager {

    public HashMap<String, PmsBooking> bookings = new HashMap();
    public HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo = new HashMap();
    public PmsPricing prices = new PmsPricing();
    public PmsConfiguration configuration = new PmsConfiguration();
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    MessageManager messageManager;

    @Autowired
    UserManager userManager;
    
    @Autowired
    OrderManager orderManager;
    
    @Autowired
    CartManager cartManager;
    
    @Autowired 
    ArxManager arxManager;
    
    @Autowired
    StoreManager storeManager;
    
    
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
            if (dataCommon instanceof PmsConfiguration) {
                configuration = (PmsConfiguration) dataCommon;
            }
            if (dataCommon instanceof PmsAdditionalItemInformation) {
                PmsAdditionalItemInformation res = (PmsAdditionalItemInformation) dataCommon;
                addiotionalItemInfo.put(res.itemId, res);
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
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingItemTypeId == null || room.bookingItemTypeId.isEmpty()) {
                if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    room.bookingItemTypeId = bookingEngine.getBookingItem(room.bookingItemId).bookingItemTypeId;
                }
            }
        }
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
        
        //First check if the room data is fine.
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
        
        if(configuration.needToAgreeOnContract && !currentBooking.contactData.agreedToTerms) {
            result.put("agreedToTerms", 1);
        }
        
        //Validate the contact data
        if(currentBooking.contactData.type == 3) {
            User validuser = userManager.checkUserNameAndPassword(currentBooking.contactData.username, currentBooking.contactData.password);
            if(validuser == null) {
                result.put("username", 1);
            }
        } else {
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
     * -3 Access denied
     * -2 Not enough available
     * -4 No rooms are selected
     * -1 Unknown error.
     */
    @Override
    public Integer completeCurrentBooking() {
        PmsBooking booking = getCurrentBooking();
        if(!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }
        
        Integer validation = validateBooking(booking);
        if(validation < 0) {
            return validation;
        }
        
        booking.priceType = prices.defaultPriceType;
        
        Integer result = 0;
        List<Booking> bookingsToAdd = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            Booking bookingToAdd = new Booking();
            bookingToAdd.startDate = room.date.start;
            if(room.date.end == null) {
                room.date.end = createInifinteDate();
            }
            if(room.numberOfGuests < room.guests.size()) {
                room.numberOfGuests = room.guests.size();
            }
            
            bookingToAdd.endDate = room.date.end;
            bookingToAdd.bookingItemTypeId = room.bookingItemTypeId;
            bookingToAdd.externalReference = room.pmsBookingRoomId;
            bookingToAdd.bookingItemId = room.bookingItemId;

            bookingsToAdd.add(bookingToAdd);
        }
        try {
            result = completeBooking(bookingsToAdd, booking);
            if(result == 0) {
                if(!booking.confirmed) {
                    doNotification("booking_completed", booking, null);
                } else {
                    doNotification("booking_confirmed", booking, null);
                }
            }
           
            return result;
        }catch(Exception e) {
            messageManager.sendErrorNotification("Unknown booking exception occured for booking id: " + booking.id);
            e.printStackTrace();
            return -1;
        }
        

    }

    private Integer completeBooking(List<Booking> bookingsToAdd, PmsBooking booking) throws ErrorException {
        if(!bookingEngine.canAdd(bookingsToAdd)) {
            return -2;
        }
        bookingEngine.addBookings(bookingsToAdd);
        booking.attachBookingItems(bookingsToAdd);
        booking.sessionId = null;
        if(booking.userId == null || booking.userId.isEmpty()) {
            booking.userId = createUser(booking).id;
        } else {
            if(configuration.autoconfirmRegisteredUsers) {
                booking.confirmed = true;
            }
        }

        if(!configuration.needConfirmation) {
            booking.confirmed = true;
        }

        saveBooking(booking);
        return 0;
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
        if(filter == null) {
            return finalizeList(new ArrayList(bookings.values()));
        }
        if(filter.state == null) {
            filter.state = 0;
        }
        
        List<PmsBooking> result = new ArrayList();
        
        if(filter.searchWord != null && !filter.searchWord.isEmpty()) {
            for(PmsBooking booking : bookings.values()) {
                if(booking.containsSearchWord(filter.searchWord)) {
                    result.add(booking);
                }
            }
        } else {
            if(filter.filterType== null || filter.filterType.equals("registered")) {
                for(PmsBooking booking : bookings.values()) {
                    if(booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate)) {
                        result.add(booking);
                    }
                }
            } else if(filter.filterType.equals("active")) {
                for(PmsBooking booking : bookings.values()) {
                    if(booking.isActiveInPeriode(filter.startDate, filter.endDate)) {
                        result.add(booking);
                    }
                }
            } else if(filter.filterType.equals("uncofirmed")) {
                for(PmsBooking booking : bookings.values()) {
                    if(!booking.confirmed) {
                        result.add(booking);
                    }
                }
            } else if(filter.filterType.equals("checkin")) {
                for(PmsBooking booking : bookings.values()) {
                    if(booking.checkingInBetween(filter.startDate, filter.endDate)) {
                        result.add(booking);
                    }
                }
            } else if(filter.filterType.equals("checkout")) {
                for(PmsBooking booking : bookings.values()) {
                    if(booking.checkingOutBetween(filter.startDate, filter.endDate)) {
                        result.add(booking);
                    }
                }
            } else if(filter.filterType.equals("deleted")) {
                for(PmsBooking booking : bookings.values()) {
                    if(booking.isDeleted) {
                        result.add(booking);
                    }
                }
            }
        }
        
        removeNotConfirmed(filter, result);
        removeDeleted(filter, result);
        
        List<PmsBooking> finalized = finalizeList(result);
        
        finalized = sortList(finalized, filter.sorting);
        
        finalized = filterTypes(finalized, filter.typeFilter);
        
        return finalized;
    }

    private void removeNotConfirmed(PmsBookingFilter filter, List<PmsBooking> result) {
        List<PmsBooking> toRemove = new ArrayList();
        if(filter.needToBeConfirmed) {
            for(PmsBooking booking : result) {
                if(!booking.confirmed) {
                    toRemove.add(booking);
                }
            }
        }
        result.removeAll(toRemove);
    }

    private List<PmsBooking> finalizeList(List<PmsBooking> result) {
        List<PmsBooking> finalized = new ArrayList();
        for(PmsBooking toReturn : result) {
            toReturn = finalize(toReturn);
            if(toReturn != null) {
                finalized.add(toReturn);
            }
        }
       checkForIncosistentBookings();
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
        if(booking.sessionId != null && !booking.sessionId.isEmpty()) {
            Calendar nowCal = Calendar.getInstance();
            nowCal.add(Calendar.HOUR_OF_DAY, -4);
            if(!booking.rowCreatedDate.after(nowCal.getTime())) {
                deleteBooking(booking.id);
                return null;
            }
        }
        if(booking.isDeleted) {
            booking.state = 2;
            return booking;
        }
        if(booking.rooms == null) {
            System.out.println("Removing booking due to no rooms");
            deleteBooking(booking, false);
            return null;
        }
        
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingId != null) {
                room.booking = bookingEngine.getBooking(room.bookingId);
                if(room.booking != null) {
                    room.date.start = room.booking.startDate;
                    room.date.end = room.booking.endDate;
                    room.bookingItemTypeId = room.booking.bookingItemTypeId;
                    room.bookingItemId = room.booking.bookingItemId;
                } else {
                    room.bookingItemId = null;
                }
                
                if(room.bookingItemId != null) {
                    room.item = bookingEngine.getBookingItem(room.bookingItemId);
                }
                if(room.bookingItemTypeId != null) {
                    room.type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
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
            bookingEngine.changeDatesOnBooking(room.bookingId, start, end);
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
        if(booking.id == null || booking.id.isEmpty() || bookings.get(booking.id) == null) {
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
            ex.printStackTrace();
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

    @Override
    public String createOrder(String bookingId, NewOrderFilter filter) {
        
        PmsBooking booking = getBooking(bookingId);
        Order order = null;
        if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            order = createMonthlyOrder(booking, filter);
        }
        if(order == null) {
            return "Could not create order.";
        }
        booking.orderIds.add(order.id);
        saveBooking(booking);
        return "";
    }

    private Order createMonthlyOrder(PmsBooking booking, NewOrderFilter filter) {
        cartManager.clear();
        
        Date startDate = filter.startInvoiceFrom;
        for(int i = 0; i < filter.numberOfMonths; i++) {
            Date endDate = addMonthsToDate(startDate, 1);
            int daysInMonth = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
            
            for(PmsBookingRooms room : booking.rooms) {
                String productId = bookingEngine.getBookingItemType(room.bookingItemTypeId).productId;
                if(productId == null) {
                    System.out.println("Product no set for this booking item type");
                }
                int numberOfDays = getNumberOfDays(room, startDate, endDate);
                if(numberOfDays == 0) {
                    return null;
                }
                double price = room.price / daysInMonth;
                price *= numberOfDays;
                
                CartItem item = cartManager.addProductItem(productId, 1);
                item.startDate = startDate;
                item.endDate = endDate;
                
                item.getProduct().discountedPrice = price;
                item.getProduct().price = price;
                cartManager.saveCartItem(item);
            }
            
            startDate = addMonthsToDate(startDate, 1);
        }
        
        User user = userManager.getUserById(booking.userId);
        
        Order order = orderManager.createOrder(user.address);
        
        order.payment = new Payment();
        order.payment.paymentType = user.preferredPaymentType;
        orderManager.saveOrder(order);
        
        booking.invoicedTo = startDate;
        
        return order;
    }

    private Date addMonthsToDate(Date startDate, Integer numberOfMonths) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, numberOfMonths);
        return cal.getTime();
    }

    private int getNumberOfDays(PmsBookingRooms room, Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(room.date.start);
        int days = 0;
        while(true) {
            if(cal.getTime().after(startDate) && (cal.getTime().before(endDate) || cal.getTime().equals(endDate))) {
                days++;
            }
            
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(room.date.end)) {
                break;
            }
        }
        return days;
    }

    @Override
    public PmsConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void saveConfiguration(PmsConfiguration notifications) {
        this.configuration = notifications;
        saveObject(notifications);
    }
    
    public void doNotification(String key, PmsBooking booking, PmsBookingRooms room) {
        key = key + "_" + booking.language;
        notify(key, booking, "sms", room);
        notify(key, booking, "email", room);
        notifyAdmin(key, booking);
    }

    private void notify(String key, PmsBooking booking, String type, PmsBookingRooms room) {
        String message = configuration.smses.get(key);
        if(type.equals("email")) {
            message = configuration.emails.get(key);
            if(message != null) {
                message = configuration.emailTemplate.replace("{content}", message);
            }
        }
        if(message == null || message.isEmpty()) {
            return;
        }
        
        message = formatMessage(message, booking, room, null);
        if(room != null) {
            notifyGuest(booking, message, type, key);
        } else {
            notifyBooker(booking, message, type, key);
        }
    }

    private void notifyBooker(PmsBooking booking, String message, String type, String key) throws ErrorException {
        User user = userManager.getUserById(booking.userId);
        if(type.equals("sms")) {
            messageManager.sendSms(user.cellPhone, message, user.prefix);
        } else {
            String title = configuration.emailTitles.get(key);
            if(key.startsWith("booking_confirmed")) {
                HashMap<String, String> attachments = createICalEntry(booking);
                String copyadress = storeManager.getMyStore().configuration.emailAdress;
                messageManager.sendMailWithAttachments(user.emailAddress, user.fullName, title, message, copyadress, copyadress, attachments);
            } else {
                messageManager.sendMailWithDefaults(user.fullName, user.emailAddress, title, message);
            }
        }
    }

    private String notifyGuest(PmsBooking booking, String message, String type, String key) {
        for(PmsBookingRooms room : booking.rooms) {
            for(PmsGuests guest : room.guests) {
                if(guest.phone == null || guest.phone.isEmpty()) {
                    continue;
                }
                if(type.equals("email")) {
                    String title = configuration.emailTitles.get(key);
                    title = formatMessage(message, booking, room, guest);
                    messageManager.sendMailWithDefaults(guest.name, guest.email, title, message);
                } else {
                    messageManager.sendSms(guest.phone, message, guest.prefix);
                }
            }
        }
        return message;
    }

    private void notifyAdmin(String key, PmsBooking booking) {
        String message = configuration.adminmessages.get(key);
        if(message == null) {
            return;
        }
        
        message = formatMessage(message, booking, null, null);
        String email = getStoreSettingsApplicationKey("emailaddress");
        String phone = getStoreSettingsApplicationKey("phoneNumber");
        messageManager.sendMailWithDefaults("Administrator", email, "Notification", message);
        messageManager.sendSms(phone, message);
    }

    private String formatMessage(String message, PmsBooking booking, PmsBookingRooms room, PmsGuests guest) {
        PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter();
        
        if(room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
        }
        message = formater.formatContactData(message, userManager.getUserById(booking.userId), null);
        message = formater.formatBookingData(message, booking, bookingEngine);
        
        return message;
    }

    @Override
    public void confirmBooking(String bookingId) {
        PmsBooking booking = getBooking(bookingId);
        booking.confirmed = true;
        saveBooking(booking);
        doNotification("booking_confirmed", booking, null);
    }

    @Override
    public PmsStatistics getStatistics(PmsBookingFilter filter) {
        List<PmsBooking> allBookings = getAllBookings(null);
        PmsStatisticsBuilder builder = new PmsStatisticsBuilder(allBookings);
        int totalRooms = bookingEngine.getBookingItems().size();
        PmsStatistics result = builder.buildStatistics(filter, totalRooms);
        result.salesEntries = builder.buildOrderStatistics(filter, orderManager);
        result.buildTotalSales();
        return result;
    }

    @Override
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        Date start = null;
        Date end = null;
        for(PmsBookingRooms room : booking.rooms) {
            if(room.date.start != null) {
                if(start == null) {
                    start = room.date.start;
                } else {
                    if(start.after(room.date.start)) {
                        start = room.date.start;
                    }
                }
            }
            if(room.date.end != null) {
                if(end == null) {
                    end = room.date.end;
                } else {
                    if(end.before(room.date.end)) {
                        end = room.date.end;
                    }
                }
            }
        }
        
        PmsBookingRooms room = new PmsBookingRooms();
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.bookingItemTypeId = itemtypeId;
        booking.rooms.add(room);
        setBooking(booking);
    }

    @Override
    public void removeAddonFromCurrentBooking(String itemtypeId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        ArrayList toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingItemTypeId.equals(itemtypeId)) {
                toRemove.add(room);
            }
        }
        booking.rooms.removeAll(toRemove);
        setBooking(booking);
    }

    @Override
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter filter) {
        if(!configuration.exposeUnsecureBookings) {
            return new ArrayList();
        }
        List<PmsBooking> result = getAllBookings(filter);
        
        List<PmsBooking> allBookings = new ArrayList();
        
        for(PmsBooking res : result) {
            allBookings.add(res.copyUnsecure());
        }
        
        return allBookings;
    }

    private Integer validateBooking(PmsBooking booking) {

        if(booking.userId != null && !booking.userId.isEmpty()) {
            if(!hasAccessUser(booking.userId)) {
                return -3;
            }
        }
        
        if(booking.contactData.type == 3) {
            User user = userManager.checkUserNameAndPassword(booking.contactData.username, booking.contactData.password);
            if(user == null) {
                return -3;
            }
            booking.userId = user.id;
        }
        
        if(booking.rooms.isEmpty()) {
            return -4;
        }    
        return 0;
    }

    @Override
    public String getContract(String bookingId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        if(booking == null || !booking.id.equals(bookingId)) {
            booking = getBooking(bookingId);
        }
        if(booking == null) {
            return "Booking could not be found";
        }
        String contract = configuration.contracts.get(booking.language);
        if(contract == null) {
            return "";
        }
        return formatMessage(contract, booking, null, null);
    }

    @Override
    public String getCurrenctContract() throws Exception {
        PmsBooking current = getCurrentBooking();
        return getContract(current.id);
    }

    @Override
    public void deleteBooking(String bookingId) {
        PmsBooking booking = bookings.get(bookingId);
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingId != null && !room.bookingId.isEmpty()) {
                bookingEngine.deleteBooking(room.bookingId);
            }
        }
        booking.isDeleted = true;
        saveBooking(booking);
    }

    private void removeDeleted(PmsBookingFilter filter, List<PmsBooking> result) {
        if(filter != null && filter.filterType != null && filter.filterType.equalsIgnoreCase("deleted")) {
            return;
        }
        List<PmsBooking> toRemove = new ArrayList();
        if(!filter.includeDeleted) {
            for(PmsBooking booking : result) {
                if(booking.isDeleted) {
                    toRemove.add(booking);
                }
            }
        }
        result.removeAll(toRemove);
    }

    @Override
    public void processor() {
        PmsManagerProcessor processor = new PmsManagerProcessor(this);
        processor.doProcessing();
    }

    void warnArxDown() {
        System.out.println("Arx is down");
    }

    @Override
    public PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter) {
        PmsIntervalResult res = new PmsIntervalResult();
        for(BookingItemType type : bookingEngine.getBookingItemTypes()) {
            BookingTimeLineFlatten line = bookingEngine.getTimelines(type.id, filter.start, filter.end);
            res.typeTimeLines.put(type.id, line.getTimelines(filter.interval));
        }
        
        List<BookingItem> items = bookingEngine.getBookingItems();
        
        for(BookingItem item : items) {
           BookingTimeLineFlatten line = bookingEngine.getTimeLinesForItem(filter.start, filter.end, item.id);
            List<BookingTimeLine> timelines = line.getTimelines(filter.interval);
            HashMap<Long, Integer> itemCountLine = new HashMap();
            timelines.stream().forEach(o -> itemCountLine.put(o.start.getTime(), o.count));
            res.itemTimeLines.put(item.id, itemCountLine);
        }
        
        return res;
    }

    @Override
    public Boolean isClean(String itemId) {
        PmsAdditionalItemInformation addiotionalInfo = getAdditionalInfo(itemId);
        return addiotionalInfo.isClean();
    }

    private PmsAdditionalItemInformation getAdditionalInfo(String itemId) {
        PmsAdditionalItemInformation result = addiotionalItemInfo.get(itemId);
        if(result == null) {
            result = new PmsAdditionalItemInformation();
            result.itemId = itemId;
            addiotionalItemInfo.put(itemId, result);
            saveAdditionalInfo(result);
        }
        
        return result;
    }
    
    private void saveAdditionalInfo(PmsAdditionalItemInformation data) {
        saveObject(data);
    }

    @Override
    public void markRoomAsCleaned(String itemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        Date start = new Date();
        Calendar end = Calendar.getInstance();
        end.setTime(start);
        boolean bookingStartingToday = bookingEngine.hasBookingsStartingBetweenTime(start, end.getTime(), itemId);
        boolean itemInUse = bookingEngine.itemInUseBetweenTime(start, end.getTime(), itemId);
        if(bookingStartingToday || !itemInUse) {
            //Only mark room cleaned if a new booking is 
            additional.markCleaned();
        } else {
            additional.addCleaningDate();
        }
        saveAdditionalInfo(additional);
    }

    @Override
    public List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms() {
        List<PmsAdditionalItemInformation> result = new ArrayList();
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(BookingItem item : items) {
            result.add(finalizeAdditionalItem(getAdditionalInfo(item.id)));
        }
        return result;
    }

    private PmsAdditionalItemInformation finalizeAdditionalItem(PmsAdditionalItemInformation additionalInfo) {
        Calendar start = Calendar.getInstance();
        Calendar end = start.getInstance();
        end.add(Calendar.MINUTE, 1);
        
        additionalInfo.isClean();
        additionalInfo.inUse = bookingEngine.itemInUseBetweenTime(start.getTime(), end.getTime(), additionalInfo.itemId);
        return additionalInfo;
    }

    @Override
    public List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day) {
        Calendar endcal = Calendar.getInstance();
        endcal.setTime(day);
        endcal.add(Calendar.DAY_OF_YEAR, 1);
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = PmsBookingFilter.PmsBookingFilterTypes.active;
        filter.startDate = day;
        filter.endDate = endcal.getTime();
        
        List<PmsBookingRooms> rooms = new ArrayList<PmsBookingRooms>();
        List<PmsBooking> allBookings = getAllBookings(filter);
        for(PmsBooking booking :allBookings) {
            for(PmsBookingRooms room : booking.rooms) {
                if(needIntervalCleaning(room, day)) {
                    rooms.add(room);
                }
            }
        }
        
        return rooms;
    }

    private boolean needIntervalCleaning(PmsBookingRooms room, Date day) {
        int days = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(day)).getDays();
        if(days == 0) {
            return false;
        }
        int interval = configuration.cleaningInterval;
        if(room.intervalCleaning != null) {
            interval = room.intervalCleaning;
        }
        if(interval == 0) {
            return false;
        }
        if(days % interval == 0) {
            return true;
        }
        return false;
    }

    private HashMap<String, String> createICalEntry(PmsBooking booking) {
        ICalendar ical = new ICalendar();
        for(PmsBookingRooms room : booking.rooms) {
            VEvent event = new VEvent();
            String title = "Booking of";
            
            if(room.booking.bookingItemId != null) {
                title += " room " + bookingEngine.getBookingItem(room.booking.bookingItemId).bookingItemName;
            } else {
                title += " " + bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            }
            
            Summary summary = event.setSummary(title);
            summary.setLanguage("en-us");

            event.setDateStart(room.date.start);
            
            int minutes = (int)((room.date.end.getTime()/60000) - (room.date.start.getTime()/60000));

            Duration duration = new Duration.Builder().minutes(minutes).build();
            event.setDuration(duration);

            ical.addEvent(event);
        }

        HashMap<String, String> attachments = new HashMap();
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();

            String str = Biweekly.write(ical).go();
            byte[] encoded = Base64.encodeBase64(str.getBytes());
            String encodedString = new String(encoded);

            attachments.put("calendar.ics", encodedString);
        }catch(Exception e) {
            e.printStackTrace();
        }
            
        return attachments;
    }

    private List<PmsBooking> sortList(List<PmsBooking> result, String sorting) {
        if(sorting == null) {
            sorting = "";
        }
        
        if(sorting.equals("visitor") || sorting.equals("visitor_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>(){
                public int compare(PmsBooking o1, PmsBooking o2){
                    return o1.rooms.get(0).guests.get(0).name.compareTo(o2.rooms.get(0).guests.get(0).name);
                }
            });
        } else if(sorting.equals("periode") || sorting.equals("periode_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>(){
                public int compare(PmsBooking o1, PmsBooking o2){
                    return o1.rooms.get(0).date.start.compareTo(o2.rooms.get(0).date.start);
                }
            });
        } else if(sorting.equals("room") || sorting.equals("room_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>(){
                public int compare(PmsBooking o1, PmsBooking o2){
                    if(o1.rooms == null || o1.rooms.isEmpty() || o1.rooms.get(0).item == null) {
                        return -1;
                    }
                    if(o2.rooms == null || o2.rooms.isEmpty() || o2.rooms.get(0).item == null) {
                        return -1;
                    }
                    return o1.rooms.get(0).item.bookingItemName.compareTo(o2.rooms.get(0).item.bookingItemName);
                }
            });
        } else if(sorting.equals("price") || sorting.equals("price_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>(){
                public int compare(PmsBooking o1, PmsBooking o2){
                    return o1.rooms.get(0).price.compareTo(o2.rooms.get(0).price);
                }
            });
        } else {
            Collections.sort(result, new Comparator<PmsBooking>(){
                public int compare(PmsBooking o1, PmsBooking o2){
                   return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
                }
             });
        }
        
        if(sorting.contains("_desc")) {
            Collections.reverse(result);
        }
        
        return result;
    }

    private List<PmsBooking> filterTypes(List<PmsBooking> finalized, List<String> typeFilter) {
        if(typeFilter == null || typeFilter.isEmpty()) {
            return finalized;
        }
        
        List<PmsBooking> result = new LinkedList();
        for(PmsBooking booking : finalized) {
            boolean add = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(room.bookingItemTypeId != null && typeFilter.contains(room.bookingItemTypeId))
                    add = true;
            }
            if(add) {
                result.add(booking);
            }
        }
        return result;
    }

    @Override
    public RegistrationRules initBookingRules() {
        return new RegistrationRules();
    }
}
