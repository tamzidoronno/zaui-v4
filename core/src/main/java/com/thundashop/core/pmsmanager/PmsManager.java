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
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
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
    
    @Autowired
    ProductManager productManager;
    
    private String specifiedMessage = "";
    
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
            room.price = calculatePrice(type.id, start, end, true);
            result.add(room);
        }
        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        booking.sessionId = getSession().id;
        
        booking.priceType = prices.defaultPriceType;
        for(PmsBookingRooms room : booking.rooms) {
            if(room.bookingItemTypeId == null || room.bookingItemTypeId.isEmpty()) {
                if(room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    room.bookingItemTypeId = bookingEngine.getBookingItem(room.bookingItemId).bookingItemTypeId;
                }
            }
        }
        for(PmsBookingRooms room : booking.rooms) {
            int totalDays = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(room.date.end)).getDays();
            room.count = totalDays;
            room.price = calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end, true);
            room.taxes = calculateTaxes(room.bookingItemTypeId);
            for(PmsGuests guest : room.guests) {
                if(guest.prefix != null) {
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                }
            }
        }
        
        if(booking.sessionStartDate == null) {
            PmsBookingDateRange range = getDefaultDateRange();
            booking.sessionStartDate = range.start;
            booking.sessionEndDate = range.end;
        }
        
        saveObject(booking);
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
        
        Integer result = 0;
        List<Booking> bookingsToAdd = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(!room.canBeAdded) {
                continue;
            }
            Booking bookingToAdd = createBooking(room);
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
        if(booking.registrationData.resultAdded.get("company_invoicenote") != null) {
            booking.invoiceNote = booking.registrationData.resultAdded.get("company_invoicenote");
        }
        if(booking.userId == null || booking.userId.isEmpty()) {
            User newuser = createUser(booking);
            booking.userId = newuser.id;
            Company curcompany = createCompany(booking);
            if(curcompany != null) {
                curcompany = userManager.saveCompany(curcompany);
                newuser.company.add(curcompany.id);
                newuser.fullName = curcompany.name;
                newuser.emailAddress = curcompany.email;
                newuser.cellPhone = curcompany.phone;
                newuser.prefix = curcompany.prefix;
                newuser.address = curcompany.address;
                
                userManager.saveUserSecure(newuser);
            }
        } else {
            if(configuration.autoconfirmRegisteredUsers) {
                booking.confirmed = true;
            }
        }

        if(!configuration.needConfirmation) {
            booking.confirmed = true;
        }
        
        User loggedonuser = userManager.getLoggedOnUser();
        if(loggedonuser != null && loggedonuser.isAdministrator()) {
            booking.confirmed = true;
        }

        booking.sessionId = "";
        
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
                User user = userManager.getUserById(booking.userId);
                if(user != null && user.fullName!= null && user.fullName.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                    result.add(booking);
                } else if(booking.containsSearchWord(filter.searchWord)) {
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
                    String productId = bookingEngine.getBookingItemType(room.bookingItemTypeId).productId;
                    if(productId != null) {
                        room.taxes = productManager.getProduct(productId).taxGroupObject.taxRate;
                    }
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
        } catch(BookingEngineException ex) {
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
        prices.progressivePrices = newPrices.progressivePrices;
        for(String typeId : newPrices.dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = newPrices.dailyPrices.get(typeId);
            for(String date : priceMap.keySet()) {
                HashMap<String, Double> existingPriceRange = prices.dailyPrices.get(typeId);
                if(existingPriceRange == null) {
                    existingPriceRange = new HashMap();
                    prices.dailyPrices.put(typeId, existingPriceRange);
                }
                existingPriceRange.put(date, priceMap.get(date));
            }
        }
        saveObject(prices);
        
        return prices;
    }

    private Double calculatePrice(String typeId, Date start, Date end, boolean avgPrice) {
        if(prices.defaultPriceType == 1) {
            return calculateDailyPricing(typeId,start,end, avgPrice);
        }
        if(prices.defaultPriceType == 7) {
            return calculateProgressivePrice(typeId,start,end,0, avgPrice);
        }
        if(prices.defaultPriceType == 8) {
            return calculateIntervalPrice(typeId,start,end,avgPrice);
        }
        
        return 0.0;
    }

    @Override
    public String createOrder(String bookingId, NewOrderFilter filter) {
        
        PmsBooking booking = getBooking(bookingId);
        Order order = null;
        order = createOrder(booking, filter);
        
        if(order == null) {
            return "Could not create order.";
        }
        booking.orderIds.add(order.id);
        saveBooking(booking);
        return "";
    }

    private Order createOrder(PmsBooking booking, NewOrderFilter filter) {
        cartManager.clear();

        boolean foundInvoice = false;
        
        for(PmsBookingRooms room : booking.rooms) {
            Double price = null;

            Date startDate = filter.startInvoiceFrom;
            if(startDate.before(room.date.start)) {
                startDate = room.date.start;
            }
            
            Date endDate = filter.endInvoiceAt;
            if(endDate.after(room.date.end)) {
                endDate = room.date.end;
            }
            int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
            
            if(room.invoicedTo != null && startDate.before(room.invoicedTo)) {
                startDate = room.invoicedTo;
            }
            
            if(filter.onlyEnded && room.date.end.after(filter.endInvoiceAt)) {
                continue;
            }
            
            if(sameDayOrAfter(room.invoicedTo, endDate)) {
                continue;
            }
            
            if(filter.itemId!= null && !filter.itemId.isEmpty()) {
                if(room.bookingItemId == null) {
                    continue;
                }
                if(room.bookingItemId.equals(filter.itemId)) {
                    continue;
                }
            }
            
            if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
                int numberOfDays = getNumberOfDays(room, startDate, endDate);
                if(numberOfDays == 0) {
                    return null;
                }
                price = room.price / daysInPeriode;
            }
            if(booking.priceType.equals(PmsBooking.PriceType.progressive)) {
                int days = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(startDate)).getDays();
                price = calculateProgressivePrice(room.bookingItemTypeId, startDate, endDate, days, true);
            }
            if(booking.priceType.equals(PmsBooking.PriceType.interval)) {
                price = calculateIntervalPrice(room.bookingItemTypeId, room.date.start, room.date.end, true);
            }
            if(booking.priceType.equals(PmsBooking.PriceType.daily) || 
                booking.priceType.equals(PmsBooking.PriceType.interval) || 
                booking.priceType.equals(PmsBooking.PriceType.progressive)) {
                price = room.price;
            }
            if(booking.priceType.equals(PmsBooking.PriceType.weekly)) {
                price = (room.price/7);
            }

            CartItem item = createCartItem(room, startDate, endDate);
            if(item == null) {
                return null;
            }
            if(prices.pricesExTaxes) {
                double tax = 1 + (calculateTaxes(room.bookingItemTypeId) / 100);
                price *= tax;
            }
            
            item.getProduct().discountedPrice = price;
            item.getProduct().price = price;
            item.setCount(daysInPeriode);
            room.invoicedTo = endDate;
            foundInvoice = true;
            cartManager.saveCartItem(item);
        }
        
        if(!foundInvoice) {
            return null;
        }
        
        User user = userManager.getUserById(booking.userId);
        if(user == null) {
            return null;
        }
        
        user.address.fullName = user.fullName;
        
        Order order = orderManager.createOrder(user.address);
        
        order.payment = new Payment();
        order.payment.paymentType = user.preferredPaymentType;
        order.userId = booking.userId;
        order.invoiceNote = booking.invoiceNote;
        orderManager.saveOrder(order);
        
        return order;
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
    
    @Administrator
    public void doNotification(String key, String bookingId, String roomId) {
        PmsBooking booking = getBooking(bookingId);
        doNotification(key, booking, null);
    }
    
    public void doNotification(String key, PmsBooking booking, PmsBookingRooms room) {
        key = key + "_" + booking.language;
        notify(key, booking, "sms", room);
        notify(key, booking, "email", room);
        notifyAdmin(key, booking);
        specifiedMessage = "";
    }

    private void notify(String key, PmsBooking booking, String type, PmsBookingRooms room) {
        String message = configuration.smses.get(key);
        if(type.equals("email")) {
            message = configuration.emails.get(key);
            if(message != null) {
                message = configuration.emailTemplate.replace("{content}", message);
                message = message.replace("\n", "<br>\n");
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
        
        if(this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }
        
        if(room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
        }
        message = formater.formatContactData(message, userManager.getUserById(booking.userId), null);
        message = formater.formatBookingData(message, booking, bookingEngine);
        
        return message;
    }

    @Override
    public void confirmBooking(String bookingId, String message) {
        this.specifiedMessage = message;
        PmsBooking booking = getBooking(bookingId);
        booking.confirmed = true;
        saveBooking(booking);
        doNotification("booking_confirmed", booking, null);
    }
    
    @Override
    public void unConfirmBooking(String bookingId, String message) {
        this.specifiedMessage = message;
        PmsBooking booking = getBooking(bookingId);
        booking.unConfirmed = true;
        saveBooking(booking);
        deleteBooking(bookingId);
        
        doNotification("booking_notconfirmed", booking, null);
    }
    

    @Override
    public PmsStatistics getStatistics(PmsBookingFilter filter) {
        filter.filterType = "active";
        List<PmsBooking> allBookings = getAllBookings(filter);
        PmsStatisticsBuilder builder = new PmsStatisticsBuilder(allBookings, prices.pricesExTaxes);
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
    public void removeFromBooking(String bookingId, String roomId) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        List<PmsBookingRooms> toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                toRemove.add(room);
            }
        }
        for(PmsBookingRooms remove : toRemove) {
            bookingEngine.deleteBooking(remove.bookingId);
            booking.rooms.remove(remove);
        }
        saveObject(booking);
    }
    
    @Override
    public void removeFromCurrentBooking(String roomId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        ArrayList toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                toRemove.add(room);
            }
        }
        booking.rooms.removeAll(toRemove);
        setBooking(booking);
    }

    @Override
    public List<PmsBooking> getAllBookingsUnsecure(PmsBookingFilter filter) {
        List<PmsBooking> result = getAllBookings(filter);
        
        List<PmsBooking> allBookings = new ArrayList();
        
        for(PmsBooking res : result) {
            allBookings.add(res.copyUnsecure());
        }
        
        return allBookings;
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
            LinkedHashMap<Long, Integer> itemCountLine = new LinkedHashMap();
            for(BookingTimeLine tl : timelines) {
                itemCountLine.put(tl.start.getTime(), tl.count);
            }
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
            
            if(room.booking != null && room.booking.bookingItemId != null && bookingEngine.getBookingItem(room.booking.bookingItemId) != null) {
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

    private User createUser(PmsBooking booking) {
        LinkedHashMap<String, String> result = booking.registrationData.resultAdded;
        User user = new User();
        user.emailAddress = "";
        /* Fields found in FieldGenerator.php */
        user.fullName = result.get("user_fullName");
        user.cellPhone = result.get("user_cellPhone");
        user.emailAddress = result.get("user_emailAddress");
        if(result.get("prefix") != null) {
            user.prefix = result.get("user_prefix");
        }
        
        user.address = new Address();
        user.address.address = result.get("user_address_address");
        user.address.postCode = result.get("user_address_postCode");
        user.address.city = result.get("user_address_city");
        user.address.countrycode = result.get("user_address_countrycode");
        user.address.countryname = result.get("user_address_countryname");
        
        if(user.emailAddress == null) { user.emailAddress = ""; }
        if(user.fullName == null) { user.fullName = ""; }
        if(user.cellPhone == null) { user.cellPhone = ""; }
        if(user.prefix == null) { user.prefix = "47"; }
        if(user.address.address == null) { user.address.address = ""; }
        if(user.address.postCode == null) { user.address.postCode = ""; }
        if(user.address.city == null) { user.address.city = ""; }
        if(user.address.countrycode == null) { user.address.countrycode = ""; }
        if(user.address.countryname == null) { user.address.countryname = ""; }
        
        return userManager.createUser(user);
    }

    private Company createCompany(PmsBooking booking) {
        LinkedHashMap<String, String> result = booking.registrationData.resultAdded;
        if(result.get("choosetyperadio") == null || result.get("choosetyperadio").equals("registration_private")) {
            return null;
        }
        
        Company company = new Company();
        company.vatNumber = result.get("company_vatNumber");
        company.name = result.get("company_name");
        company.phone = result.get("company_phone");
        company.website = result.get("company_website");
        company.email = result.get("company_email");
        company.contactPerson = result.get("company_contact");
        company.prefix = result.get("company_prefix");
        company.phone = result.get("company_phone");
        company.vatRegisterd = true;
        if(result.get("company_vatRegistered") != null) {
            company.vatRegisterd = result.get("company_vatRegistered").equals("true");
        }
        company.invoiceEmail = result.get("company_emailAddressToInvoice");
        
        company.address = new Address();
        company.address.address = result.get("company_address_address");
        company.address.postCode = result.get("company_address_postCode");
        company.address.city = result.get("company_address_city");
        company.address.countrycode = result.get("company_address_countrycode");
        company.address.countryname = result.get("company_address_countryname");
        
        company.invoiceAddress = new Address();
        company.invoiceAddress.address = result.get("company_invoiceAddress_address");
        company.invoiceAddress.postCode = result.get("company_invoiceAddress_postCode");
        company.invoiceAddress.city = result.get("company_invoiceAddress_city");
        company.invoiceAddress.countrycode = result.get("company_invoiceAddress_countrycode");
        company.invoiceAddress.countryname = result.get("company_invoiceAddress_countryname");
        
        return company;
    }

    @Override
    public void addRepeatingData(PmsRepeatingData data) throws Exception {
        PmsBooking curBooking = getCurrentBooking();
        removeRepeatedRooms(curBooking);
        
        if(data.repeattype.equals("repeat")) {
            addRepeatingRooms(data);
        } else {
            addSingleDate(data);
        }
        updateBookingAddonList();
        setBooking(curBooking);
    }

    private List<TimeRepeaterDateRange> addSingleDate(PmsRepeatingData data) {
        List<TimeRepeaterDateRange> toReturn = new ArrayList();
        Booking booking = new Booking();
        booking.bookingItemId = data.bookingItemId;
        booking.startDate = data.data.firstEvent.start;
        booking.endDate = data.data.firstEvent.end;
        
        if(data.bookingTypeId == null || data.bookingTypeId.isEmpty()) {
            booking.bookingItemTypeId = bookingEngine.getBookingItem(data.bookingItemId).bookingItemTypeId;
        }
        
        List<Booking> toCheck = new ArrayList();
        toCheck.add(booking);
        PmsBooking curBooking = getCurrentBooking();
        if(bookingEngine.canAdd(toCheck)) {
            PmsBookingRooms room = new PmsBookingRooms();
            room.date.start = data.data.firstEvent.start;
            room.date.end = data.data.firstEvent.end;
            room.bookingItemId = booking.bookingItemId;
            room.bookingItemTypeId =booking.bookingItemTypeId;
            curBooking.rooms.add(room);
        } else {
            toReturn.add(data.data.firstEvent);
        }
        
        curBooking.lastRepeatingData = data;
        saveBooking(curBooking);
        
        return toReturn;
    }

    private List<TimeRepeaterDateRange> addRepeatingRooms(PmsRepeatingData data) throws ErrorException {
        TimeRepeater repeater = new TimeRepeater();
        LinkedList<TimeRepeaterDateRange> lines = repeater.generateRange(data.data);
        
        List<TimeRepeaterDateRange> cantAdd = new ArrayList();
        List<PmsBookingRooms> allRooms = new ArrayList();
        
        for(TimeRepeaterDateRange line : lines) {
            
            Booking booking = new Booking();
            booking.bookingItemId = data.bookingItemId;
            booking.startDate = line.start;
            booking.endDate = line.end;
            
            if(data.bookingTypeId == null || data.bookingTypeId.isEmpty()) {
                booking.bookingItemTypeId = bookingEngine.getBookingItem(data.bookingItemId).bookingItemTypeId;
            }
            
            PmsBookingRooms room = new PmsBookingRooms();
            room.date.start = line.start;
            room.date.end = line.end;
            room.bookingItemId = booking.bookingItemId;
            room.bookingItemTypeId =booking.bookingItemTypeId;
            room.addedByRepeater = true;
            
            List<Booking> toCheck = new ArrayList();
            toCheck.add(booking);
            if(!bookingEngine.canAdd(toCheck)) {
                room.canBeAdded = false;
            }
            
            allRooms.add(room);
        }
        
        
        PmsBooking curBooking = getCurrentBooking();
        curBooking.rooms.addAll(allRooms);
        curBooking.lastRepeatingData = data;
        saveBooking(curBooking);
        
        return cantAdd;
    }

    private void removeRepeatedRooms(PmsBooking curBooking) {
        List<PmsBookingRooms> toRemove = new ArrayList();
        for(PmsBookingRooms room : curBooking.rooms) {
            if(room.addedByRepeater) {
                toRemove.add(room);
            }
        }
        curBooking.rooms.removeAll(toRemove);
    }

    private Double calculateDailyPricing(String typeId, Date start, Date end, boolean avgPrice) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);
        
        if(priceRange == null) {
            return 0.0;
        }
        
        Double defaultPrice = priceRange.get("default");
        if(defaultPrice == null) {
            defaultPrice = 0.0;
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
        if(avgPrice) {
            total = total / days;
        }
        return total;
    }

    private Double calculateIntervalPrice(String typeId, Date start, Date end, boolean avgprice) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        if(priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.124;
        }
        int daysoffset=0;
        for(ProgressivePriceAttribute attr : priceRange) {
            daysoffset += attr.numberOfTimeSlots;
            if(daysoffset >= totalDays) {
                if(avgprice) {
                    return attr.price;
                } else {
                    return attr.price;
                }
            }
        }
        
        //Could not find price to use.
        return -0.333;
    }
    
    private Double calculateProgressivePrice(String typeId, Date start, Date end, int offset, boolean avgPrice) {
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        if(priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.123;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = offset;
        Double total = 0.0;
        while(true) {
            int daysoffset = 0;
            for(ProgressivePriceAttribute attr : priceRange) {
                daysoffset += attr.numberOfTimeSlots;
                if(daysoffset > days) {
                    total += attr.price;
                    daysoffset = 0;
                    break;
                }
            }
            days++;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        
        if(avgPrice) {
            total = total / days;
        }
        return total;
    }

    private double calculateTaxes(String bookingItemTypeId) {
        BookingItemType item = bookingEngine.getBookingItemType(bookingItemTypeId);
        if(item.productId != null && !item.productId.isEmpty()) {
            Product product = productManager.getProduct(item.productId);
            if(product.taxGroupObject != null) {
                return product.taxGroupObject.taxRate;
            }
            return -1.0;
        }
        return -2.0;
    }

    private CartItem createCartItem(PmsBookingRooms room, Date startDate, Date endDate) {
        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        BookingItem bookingitem = null;
        if(room.bookingItemId != null) {
            bookingitem = bookingEngine.getBookingItem(room.bookingItemId);
        }
        if(type == null) {
            return null;
        }

        String productId = type.productId;
        if(productId == null) {
            System.out.println("Product not set for this booking item type");
        }
        int numberOfDays = getNumberOfDays(room, startDate, endDate);
        if(numberOfDays == 0) {
            return null;
        }

        CartItem item = cartManager.addProductItem(productId, 1);
        item.startDate = startDate;
        item.endDate = endDate;
        item.getProduct().name = type.name;
        if(bookingitem != null) {
            item.getProduct().name += " (" + bookingitem.bookingItemName + ")";
        }
        return item;
    }

    @Override
    public List<Integer> getAvailabilityForRoom(String bookingItemId, Date startTime, Date endTime, Integer intervalInMinutes) {
        LinkedList<TimeRepeaterDateRange> lines = createAvailabilityLines(bookingItemId);
        
        DateTime timer = new DateTime(startTime);
        List<Integer> result = new ArrayList();
        while(true) {
            if(hasRange(lines, timer)) {
                result.add(1);
            } else {
                result.add(0);
            }
            timer = timer.plusMinutes(intervalInMinutes);
            if(timer.toDate().after(endTime) || timer.toDate().equals(endTime)) {
                break;
            }
        }
        return result;
    }

    private Date getMorning(boolean morning) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, 2016);
        date.set(Calendar.DAY_OF_YEAR, 1);
        date.set(Calendar.HOUR_OF_DAY, 7);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.set(Calendar.MINUTE, 30);
        if(morning) {
            return date.getTime();
        }
        date.set(Calendar.HOUR_OF_DAY,22);
        
        return date.getTime();
    }

    private LinkedList<TimeRepeaterDateRange> createAvailabilityLines(String bookingItemId) {
        if(bookingItemId != null && bookingItemId.isEmpty()) {
            bookingItemId = null;
        }
        TimeRepeaterData repeater = bookingEngine.getOpeningHours(bookingItemId);
        if(repeater == null) {
            return new LinkedList();
        } else {
            TimeRepeater generator = new TimeRepeater();
            return generator.generateRange(repeater);
        }
    }

    private boolean hasRange(LinkedList<TimeRepeaterDateRange> lines, DateTime timer) {
        for(TimeRepeaterDateRange line : lines) {
            if(line.isBetweenTime(timer.toDate())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toggleAddon(String itemId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        if(booking.bookingEngineAddons.contains(itemId)) {
            booking.bookingEngineAddons.remove(itemId);
        } else {
            booking.bookingEngineAddons.add(itemId);
        }
        updateBookingAddonList();
        setBooking(booking);
    }

    private void updateBookingAddonList() {
        PmsBooking booking = getCurrentBooking();
        List<PmsBookingRooms> toRemove = new ArrayList();
        for(PmsBookingRooms room : booking.rooms) {
            if(room.isAddon) {
                toRemove.add(room);
            }
        }
        
        booking.rooms.removeAll(toRemove);
        
        List<PmsBookingRooms> allToAdd = new ArrayList();
        for(String addonId : booking.bookingEngineAddons) {
            for(PmsBookingRooms room : booking.rooms) {
                Booking bookingToAdd = new Booking();
                bookingToAdd.startDate = room.date.start;
                bookingToAdd.endDate = room.date.end;
                bookingToAdd.bookingItemTypeId = addonId;
                
                PmsBookingRooms toAddRoom = new PmsBookingRooms();
                toAddRoom.date.start = room.date.start;
                toAddRoom.date.end = room.date.end;
                toAddRoom.bookingItemTypeId = addonId;
                toAddRoom.isAddon = true;
                
                List<Booking> checkToAdd = new ArrayList();
                checkToAdd.add(bookingToAdd);
                toAddRoom.canBeAdded = true;
                if(!bookingEngine.canAdd(checkToAdd)) {
                    toAddRoom.canBeAdded = false;
                }
                
                allToAdd.add(toAddRoom);
            }
        }
        
        booking.rooms.addAll(allToAdd);

    }
    
    @Override
    public PmsBookingDateRange getDefaultDateRange() {
        
        String[] defaultTimeStart = getConfiguration().defaultStart.split(":");
        String[] defaultEndStart = getConfiguration().defaultEnd.split(":");
        
        Calendar calStart = Calendar.getInstance();
        calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultTimeStart[0]));
        calStart.set(Calendar.MINUTE, new Integer(defaultTimeStart[1]));
        calStart.set(Calendar.SECOND, 0);
        
        
        PmsBookingDateRange  range = new PmsBookingDateRange();
        range.start = calStart.getTime();
        
        if(getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.DAILY)) {
            calStart.add(Calendar.DAY_OF_YEAR, getConfiguration().minStay);
            calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultEndStart[0]));
            calStart.set(Calendar.MINUTE, new Integer(defaultEndStart[1]));
            calStart.set(Calendar.SECOND, 0);
        }
        if(getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.HOURLY)) {
            calStart.add(Calendar.HOUR, getConfiguration().minStay);
        }
        
        
        range.end = calStart.getTime(); 
        return range;
   }

    @Override
    public String addBookingItem(String bookingId, String item, Date start, Date end) {
        PmsBooking booking = getBooking(bookingId);
        
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemId = item;
        room.bookingItemTypeId = bookingEngine.getBookingItem(item).bookingItemTypeId;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.guests.add(new PmsGuests());
        
        Booking bookingToAdd = createBooking(room);
        List<Booking> bookingToAddList = new ArrayList();
        bookingToAddList.add(bookingToAdd);
        if(!bookingEngine.canAdd(bookingToAddList)) {
            return "The room can not be added, its not available.";
        }
        
        bookingEngine.addBookings(bookingToAddList);
        booking.rooms.add(room);
        booking.attachBookingItems(bookingToAddList);
        saveBooking(booking);
        return "";
    }

    private Booking createBooking(PmsBookingRooms room) {
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
        return bookingToAdd;
    }

    @Override
    public String getDefaultMessage(String bookingId) {
        PmsBooking booking = getBooking(bookingId);
        String message = getConfiguration().defaultMessage.get(booking.language);
        if(message == null) {
            return "";
        }
        return formatMessage(message, booking, null, null);
    }

    private boolean sameDayOrAfter(Date invoicedTo, Date endDate) {
        if(invoicedTo == null) {
            return false;
        }
        
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(invoicedTo);
        cal2.setTime(endDate);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        if(sameDay) {
            return true;
        }
        
        if(invoicedTo.after(endDate)) {
            return true;
        }
        return false;
    }

}
