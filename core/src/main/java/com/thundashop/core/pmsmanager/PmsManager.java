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
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ibm.icu.util.Calendar; 
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingEngineConfiguration;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshoplock.GetShopLockManager;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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
    private PmsConfiguration configuration = new PmsConfiguration();
    private List<String> repicientList = new ArrayList();
    private List<String> warnedAbout = new ArrayList();

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
    DoorManager arxManager;

    @Autowired
    StoreManager storeManager;

    @Autowired
    ProductManager productManager;

    @Autowired
    GetShopLockManager getShopLockManager;

    private String specifiedMessage = "";
    Date lastOrderProcessed;
    private List<PmsLog> logentries = new ArrayList();
    private boolean initFinalized = false;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking) dataCommon;
                booking.printInvoicedTo();
                bookings.put(booking.id, booking);
            }
            if (dataCommon instanceof PmsPricing) {
                prices = (PmsPricing) dataCommon;
            }
            if (dataCommon instanceof PmsConfiguration) {
                configuration = (PmsConfiguration) dataCommon;
            }
            if (dataCommon instanceof PmsLog) {
                PmsLog entry = (PmsLog) dataCommon;
                if(entry.logText.contains("Automarking booking as paid for, since no orders has been added") || entry.logText.equals("Booking saved / updated") || entry.logText.contains("booking has been deleted")) {
                    deleteObject(entry);
                } else {
                    logentries.add(entry);
                }
            }
            if (dataCommon instanceof PmsAdditionalItemInformation) {
                PmsAdditionalItemInformation res = (PmsAdditionalItemInformation) dataCommon;
                addiotionalItemInfo.put(res.itemId, res);
            }
        }
         
        createProcessor("logfetcher", ArxLogFetcher.class);
        createScheduler("pmsprocessor", "* * * * *", CheckPmsProcessing.class);
    }

    @Override
    public void setSession(Session session) {
        super.setSession(session); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    public List<Room> getAllRoomTypes(Date start, Date end) {
        List<Room> result = new ArrayList();
        List<BookingItemType> allGroups = bookingEngine.getBookingItemTypes();

        Collections.sort(allGroups, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        for (BookingItemType type : allGroups) {
            if (!type.visibleForBooking) {
                continue;
            }
            Room room = new Room();
            room.type = type;
            String couponcode = getCouponCode("");
            room.price = calculatePrice(type.id, start, end, true, couponcode, PmsBooking.PriceType.daily);
            result.add(room);
        }

        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        booking.sessionId = getSession().id;
        
        if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
            Coupon cop = cartManager.getCoupon(booking.couponCode);
            if(cop == null) {
                booking.couponCode = "";
            }
        }

        booking.priceType = prices.defaultPriceType;
        for (PmsBookingRooms room : booking.rooms) {
            if (room.bookingItemTypeId == null || room.bookingItemTypeId.isEmpty()) {
                if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    room.bookingItemTypeId = bookingEngine.getBookingItem(room.bookingItemId).bookingItemTypeId;
                }
            }
        }
        for (PmsBookingRooms room : booking.rooms) {
            int totalDays = 1;
            if (room.date.end != null && room.date.start != null) {
                totalDays = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(room.date.end)).getDays();
            }
            
            room.count = totalDays;
            String couponCode = getCouponCode(booking.couponCode);
            
            room.price = calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end, true, couponCode, booking.priceType);
            room.taxes = calculateTaxes(room.bookingItemTypeId);
            for (PmsGuests guest : room.guests) {
                if (guest.prefix != null) {
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                }
            }
        }

        if (booking.sessionStartDate == null) {
            PmsBookingDateRange range = getDefaultDateRange();
            booking.sessionStartDate = range.start;
            booking.sessionEndDate = range.end;
        }

        saveObject(booking);
        bookings.put(booking.id, booking);
    }

    @Override
    public PmsBooking getCurrentBooking() {
        if (getSession() == null) {
            System.out.println("Warning, no session set yet");
        }
        PmsBooking result = findBookingForSession();

        if (result == null) {
            result = startBooking();
        }

        if (result.sessionEndDate != null && result.sessionStartDate != null && result.sessionStartDate.after(result.sessionEndDate)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(result.sessionStartDate);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            result.sessionEndDate = cal.getTime();
        }

        return result;
    }

    @Override
    public PmsBooking startBooking() {
        PmsBooking currentBooking = findBookingForSession();

        if (currentBooking != null) {
            hardDeleteBooking(currentBooking);
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
                        if (phone.length() == 10 && phone.startsWith("07")) {
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

        HashMap<String, String> result = new HashMap();
        result.put("prefix", prefix);
        result.put("phone", phone);
        return result;
    }

    @Override
    public PmsBooking completeCurrentBooking() {
        PmsBooking booking = getCurrentBooking();
        if (!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }

        Integer result = 0;
        booking.isDeleted = false;
        List<Booking> bookingsToAdd = buildRoomsToAddToEngineList(booking);
        try {
            result = completeBooking(bookingsToAdd, booking);

            if (result == 0) {
                if (!configuration.payAfterBookingCompleted) {
                    if (bookingIsOK(booking)) {
                        if (!booking.confirmed) {
                            doNotification("booking_completed", booking, null);
                        } else {
                            doNotification("booking_confirmed", booking, null);
                        }
                    }
                }
            }

            try {
                if (!configuration.payAfterBookingCompleted) {
                    processor();
                } else {
                    createPrepaymentOrder(booking.id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return booking;
        } catch (Exception e) {
            messageManager.sendErrorNotification("Unknown booking exception occured for booking id: " + booking.id, e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String createPrepaymentOrder(String bookingId) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        NewOrderFilter filter = new NewOrderFilter();
        filter.prepayment = true;
        filter.startInvoiceFrom = booking.getStartDate();
        filter.endInvoiceAt = booking.getEndDate();
        if(addBookingToCart(booking, filter)) {
            Order order = createOrderFromCart(booking);
            booking.orderIds.add(order.id);
            saveBooking(booking);
            return order.id;
        }
        return "";
    }

    private Integer completeBooking(List<Booking> bookingsToAdd, PmsBooking booking) throws ErrorException {
        if (!canAdd(bookingsToAdd)) {
            return -2;
        }
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isCustomer()) {
            booking.userId = getSession().currentUser.id;
        }
        
        bookingEngine.addBookings(bookingsToAdd);
        booking.attachBookingItems(bookingsToAdd);
        booking.sessionId = null;
        if (booking.registrationData.resultAdded.get("company_invoicenote") != null) {
            booking.invoiceNote = booking.registrationData.resultAdded.get("company_invoicenote");
        }
        if (booking.userId == null || booking.userId.isEmpty()) {
            User newuser = createUser(booking);
            booking.userId = newuser.id;
            Company curcompany = createCompany(booking);
            if (curcompany != null) {
                curcompany = userManager.saveCompany(curcompany);
                newuser.company.add(curcompany.id);
                newuser.fullName = curcompany.name;
                newuser.emailAddress = curcompany.email;
                newuser.cellPhone = curcompany.phone;
                newuser.prefix = curcompany.prefix;
                newuser.address = curcompany.address;

                userManager.saveUserSecure(newuser);
            }
        } else if (configuration.autoconfirmRegisteredUsers) {
            booking.confirmed = true;
        }

        if (!configuration.needConfirmation) {
            booking.confirmed = true;
        }

        User loggedonuser = userManager.getLoggedOnUser();
        if (loggedonuser != null && configuration.autoconfirmRegisteredUsers) {
            booking.confirmed = true;
        }
        
        booking.sessionId = "";

        saveBooking(booking);
        System.out.println("Booking has been completed: " + booking.id);
        return 0;
    }

    private Date createInifinteDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 100);
        return cal.getTime();
    }

    private PmsBooking findBookingForSession() {
        if (getSession() == null) {
            return null;
        }
        String sessionId = getSession().id;
        if (sessionId == null) {
            return null;
        }

        for (PmsBooking booking : bookings.values()) {
            if (booking.sessionId != null && booking.sessionId.equals(sessionId)) {
                return booking;
            }
        }
        return null;
    }

    private boolean hasAccessUser(String userId) {
        User loggedOn = userManager.getLoggedOnUser();
        if (loggedOn.isAdministrator() || loggedOn.id.equals(userId)) {
            return true;
        }
        return false;
    }

    @Override
    public List<PmsBooking> getAllBookings(PmsBookingFilter filter) {
        if (!initFinalized) {
            finalizeList(new ArrayList(bookings.values()));
            initFinalized = true;
        }
        if (filter == null) {
            return finalizeList(new ArrayList(bookings.values()));
        }
        if (filter.state == null) {
            filter.state = 0;
        }

        List<PmsBooking> result = new ArrayList();

        if (filter.searchWord != null && !filter.searchWord.isEmpty()) {

            for (PmsBooking booking : bookings.values()) {
                User user = userManager.getUserById(booking.userId);
                if (user != null && user.fullName != null && user.fullName.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                    result.add(booking);
                } else if (booking.containsSearchWord(filter.searchWord)) {
                    result.add(booking);
                }

                for (PmsBookingRooms room : booking.rooms) {
                    if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                        BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                        if (item != null && item.bookingItemName != null && item.bookingItemName.contains(filter.searchWord)) {
                            result.add(booking);
                        }
                    }
                }
            }
        } else if (filter.filterType == null || filter.filterType.equals("registered")) {
            for (PmsBooking booking : bookings.values()) {
                if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("active")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.isActiveInPeriode(filter.startDate, filter.endDate)) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("uncofirmed")) {
            for (PmsBooking booking : bookings.values()) {
                if (!booking.confirmed) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("checkin")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.checkingInBetween(filter.startDate, filter.endDate)) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("checkout")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.checkingOutBetween(filter.startDate, filter.endDate)) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("deleted")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.isDeleted) {
                    result.add(booking);
                }
            }
        }

        removeNotConfirmed(filter, result);
        removeDeleted(filter, result);
        removeBeingProcessed(result);

        List<PmsBooking> finalized = finalizeList(result);

        finalized = sortList(finalized, filter.sorting);

        finalized = filterTypes(finalized, filter.typeFilter);
        finalized = filterByUser(finalized,filter.userId);

        return finalized;
    }

    private void removeNotConfirmed(PmsBookingFilter filter, List<PmsBooking> result) {
        List<PmsBooking> toRemove = new ArrayList();
        if (filter.needToBeConfirmed) { 
            for (PmsBooking booking : result) {
                if (!booking.confirmed) {
                    toRemove.add(booking);
                }
            }
        }
        result.removeAll(toRemove);
    }

    private List<PmsBooking> finalizeList(List<PmsBooking> result) {
        List<PmsBooking> finalized = new ArrayList();
        for (PmsBooking toReturn : result) {
            toReturn = finalize(toReturn);
            if (toReturn != null) {
                finalized.add(toReturn);
            }
        }
        checkForIncosistentBookings();
        return finalized;
    }

    public PmsBooking getBookingUnsecure(String bookingId) {
        PmsBooking booking = bookings.get(bookingId);
        if (booking == null) {
            return null;
        }
        return finalize(booking);
    }
    
    public PmsBooking getBooking(String bookingId) {
        PmsBooking booking = bookings.get(bookingId);
        checkSecurity(booking);
        if (booking == null) {
            return null;
        }
        return finalize(booking);
    }

    
    private String generateCode() {
        for (int i = 0; i < 100000; i++) {
            int start = 1;
            int end = 10;
            for (int j = 0; j < configuration.codeSize - 1; j++) {
                start *= 10;
                end *= 10;
            }
            end = end - 1;

            Integer newcode = new Random().nextInt(end - start) + start;
            if (!codeExist(newcode)) {
                return newcode.toString();
            }
        }
        System.out.println("Tried 100 000 times to generate a code without success");
        return null;
    }

    private boolean codeExist(int newcode) {
        for(PmsBooking booking : bookings.values()) {
            if(booking.rooms == null) {
                continue;
            }
            for (PmsBookingRooms room : booking.rooms) {
                if (room.code != null && (room.code.equals(newcode) && !room.isEnded())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public PmsBooking finalize(PmsBooking booking) {
        if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
            Calendar nowCal = Calendar.getInstance();
            nowCal.add(Calendar.HOUR_OF_DAY, -4);
            if (!booking.rowCreatedDate.after(nowCal.getTime())) {
                hardDeleteBooking(booking);
                return null;
            }
        }
        if (booking.isDeleted) {
            booking.state = 2;
            return booking;
        }
        if (booking.rooms == null) {
            booking.rooms = new ArrayList();
        }

        for (PmsBookingRooms room : booking.rooms) {
            if(room.code == null || room.code.isEmpty()) {
                room.code = generateCode();
                saveBooking(booking);
            }
            if (room.bookingId != null) {
                room.booking = bookingEngine.getBooking(room.bookingId);
                if (room.booking != null) {
                    room.date.start = room.booking.startDate;
                    room.date.end = room.booking.endDate;
                    room.bookingItemTypeId = room.booking.bookingItemTypeId;
                    room.bookingItemId = room.booking.bookingItemId;
                } else {
                    room.bookingItemId = null;
                }

                if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    room.item = bookingEngine.getBookingItem(room.bookingItemId);
                    room.bookingItemTypeId = room.item.bookingItemTypeId;
                }

                room.type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if (type != null) {
                    String productId = bookingEngine.getBookingItemType(room.bookingItemTypeId).productId;
                    if (productId != null) {
                        if (productManager.getProduct(productId).taxGroupObject != null) {
                            room.taxes = productManager.getProduct(productId).taxGroupObject.taxRate;
                        }
                    }
                }

            }
        }

        return booking;
    }


    private void checkForIncosistentBookings() {
        List<String> added = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            if (booking.rooms != null) {
                for (PmsBookingRooms room : booking.rooms) {
                    if (room.bookingId != null) {
                        added.add(room.bookingId);
                    }
                }
            }
        }

        List<Booking> allBookings = bookingEngine.getAllBookings();
        for (Booking booking : allBookings) {
            if (!added.contains(booking.id)) {
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

            String from = "none";
            if (room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if (type != null) {
                    from = type.name;
                }
            }
            String to = bookingEngine.getBookingItemType(newType).name;

            String logText = "Changed item type from : " + from + " to " + to;
            logEntry(logText, bookingId, null, roomId);
        } catch (BookingEngineException ex) {
            return ex.getMessage();
        }
        return "";

    }

    @Override
    public PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end) {
        PmsBooking booking = getBooking(bookingId);
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            bookingEngine.changeDatesOnBooking(room.bookingId, start, end);
            Date oldStart = room.date.start;
            Date oldEnd = room.date.end;

            room.date.start = start;
            room.date.end = end;
            room.date.exitCleaningDate = null;
            room.date.cleaningDate = null;
            saveBooking(booking);

            String logText = "New date set from " + convertToStandardTime(oldStart) + " - " + convertToStandardTime(oldEnd) + " to, " + convertToStandardTime(start) + " - " + convertToStandardTime(end);
            logEntry(logText, bookingId, null, roomId);
            return room;
        } catch (BookingEngineException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveBooking(PmsBooking booking) throws ErrorException {
        if (booking.id == null || booking.id.isEmpty() || bookings.get(booking.id) == null) {
            throw new ErrorException(1000015);
        }
        validatePhoneNumbers(booking);
        bookings.put(booking.id, booking);
        saveObject(booking);
    }

    private void validatePhoneNumbers(PmsBooking booking) {
        for (PmsBookingRooms room : booking.rooms) {
            for (PmsGuests guest : room.guests) {
                HashMap<String, String> result = validatePhone("+" + guest.prefix + "" + guest.phone, "no");
                if (result != null) {
                    guest.prefix = result.get("prefix").replace("+", "");
                    guest.phone = result.get("phone");
                }
            }
        }
    }

    @Override
    public String setBookingItem(String roomId, String bookingId, String itemId) {
        PmsBooking booking = getBooking(bookingId);
        if (booking == null) {
            return "Booking does not exists";
        }
        try {
             PmsBookingRooms room = booking.findRoom(roomId);
            if (room == null) {
                return "Room does not exists";
            }

            checkIfRoomShouldBeUnmarkedDirty(room, booking.id);
            bookingEngine.changeBookingItemOnBooking(room.bookingId, itemId);
            resetBookingItem(room, itemId, booking);
            finalize(booking);

            String from = "none";
            if (room.bookingItemId != null) {
                BookingItem oldItem = bookingEngine.getBookingItem(room.bookingItemId);
                if (oldItem != null) {
                    from = oldItem.bookingItemName;
                }
            }

            String logText = "Changed room to " + bookingEngine.getBookingItem(itemId).bookingItemName + " from " + from;
            logEntry(logText, bookingId, null, roomId);
            doNotification("room_changed", booking, room);
        } catch (BookingEngineException ex) {
            return ex.getMessage();
        }
        saveBooking(booking);
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
        prices.pricesExTaxes = newPrices.pricesExTaxes;
        prices.privatePeopleDoNotPayTaxes = newPrices.privatePeopleDoNotPayTaxes;
        for (String typeId : newPrices.dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = newPrices.dailyPrices.get(typeId);
            for (String date : priceMap.keySet()) {
                HashMap<String, Double> existingPriceRange = prices.dailyPrices.get(typeId);
                if (existingPriceRange == null) {
                    existingPriceRange = new HashMap();
                    prices.dailyPrices.put(typeId, existingPriceRange);
                }
                existingPriceRange.put(date, priceMap.get(date));
            }
        }
        saveObject(prices);

        logEntry("Prices updated", null, null);

        return prices;
    }

    private Double calculatePrice(String typeId, Date start, Date end, boolean avgPrice, String couponCode, Integer priceType) {
        double price = 0.0;
        if (prices.defaultPriceType == 1) {
            price = calculateDailyPricing(typeId, start, end, avgPrice);
        }
        if (prices.defaultPriceType == 2) {
            price = calculateMonthlyPricing(typeId);
        }
        if (prices.defaultPriceType == 7) {
            price = calculateProgressivePrice(typeId, start, end, 0, avgPrice, priceType);
        }
        if (prices.defaultPriceType == 8) {
            price = calculateIntervalPrice(typeId, start, end, avgPrice);
        }
        
        if(couponCode != null && !couponCode.isEmpty()) {
            price = cartManager.calculatePriceForCoupon(couponCode, price);
        }
        
        return price;
    }

    @Override
    public String createOrder(String bookingId, NewOrderFilter filter) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        
        if(addBookingToCart(booking, filter)) {
            Order order = createOrderFromCart(booking);
            if (order == null) {
                return "Could not create order.";
            }
            booking.orderIds.add(order.id);
            booking.payedFor = false;
            saveBooking(booking);
        }
        return "";
    }

    private boolean addBookingToCart(PmsBooking booking, NewOrderFilter filter) {
        cartManager.clear();

        boolean foundInvoice = false;
        double totalprice = 0;
        for (PmsBookingRooms room : booking.rooms) {
            if(!room.needInvoicing(filter)) {
                continue;
            }
            
            Date startDate = room.getInvoiceStartDate(filter);
            filter.startInvoiceFrom = startDate;
            Date endDate = room.getInvoiceEndDate(filter, booking);

            int daysInPeriode = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
            if(booking.priceType.equals(PmsBooking.PriceType.monthly)) {
                daysInPeriode = getNumberOfMonthsBetweenDates(startDate, endDate);
            }
            Double price = getPriceInPeriode(booking, room, startDate, endDate);
            CartItem item = createCartItem(room, startDate, endDate);
            if (item == null) {
                return false;
            }
            
            if (configuration.substractOneDayOnOrder && !filter.onlyEnded) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(item.endDate);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                item.endDate = cal.getTime();
            }
            
            boolean includeTaxes = true;
            if(prices.privatePeopleDoNotPayTaxes) {
                User user = userManager.getUserById(booking.userId);
                if(user.company.isEmpty()) {
                    includeTaxes = false;
                } else {
                    Company company = userManager.getCompany(user.company.get(0));
                    includeTaxes = company.vatRegisterd;
                }
            }
            
            if (prices.pricesExTaxes && includeTaxes) {
                double tax = 1 + (calculateTaxes(room.bookingItemTypeId) / 100);
                //Order price needs to be inc taxes..
                price *= tax;
            }

            String guestName = "";
            if (room.guests.size() > 0) {
                guestName = room.guests.get(0).name;
            }

            totalprice += price;
            
            item.getProduct().discountedPrice = price;
            item.getProduct().price = price;
            item.getProduct().metaData = guestName;
            item.getProduct().externalReferenceId = room.pmsBookingRoomId;
            item.setCount(daysInPeriode);
            room.invoicedTo = endDate;
            foundInvoice = true;
            cartManager.saveCartItem(item);
        }
        
        if(totalprice == 0.0) {
            return false;
        }
        
        return foundInvoice;
    }

    private int getNumberOfDays(PmsBookingRooms room, Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(room.date.start);
        int days = 0;
        while (true) {
            if (cal.getTime().after(startDate) && (cal.getTime().before(endDate) || cal.getTime().equals(endDate))) {
                days++;
            }

            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getTime().after(room.date.end)) {
                break;
            }
        }
        return days;
    }

    @Override
    public PmsConfiguration getConfiguration() {
        Gson gson = new Gson();
        String copy = gson.toJson(configuration);

        User loggedOn = getSession().currentUser;
        if (loggedOn != null && loggedOn.isAdministrator()) {
            return configuration;
        }

        PmsConfiguration toReturn = gson.fromJson(copy, PmsConfiguration.class);
        toReturn.arxUsername = "";
        toReturn.arxPassword = "";
        toReturn.arxHostname = "";

        return toReturn;
    }

    @Override
    public void saveConfiguration(PmsConfiguration notifications) {
        this.configuration = notifications;
        saveObject(notifications);
        logEntry("Configuration updated", null, null);
    }

    @Administrator
    public void doNotification(String key, String bookingId) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        doNotification(key, booking, null);
    }

    public void doNotification(String key, PmsBooking booking, PmsBookingRooms room) {
        repicientList.clear();
        key = key + "_" + booking.language;
        String message = notify(key, booking, "sms", room);
        List<String> smsRecp = repicientList;
        String repssms = "";
        for (String rep : smsRecp) {
            repssms += rep + ", ";
        }
        repicientList.clear();

        String message2 = notify(key, booking, "email", room);
        notifyAdmin(key, booking);
        specifiedMessage = "";
        List<String> emailRecp = repicientList;

        String repemail = "";
        for (String rep : emailRecp) {
            repemail += rep + ", ";
        }

        if (message != null && !message.isEmpty()) {
            logEntry("Sms notification: " + key + " Message: " + message + " recipients: " + repssms, booking.id, null);
        }
        if (message2 != null && !message2.isEmpty()) {
            logEntry("Email notification: " + key + " Message: " + message2 + " recipients: " + repemail, booking.id, null);
        }
    }

    private String notify(String key, PmsBooking booking, String type, PmsBookingRooms room) {
        String message = configuration.smses.get(key);
        if (type.equals("email")) {
            message = configuration.emails.get(key);
            if (message != null) {
                message = configuration.emailTemplate.replace("{content}", message);
                message = message.replace("\n", "<br>\n");
            }
        }
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = formatMessage(message, booking, room, null);
        if (room != null) {
            notifyGuest(booking, message, type, key, room);
        } else {
            notifyBooker(booking, message, type, key);
        }
        return message;
    }

    private void notifyBooker(PmsBooking booking, String message, String type, String key) throws ErrorException {
        User user = userManager.getUserById(booking.userId);
        if (type.equals("sms")) {
            messageManager.sendSms("sveve", user.cellPhone, message, user.prefix, configuration.smsName);
            repicientList.add(user.cellPhone);
        } else {
            String title = configuration.emailTitles.get(key);
            String fromName = getFromName();
            String fromEmail = getFromEmail();
            if (key.startsWith("booking_confirmed")) {
                HashMap<String, String> attachments = createICalEntry(booking);
                messageManager.sendMailWithAttachments(user.emailAddress, user.fullName, title, message, fromEmail, fromName, attachments);
            } else {
                messageManager.sendMail(user.emailAddress, user.fullName, title, message, fromEmail, fromName);
            }

            if (configuration.copyEmailsToOwnerOfStore) {
                String copyadress = storeManager.getMyStore().configuration.emailAdress;
                messageManager.sendMail(copyadress, user.fullName, title, message, fromEmail, fromName);
            }

            repicientList.add(user.emailAddress);
        }
    }

    private String notifyGuest(PmsBooking booking, String message, String type, String key, PmsBookingRooms roomToNotify) {
        for (PmsBookingRooms room : booking.rooms) {
            if (roomToNotify != null) {
                if (!room.pmsBookingRoomId.equals(roomToNotify.pmsBookingRoomId)) {
                    continue;
                }
            }
            
            if(room.guests == null || room.guests.isEmpty() || !bookingEngine.getConfig().rules.includeGuestData) {
                if (type.equals("email")) {
                    String email = userManager.getUserById(booking.userId).emailAddress;
                    String name = userManager.getUserById(booking.userId).fullName;
                    String title = configuration.emailTitles.get(key);
                    title = formatMessage(message, booking, room, null);
                    messageManager.sendMail(email, name, title, message, getFromEmail(), getFromName());
                    repicientList.add(email);
                } else {
                    String phone = userManager.getUserById(booking.userId).cellPhone;
                    String prefix = userManager.getUserById(booking.userId).prefix;
                    messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
                    repicientList.add(phone);
                }
            } else {
                for (PmsGuests guest : room.guests) {
                    if (type.equals("email")) {
                        String email = guest.email;
                        if (email == null || email.isEmpty()) {
                            logEntry("Email not sent due to no email set for guest " + guest.name, booking.id, null);
                            continue;
                        }
                        String title = configuration.emailTitles.get(key);
                        title = formatMessage(message, booking, room, guest);
                        messageManager.sendMail(guest.email, guest.name, title, message, getFromEmail(), getFromName());
                        repicientList.add(email);
                    } else {
                        String phone = guest.phone;
                        if (phone == null || phone.isEmpty()) {
                            logEntry("Sms not sent due to no phone number set for guest " + guest.name, booking.id, null);
                            continue;
                        }

                        messageManager.sendSms("sveve", phone, message, guest.prefix, configuration.smsName);
                        repicientList.add(phone);
                    }
                }
            }
        }
        return message;
    }

    private void notifyAdmin(String key, PmsBooking booking) {
        String message = configuration.adminmessages.get(key);
        if (message == null) {
            return;
        }

        message = formatMessage(message, booking, null, null);
        String email = storeManager.getMyStore().configuration.emailAdress;
        String phone = storeManager.getMyStore().configuration.phoneNumber;
        
        if(!configuration.sendAdminTo.isEmpty()) {
            email = configuration.sendAdminTo;
        }
        
        messageManager.sendMail(email, "Administrator", "Notification", message, getFromEmail(), getFromName());
        messageManager.sendSms("sveve", phone, message, "47");
    }

    private String formatMessage(String message, PmsBooking booking, PmsBookingRooms room, PmsGuests guest) {
        PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter();

        if (this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }

        if (room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
        }
        message = formater.formatContactData(message, userManager.getUserById(booking.userId), null);
        message = formater.formatBookingData(message, booking, bookingEngine);

        message = message.replace("{extrafield}", configuration.extraField);

        return message;
    }

    @Override
    public void confirmBooking(String bookingId, String message) {
        this.specifiedMessage = message;
        PmsBooking booking = getBookingUnsecure(bookingId);
        booking.confirmed = true;
        saveBooking(booking);
        doNotification("booking_confirmed", booking, null);
        logEntry("Confirmed booking", bookingId, null);
    }

    @Override
    public void unConfirmBooking(String bookingId, String message) {
        this.specifiedMessage = message;
        PmsBooking booking = getBookingUnsecure(bookingId);
        booking.unConfirmed = true;
        saveBooking(booking);
        deleteBooking(bookingId);

        doNotification("booking_notconfirmed", booking, null);
        logEntry("Booking rejected", bookingId, null);
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
        for (PmsBookingRooms room : booking.rooms) {
            if (room.date.start != null) {
                if (start == null) {
                    start = room.date.start;
                } else if (start.after(room.date.start)) {
                    start = room.date.start;
                }
            }
            if (room.date.end != null) {
                if (end == null) {
                    end = room.date.end;
                } else if (end.before(room.date.end)) {
                    end = room.date.end;
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
        PmsBooking booking = getBookingUnsecure(bookingId);
        checkSecurity(booking);
        List<PmsBookingRooms> toRemove = new ArrayList();
        String roomName = "";
        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(roomId)) {
                if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    roomName = bookingEngine.getBookingItem(room.bookingItemId).bookingItemName + " (" + convertToStandardTime(room.date.start) + " - " + convertToStandardTime(room.date.end) + ")";
                } else {
                    roomName = bookingEngine.getBookingItemType(room.bookingItemTypeId).name + " (" + convertToStandardTime(room.date.start) + " - " + convertToStandardTime(room.date.end) + ")";
                }
                toRemove.add(room);
            }
        }
        for (PmsBookingRooms remove : toRemove) {
            bookingEngine.deleteBooking(remove.bookingId);
            booking.rooms.remove(remove);
        }
        saveObject(booking);

        logEntry(roomName + " removed from booking ", bookingId, null);
        if(booking.rooms.isEmpty()) {
            deleteBooking(booking.id);
        }
    }

    @Override
    public void removeFromCurrentBooking(String roomId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        ArrayList toRemove = new ArrayList();
        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(roomId)) {
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

        for (PmsBooking res : result) {
            allBookings.add(res.copyUnsecure());
        }

        return allBookings;
    }

    @Override
    public String getContract(String bookingId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        if (booking == null || !booking.id.equals(bookingId)) {
            booking = getBookingUnsecure(bookingId);
        }
        if (booking == null) {
            return "Booking could not be found";
        }
        String contract = configuration.contracts.get(booking.language);
        if (contract == null) {
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
        for (PmsBookingRooms room : booking.rooms) {
            if (room.bookingId != null && !room.bookingId.isEmpty()) {
                bookingEngine.deleteBooking(room.bookingId);
            }
        }
        booking.isDeleted = true;
        saveBooking(booking);
    }

    
    private void hardDeleteBooking(PmsBooking booking) {
        bookings.remove(booking.id);
        deleteObject(booking);
}
    
    private void removeDeleted(PmsBookingFilter filter, List<PmsBooking> result) {
        if (filter != null && filter.filterType != null && filter.filterType.equalsIgnoreCase("deleted")) {
            return;
        }
        List<PmsBooking> toRemove = new ArrayList();
        if (!filter.includeDeleted) {
            for (PmsBooking booking : result) {
                if (booking.isDeleted) {
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
        for (BookingItemType type : bookingEngine.getBookingItemTypes()) {
            BookingTimeLineFlatten line = bookingEngine.getTimelines(type.id, filter.start, filter.end);
            res.typeTimeLines.put(type.id, line.getTimelines(filter.interval));
        }

        List<BookingItem> items = bookingEngine.getBookingItems();

        for (BookingItem item : items) {
            BookingTimeLineFlatten line = bookingEngine.getTimeLinesForItem(filter.start, filter.end, item.id);
            List<BookingTimeLine> timelines = line.getTimelines(filter.interval);
            LinkedHashMap<Long, Integer> itemCountLine = new LinkedHashMap();
            for (BookingTimeLine tl : timelines) {
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
        if (result == null) {
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

        if (bookingStartingToday || !itemInUse) {
            //Only mark room cleaned if a new booking is 
            additional.markCleaned();
        } else {
            additional.addCleaningDate();
        }
        saveAdditionalInfo(additional);

        String logText = "Marked room as cleaned, item in use: ";
        if (itemInUse) {
            logText += " in use";
        } else {
            logText += " not in use";
        }
        logEntry(logText, null, additional.itemId);
    }

    void markRoomAsDirty(String bookingItemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(bookingItemId);
        additional.markDirty();
        saveAdditionalInfo(additional);

        BookingItem item = bookingEngine.getBookingItem(additional.itemId);
        if (item != null) {
            String logText = "Marked room as dirty, item in use";
            logEntry(logText, null, additional.itemId);
        }
    }

    @Override
    public List<PmsAdditionalItemInformation> getAllAdditionalInformationOnRooms() {
        List<PmsAdditionalItemInformation> result = new ArrayList();
        List<BookingItem> items = bookingEngine.getBookingItems();
        for (BookingItem item : items) {
            result.add(finalizeAdditionalItem(getAdditionalInfo(item.id)));
        }
        return result;
    }

    private PmsAdditionalItemInformation finalizeAdditionalItem(PmsAdditionalItemInformation additionalInfo) {
        Calendar start = Calendar.getInstance();
        Calendar end = start.getInstance();
        end.add(Calendar.DAY_OF_YEAR, 1);

        additionalInfo.isClean();
        additionalInfo.inUse = bookingEngine.itemInUseBetweenTime(start.getTime(), end.getTime(), additionalInfo.itemId);
        return additionalInfo;
    }

    @Override
    public List<PmsBookingRooms> getRoomsNeedingCheckoutCleaning(Date day) {
        List<PmsBookingRooms> result = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            if (booking.isDeleted) {
                continue;
            }
            if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
                continue;
            }

            for (PmsBookingRooms room : booking.rooms) {
                if (needCheckOutCleaning(room, day)) {
                    finalize(booking);
                    result.add(room);
                }
            }
        }

        sortRooms(result);

        return result;
    }

    @Override
    public List<PmsBookingRooms> getRoomsNeedingIntervalCleaning(Date day) {

        List<PmsBookingRooms> rooms = new ArrayList<PmsBookingRooms>();
        for (PmsBooking booking : bookings.values()) {
            if (booking.isDeleted) {
                continue;
            }
            if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
                continue;
            }
            for (PmsBookingRooms room : booking.rooms) {
                if (needIntervalCleaning(room, day)) {
                    finalize(booking);
                    rooms.add(room);
                }
            }
        }
        sortRooms(rooms);

        return rooms;
    }

    public boolean needIntervalCleaning(PmsBookingRooms room, Date day) {
        if (room.date.cleaningDate == null) {
            room.date.cleaningDate = room.date.start;
        }
        if (room.isEndingToday(day)) {
            return false;
        }
        if (!room.isActiveOnDay(day)) {
            return false;
        }
        int days = Days.daysBetween(new LocalDate(room.date.cleaningDate), new LocalDate(day)).getDays();
        if (days == 0) {
            return false;
        }
        int interval = configuration.cleaningInterval;
        if (room.intervalCleaning != null) {
            interval = room.intervalCleaning;
        }
        if (interval == 0) {
            return false;
        }
        if (days % interval == 0) {
            return true;
        }
        return false;
    }

    private HashMap<String, String> createICalEntry(PmsBooking booking) {
        ICalendar ical = new ICalendar();
        for (PmsBookingRooms room : booking.rooms) {
            VEvent event = new VEvent();
            String title = "Booking of";

            if (room.booking != null && room.booking.bookingItemId != null && bookingEngine.getBookingItem(room.booking.bookingItemId) != null) {
                title += " room " + bookingEngine.getBookingItem(room.booking.bookingItemId).bookingItemName;
            } else {
                title += " " + bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            }

            Summary summary = event.setSummary(title);
            summary.setLanguage("en-us");

            event.setDateStart(room.date.start);

            int minutes = (int) ((room.date.end.getTime() / 60000) - (room.date.start.getTime() / 60000));

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachments;
    }

    private List<PmsBooking> sortList(List<PmsBooking> result, String sorting) {
        if (sorting == null) {
            sorting = "";
        }

        if (sorting.equals("visitor") || sorting.equals("visitor_desc")) {
//            Collections.sort(result, new Comparator<PmsBooking>(){
//                public int compare(PmsBooking o1, PmsBooking o2){
//                    return o1.rooms.get(0).guests.get(0).name.compareTo(o2.rooms.get(0).guests.get(0).name);
//                }
//            });
        } else if (sorting.equals("periode") || sorting.equals("periode_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    return o1.rooms.get(0).date.start.compareTo(o2.rooms.get(0).date.start);
                }
            });
        } else if (sorting.equals("room") || sorting.equals("room_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    if (o1.rooms == null || o1.rooms.isEmpty() || o1.rooms.get(0).item == null) {
                        return -1;
                    }
                    if (o2.rooms == null || o2.rooms.isEmpty() || o2.rooms.get(0).item == null) {
                        return -1;
                    }
                    return o1.rooms.get(0).item.bookingItemName.compareTo(o2.rooms.get(0).item.bookingItemName);
                }
            });
        } else if (sorting.equals("price") || sorting.equals("price_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    return o1.rooms.get(0).price.compareTo(o2.rooms.get(0).price);
                }
            });
        } else {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
                }
            });
        }

        if (sorting.contains("_desc")) {
            Collections.reverse(result);
        }

        return result;
    }

    private List<PmsBooking> filterTypes(List<PmsBooking> finalized, List<String> typeFilter) {
        if (typeFilter == null || typeFilter.isEmpty()) {
            return finalized;
        }

        List<PmsBooking> result = new LinkedList();
        for (PmsBooking booking : finalized) {
            boolean add = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (room.bookingItemTypeId != null && typeFilter.contains(room.bookingItemTypeId)) {
                    add = true;
                }
            }
            if (add) {
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
        if (result.get("prefix") != null) {
            user.prefix = result.get("user_prefix");
        }

        user.address = new Address();
        user.address.address = result.get("user_address_address");
        user.address.postCode = result.get("user_address_postCode");
        user.address.city = result.get("user_address_city");
        user.address.countrycode = result.get("user_address_countrycode");
        user.address.countryname = result.get("user_address_countryname");

        if (user.emailAddress == null) {
            user.emailAddress = "";
        }
        if (user.fullName == null) {
            user.fullName = "";
        }
        if (user.cellPhone == null) {
            user.cellPhone = "";
        }
        if (user.prefix == null) {
            user.prefix = "47";
        }
        if (user.address.address == null) {
            user.address.address = "";
        }
        if (user.address.postCode == null) {
            user.address.postCode = "";
        }
        if (user.address.city == null) {
            user.address.city = "";
        }
        if (user.address.countrycode == null) {
            user.address.countrycode = "";
        }
        if (user.address.countryname == null) {
            user.address.countryname = "";
        }

        return userManager.createUser(user);
    }

    private Company createCompany(PmsBooking booking) {
        LinkedHashMap<String, String> result = booking.registrationData.resultAdded;
        if (result.get("choosetyperadio") == null || result.get("choosetyperadio").equals("registration_private")) {
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
        if (result.get("company_vatRegistered") != null) {
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

        if (data.repeattype.equals("repeat")) {
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
        booking.bookingItemTypeId = data.bookingTypeId;
        booking.startDate = data.data.firstEvent.start;
        booking.endDate = data.data.firstEvent.end;

        if (data.bookingTypeId == null || data.bookingTypeId.isEmpty()) {
            booking.bookingItemTypeId = bookingEngine.getBookingItem(data.bookingItemId).bookingItemTypeId;
        }

        List<Booking> toCheck = new ArrayList();
        toCheck.add(booking);
        PmsBooking curBooking = getCurrentBooking();

        PmsBookingRooms room = new PmsBookingRooms();
        room.date.start = data.data.firstEvent.start;
        room.date.end = data.data.firstEvent.end;
        room.bookingItemId = booking.bookingItemId;
        room.bookingItemTypeId = booking.bookingItemTypeId;
        room.canBeAdded = canAdd(toCheck);
        curBooking.rooms.add(room);

        curBooking.lastRepeatingData = data;
        saveBooking(curBooking);

        return toReturn;
    }

    public List<PmsBookingRooms> getRoomsFromRepeaterData(PmsRepeatingData data) {
        TimeRepeater repeater = new TimeRepeater();
        LinkedList<TimeRepeaterDateRange> lines = repeater.generateRange(data.data);

        List<PmsBookingRooms> allRooms = new ArrayList();

        for (TimeRepeaterDateRange line : lines) {

            Booking booking = new Booking();
            booking.bookingItemId = data.bookingItemId;
            booking.bookingItemTypeId = data.bookingTypeId;
            booking.startDate = line.start;
            booking.endDate = line.end;

            if (data.bookingTypeId == null || data.bookingTypeId.isEmpty()) {
                booking.bookingItemTypeId = bookingEngine.getBookingItem(data.bookingItemId).bookingItemTypeId;
            }
            PmsBookingRooms room = new PmsBookingRooms();
            room.date.start = line.start;
            room.date.end = line.end;
            room.bookingItemId = booking.bookingItemId;
            room.bookingItemTypeId = booking.bookingItemTypeId;
            room.addedByRepeater = true;
            room.booking = booking;
            booking.externalReference = room.pmsBookingRoomId;
            if (booking.bookingItemId == null) {
                booking.bookingItemId = "";
            }

            List<Booking> toCheck = new ArrayList();
            toCheck.add(booking);
            if (!canAdd(toCheck)) {
                room.canBeAdded = false;
            }

            allRooms.add(room);
        }
        return allRooms;
    }

    private List<TimeRepeaterDateRange> addRepeatingRooms(PmsRepeatingData data) throws ErrorException {
        List<PmsBookingRooms> allRooms = getRoomsFromRepeaterData(data);

        PmsBooking curBooking = getCurrentBooking();
        allRooms = excludeAlreadyAdded(allRooms, curBooking);

        curBooking.rooms.addAll(allRooms);
        curBooking.lastRepeatingData = data;
        saveBooking(curBooking);
        List<TimeRepeaterDateRange> cantAdd = new ArrayList();
        for (PmsBookingRooms room : allRooms) {
            if (!room.canBeAdded) {
                TimeRepeaterDateRange cant = new TimeRepeaterDateRange();
                cant.start = room.date.start;
                cant.end = room.date.end;
                cantAdd.add(cant);
            }
        }

        return cantAdd;
    }

    private void removeRepeatedRooms(PmsBooking curBooking) {
        List<PmsBookingRooms> toRemove = new ArrayList();
        for (PmsBookingRooms room : curBooking.rooms) {
            if (room.addedByRepeater) {
                toRemove.add(room);
            }
        }
        curBooking.rooms.removeAll(toRemove);
    }

    private Double calculateDailyPricing(String typeId, Date start, Date end, boolean avgPrice) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);

        if (priceRange == null) {
            return 0.0;
        }

        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = 0;
        Double total = 0.0;
        while (true) {
            days++;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String dateToUse = formatter.format(cal.getTime());
            if (priceRange.get(dateToUse) != null) {
                total += priceRange.get(dateToUse);
            } else {
                total += defaultPrice;
            }
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
        }
        if (avgPrice) {
            total = total / days;
        }
        return total;
    }

    private Double calculateIntervalPrice(String typeId, Date start, Date end, boolean avgprice) {
        int totalDays = Days.daysBetween(new LocalDate(start), new LocalDate(end)).getDays();
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.124;
        }
        int daysoffset = 0;
        for (ProgressivePriceAttribute attr : priceRange) {
            daysoffset += attr.numberOfTimeSlots;
            if (daysoffset >= totalDays) {
                if (avgprice) {
                    return attr.price;
                } else {
                    return attr.price;
                }
            }
        }

        //Could not find price to use.
        return -0.333;
    }

    private Double calculateProgressivePrice(String typeId, Date start, Date end, int offset, boolean avgPrice, Integer priceType) {
        ArrayList<ProgressivePriceAttribute> priceRange = prices.progressivePrices.get(typeId);
        if (priceRange == null) {
            System.out.println("No progressive price found for type");
            return -0.123;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int days = offset;
        Double total = 0.0;
        while (true) {
            int daysoffset = 0;
            for (ProgressivePriceAttribute attr : priceRange) {
                daysoffset += attr.numberOfTimeSlots;
                if (daysoffset > days) {
                    total += attr.price;
                    daysoffset = 0;
                    break;
                }
            }
            days++;
            if(priceType.equals(PmsBooking.PriceType.daily)) { cal.add(Calendar.DAY_OF_YEAR, 1); }
            else if(priceType.equals(PmsBooking.PriceType.weekly)) { cal.add(Calendar.DAY_OF_YEAR, 7); }
            else if(priceType.equals(PmsBooking.PriceType.monthly)) { cal.add(Calendar.MONTH, 1); }
            else if(priceType.equals(PmsBooking.PriceType.hourly)) { cal.add(Calendar.HOUR, 1); }
            else { cal.add(Calendar.DAY_OF_YEAR, 1); }
            if (end == null || cal.getTime().after(end) || cal.getTime().equals(end)) {
                break;
            }
            if(priceType.equals(PmsBooking.PriceType.daily) || priceType.equals(PmsBooking.PriceType.progressive)) { 
                if(isSameDay(cal.getTime(), end)) {
                    break;
                }
            }

        }

        if (avgPrice) {
            total = total / days;
        }
        return total;
    }

    private double calculateTaxes(String bookingItemTypeId) {
        BookingItemType item = bookingEngine.getBookingItemType(bookingItemTypeId);
        if (item.productId != null && !item.productId.isEmpty()) {
            Product product = productManager.getProduct(item.productId);
            if (product.taxGroupObject != null) {
                return product.taxGroupObject.taxRate;
            }
            return -1.0;
        }
        return -2.0;
    }

    private CartItem createCartItem(PmsBookingRooms room, Date startDate, Date endDate) {
        BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
        BookingItem bookingitem = null;
        if (room.bookingItemId != null) {
            bookingitem = bookingEngine.getBookingItem(room.bookingItemId);
        }
        if (type == null) {
            return null;
        }

        String productId = type.productId;
        if (productId == null) {
            System.out.println("Product not set for this booking item type");
            return null;
        }
        int numberOfDays = getNumberOfDays(room, startDate, endDate);
        if (numberOfDays == 0) {
            return null;
        }

        CartItem item = cartManager.addProductItem(productId, 1);
        item.startDate = startDate;
        item.endDate = endDate;
        
        item.getProduct().name = type.name;
        if (bookingitem != null) {
            item.getProduct().additionalMetaData = bookingitem.bookingItemName;
        }
        return item;
    }

    @Override
    public List<Integer> getAvailabilityForType(String bookingTypeId, Date startTime, Date endTime, Integer intervalInMinutes) {
        LinkedList<TimeRepeaterDateRange> lines = createAvailabilityLines(bookingTypeId);

        DateTime timer = new DateTime(startTime);
        List<Integer> result = new ArrayList();
        while (true) {
            if (hasRange(lines, timer)) {
                result.add(1);
            } else {
                result.add(0);
            }
            timer = timer.plusMinutes(intervalInMinutes);
            if (timer.toDate().after(endTime) || timer.toDate().equals(endTime)) {
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
        if (morning) {
            return date.getTime();
        }
        date.set(Calendar.HOUR_OF_DAY, 22);

        return date.getTime();
    }

    private LinkedList<TimeRepeaterDateRange> createAvailabilityLines(String bookingItemId) {
        if (bookingItemId != null && bookingItemId.isEmpty()) {
            bookingItemId = null;
        }
        LinkedList<TimeRepeaterDateRange> result = new LinkedList<TimeRepeaterDateRange>();
        List<TimeRepeaterData> repeaters = bookingEngine.getOpeningHours(bookingItemId);
        if (repeaters.isEmpty()) {
            repeaters = bookingEngine.getOpeningHours(null);
        }
        for (TimeRepeaterData repeater : repeaters) {
            TimeRepeater generator = new TimeRepeater();
            result.addAll(generator.generateRange(repeater));
        }

        return result;
    }

    private boolean hasRange(LinkedList<TimeRepeaterDateRange> lines, DateTime timer) {
        for (TimeRepeaterDateRange line : lines) {
            if (line.isBetweenTime(timer.toDate())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toggleAddon(String itemId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        if (booking.bookingEngineAddons.contains(itemId)) {
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
        for (PmsBookingRooms room : booking.rooms) {
            if (room.isAddon) {
                toRemove.add(room);
            }
        }

        booking.rooms.removeAll(toRemove);

        List<PmsBookingRooms> allToAdd = new ArrayList();
        for (String addonId : booking.bookingEngineAddons) {
            for (PmsBookingRooms room : booking.rooms) {
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
                if (!canAdd(checkToAdd)) {
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

        PmsBookingDateRange range = new PmsBookingDateRange();
        range.start = calStart.getTime();

        if (getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.DAILY)) {
            calStart.add(Calendar.DAY_OF_YEAR, getConfiguration().minStay);
            calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultEndStart[0]));
            calStart.set(Calendar.MINUTE, new Integer(defaultEndStart[1]));
            calStart.set(Calendar.SECOND, 0);
        }
        if (getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.HOURLY)) {
            calStart.add(Calendar.HOUR, getConfiguration().minStay);
        }

        range.end = calStart.getTime();
        return range;
    }

    @Override
    public String addBookingItem(String bookingId, String item, Date start, Date end) {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms room = new PmsBookingRooms();
        BookingItem itemToAdd = bookingEngine.getBookingItem(item);
        room.bookingItemId = item;
        room.bookingItemTypeId = itemToAdd.bookingItemTypeId;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.guests.add(new PmsGuests());

        Booking bookingToAdd = createBooking(room);
        List<Booking> bookingToAddList = new ArrayList();
        bookingToAddList.add(bookingToAdd);
        if (!canAdd(bookingToAddList)) {
            return "The room can not be added, its not available.";
        }

        bookingEngine.addBookings(bookingToAddList);
        booking.rooms.add(room);
        booking.attachBookingItems(bookingToAddList);
        saveBooking(booking);

        logEntry(itemToAdd.bookingItemName + " added to booking " + " time: " + convertToStandardTime(start) + " " + convertToStandardTime(end), bookingId, item);
        return "";
    }

    @Override
    public String addBookingItemType(String bookingId, String type, Date start, Date end) {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms room = new PmsBookingRooms();
        BookingItemType typeToAdd = bookingEngine.getBookingItemType(type);
        room.bookingItemTypeId = type;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.guests.add(new PmsGuests());

        Booking bookingToAdd = createBooking(room);
        List<Booking> bookingToAddList = new ArrayList();
        bookingToAddList.add(bookingToAdd);
        if (!canAdd(bookingToAddList)) {
            return "The room can not be added, its not available.";
        }

        bookingEngine.addBookings(bookingToAddList);
        booking.rooms.add(room);
        booking.attachBookingItems(bookingToAddList);
        saveBooking(booking);

        logEntry(typeToAdd.name + " added to booking " + " time: " + convertToStandardTime(start) + " " + convertToStandardTime(end), bookingId, null);
        processor();
        return "";
    }

    private Booking createBooking(PmsBookingRooms room) {
        Booking bookingToAdd = new Booking();
        bookingToAdd.startDate = room.date.start;
        if (room.date.end == null) {
            room.date.end = createInifinteDate();
        }
        if (room.numberOfGuests < room.guests.size()) {
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
        PmsBooking booking = getBookingUnsecure(bookingId);
        String message = getConfiguration().defaultMessage.get(booking.language);
        if (message == null) {
            return "";
        }
        return formatMessage(message, booking, null, null);
    }


    @Override
    public void addComment(String bookingId, String comment) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        PmsBookingComment commentToAdd = new PmsBookingComment();
        commentToAdd.userId = userManager.getLoggedOnUser().id;
        commentToAdd.comment = comment;
        booking.comments.put(new Date().getTime(), commentToAdd);
        saveBooking(booking);
    }

    void autoAssignItem(PmsBookingRooms room) {
        List<BookingItem> items = bookingEngine.getAvailbleItems(room.bookingItemTypeId, room.date.start, room.date.end);
        Collections.sort(items, new Comparator<BookingItem>() {
            public int compare(BookingItem o1, BookingItem o2) {
                return o1.order.compareTo(o2.order);
            }
        });
        if (items.isEmpty()) {
            System.out.println("No items available?");
        } else {
            BookingItem item = items.get(0);
            room.bookingItemId = item.id;
            room.bookingItemTypeId = item.bookingItemTypeId;

            if (room.bookingId != null) {
                    try {
                        bookingEngine.changeBookingItemOnBooking(room.bookingId, item.id);
                    }catch(Exception e) {
                        if(warnedAbout.contains("Itemchangedfailed_" + room.pmsBookingRoomId)) {
                            messageManager.sendErrorNotification("Booking failure for room: " + room.pmsBookingRoomId + ", rooms where not reserved in booking engine. address: " + storeManager.getMyStore().webAddress, null);
                            warnedAbout.add("Itemchangedfailed_" + room.pmsBookingRoomId);
                        }
                    }
                
            }
            PmsBooking booking = getBookingFromRoom(room.pmsBookingRoomId);
            String bookingId = "";
            if (booking != null) {
                bookingId = booking.id;
            }
            logEntry("Autoasigned item " + item.bookingItemName, bookingId, null);
        }
    }

    @Override
    public void logEntry(String logText, String bookingId, String itemId) {
        logEntry(logText, bookingId, itemId, null);

    }

    private void logEntry(String logText, String bookingId, String itemId, String roomId) {
        String userId = "";
        if (getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
        }

        PmsLog log = new PmsLog();
        log.bookingId = bookingId;
        log.bookingItemId = itemId;
        log.userId = userId;
        log.roomId = roomId;
        BookingItem item = bookingEngine.getBookingItem(itemId);
        if (item != null) {
            log.bookingItemType = item.bookingItemTypeId;
        }
        log.logText = logText;
        logentries.add(log);
        saveObject(log);
    }

    @Override
    public List<PmsLog> getLogEntries(PmsLog filter) {
        
        List<PmsLog> res = new ArrayList();
        if(filter != null) {
            for (PmsLog log : logentries) {
                if(filter.includeAll) {
                    res.add(log);
                } else if (!filter.bookingId.isEmpty() && filter.bookingId.equals(log.bookingId)) {
                    res.add(log);
                } else if (!filter.bookingItemId.isEmpty() && filter.bookingItemId.equals(log.bookingItemId)) {
                    res.add(log);
                }
            }
        } else {
            res = logentries;
        }
        
        

        Collections.sort(res, new Comparator<PmsLog>() {
            public int compare(PmsLog o1, PmsLog o2) {
                return o2.dateEntry.compareTo(o1.dateEntry);
            }
        });
        
        for(PmsLog log : res) {
            User user = userManager.getUserById(log.userId);
            if(user != null) {
                log.userName = user.fullName;
            }
            if(log.bookingItemId != null && !log.bookingItemId.isEmpty()) {
                BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
                if(item != null) {
                    log.roomName = item.bookingItemName;
                }
            }
        }

        
        if(res.size() > 200) {
            List<PmsLog> newres = new ArrayList();
            int i = 0;
            for(PmsLog test : res) {
                i++;
                newres.add(test);
                if(i > 200) {
                    break;
                }
            }
            res = newres;
        }
        
        return res;
    }

    private String convertToStandardTime(Date start) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        return sdf.format(start);
    }

    @Override
    public List<PmsBookingRooms> updateRepeatingDataForBooking(PmsRepeatingData data, String bookingId) {
        PmsBooking booking = getBooking(bookingId);
        List<PmsBookingRooms> toRemove = new ArrayList();
        for (PmsBookingRooms room : booking.rooms) {

            if (room.addedByRepeater) {
                boolean inStart = (room.date.start.after(data.data.firstEvent.start) || room.date.start.equals(data.data.firstEvent.start));
                boolean inEnd = (room.date.end.before(data.data.endingAt) || room.date.end.equals(data.data.endingAt));
                if (inStart && inEnd) {
                    toRemove.add(room);
                    bookingEngine.deleteBooking(room.bookingId);
                }
            }
        }
        booking.rooms.removeAll(toRemove);

        List<PmsBookingRooms> rooms = getRoomsFromRepeaterData(data);
        List<PmsBookingRooms> roomsToReturn = new ArrayList();
        List<Booking> toAdd = new ArrayList();
        for (PmsBookingRooms room : rooms) {
            if (room.canBeAdded) {
                toAdd.add(room.booking);
                booking.rooms.add(room);
            } else {
                roomsToReturn.add(room);
            }
        }

        bookingEngine.addBookings(toAdd);
        booking.attachBookingItems(toAdd);

        booking.lastRepeatingData = data;
        saveBooking(booking);

        logEntry("Updated repeating dates", bookingId, null);

        return roomsToReturn;
    }

    @Override
    public PmsBooking getBookingFromRoom(String pmsBookingRoomId) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public void returnedKey(String roomId) {
        for (PmsBooking booking : getAllBookings(null)) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.pmsBookingRoomId == null || !room.pmsBookingRoomId.equals(roomId)) {
                    continue;
                }
                if (room.keyIsReturned) {
                    room.keyIsReturned = false;
                    logEntry("Key undelivered for room: " + bookingEngine.getBookingItem(room.bookingItemId).bookingItemName, booking.id, roomId);
                } else {
                    room.keyIsReturned = true;
                    logEntry("Key delivered for room: " + bookingEngine.getBookingItem(room.bookingItemId).bookingItemName, booking.id, roomId);
                }
                saveBooking(booking);
                if (!room.isEndingToday() && room.keyIsReturned) {
                    if(!room.isEnded()) {
                        User usr = userManager.getUserById(booking.userId);
                        String roomName = bookingEngine.getBookingItem(roomId).bookingItemName;
                        String msg = "Key delivered for someone not checking out today, at room: " + roomName + ", booked by: " + usr.fullName;
                        String email = storeManager.getMyStore().configuration.emailAdress;
                        messageManager.sendMail(email, email, msg, msg, email, email);
                        System.out.println(msg);
                    }
                }
                return;
            }
        }

    }

    void warnAboutUnableToAutoExtend(String bookingItemName, String reason) {
        Calendar cal = Calendar.getInstance();
        Integer day = cal.get(Calendar.DAY_OF_YEAR);
        String warningString = bookingItemName + "-" + day;
        if (warnedAbout.contains(warningString)) {
            return;
        }
        String copyadress = storeManager.getMyStore().configuration.emailAdress;
        messageManager.sendMail(copyadress, copyadress, "Unable to autoextend stay for room: " + bookingItemName, "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
        messageManager.sendMail("pal@getshop.com", copyadress, "Unable to autoextend stay for room: " + bookingItemName, "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
        warnedAbout.add(warningString);
    }

    public boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    boolean needCheckOutCleaning(PmsBookingRooms room, Date toDate) {
        if (room.date.exitCleaningDate == null) {
            room.date.exitCleaningDate = room.date.end;

            if (configuration.cleaningNextDay) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.date.exitCleaningDate);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                room.date.exitCleaningDate = cal.getTime();
            }

        }

        return isSameDay(room.date.exitCleaningDate, toDate);
    }

    private List<PmsBookingRooms> excludeAlreadyAdded(List<PmsBookingRooms> allRooms, PmsBooking curBooking) {
        List<PmsBookingRooms> toremove = new ArrayList();
        for (PmsBookingRooms room : allRooms) {
            for (PmsBookingRooms alreadyAdded : curBooking.rooms) {
                if (room.isSame(alreadyAdded)) {
                    toremove.add(room);
                }
            }
        }
        allRooms.removeAll(toremove);
        return allRooms;
    }

    private void sortRooms(List<PmsBookingRooms> result) {
        Collections.sort(result, new Comparator<PmsBookingRooms>() {
            public int compare(PmsBookingRooms o1, PmsBookingRooms o2) {
                if (o1.item == null || o2.item == null) {
                    System.out.println("This is null");
                }
                return o1.item.bookingItemName.compareTo(o2.item.bookingItemName);
            }
        });
    }

    void makeSureCleaningsAreOkay() {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = PmsBookingFilter.PmsBookingFilterTypes.active;
        filter.startDate = new Date();
        filter.needToBeConfirmed = true;
        filter.endDate = new Date();
        List<PmsBooking> allBookings = getAllBookings(filter);
        for (PmsBooking booking : allBookings) {
            boolean needSaving = false;
            for (PmsBookingRooms room : booking.rooms) {
                if (!room.isStarted()) {
                    continue;
                }
                if (room.isEnded()) {
                    continue;
                }
                if (room.isStartingToday()) {
                    continue;
                }

                String ownerMail = storeManager.getMyStore().configuration.emailAdress;
                String addressMail = storeManager.getMyStore().webAddress;
                if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    System.out.println("Booking started without item, Owner: " + ownerMail + ", address:" + addressMail);
                    continue;
                }

                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                if (item == null) {
                    messageManager.sendMail("pal@getshop.com", "pal@getshop.com", "Booking started without item (nullitem)", "Owner: " + ownerMail + ", address:" + addressMail, "pal@getshop.com", "pal@getshop.com");
                } else {
                    PmsAdditionalItemInformation additional = getAdditionalInfo(room.bookingItemId);
                    if (additional.isClean(false)) {
                        additional.markDirty();
                        needSaving = true;
                        logEntry("Marking item " + item.bookingItemName + " as dirty (failure in marking)", booking.id, item.id);
                        saveObject(additional);
                    }
                }
            }
            if (needSaving) {
                saveBooking(booking);
            }
        }
    }

    @Override
    public void setNewCleaningIntervalOnRoom(String roomId, Integer interval) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.pmsBookingRoomId.equals(roomId)) {
                    room.intervalCleaning = interval;
                    saveBooking(booking);
                }
            }
        }
    }

    private void checkIfRoomShouldBeUnmarkedDirty(PmsBookingRooms room, String bookingId) {
        if (!configuration.unsetCleaningIfJustSetWhenChangingRooms) {
            return;
        }

        if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
            PmsAdditionalItemInformation additional = getAdditionalInfo(room.bookingItemId);
            if (additional != null) {
                if (additional.unsetMarkedDirtyPastThirtyMinutes()) {
                    logEntry("Unsetting cleaning for this room.", bookingId, room.bookingItemId);
                    saveObject(additional);
                }
            }
        }

    }

    private void resetBookingItem(PmsBookingRooms room, String itemId, PmsBooking booking) {
        if (room.isStarted() && !room.isEnded()) {
            room.addedToArx = false;
            PmsAdditionalItemInformation add = getAdditionalInfo(itemId);
            add.markDirty();
            saveObject(add);
            doNotification("room_changed", booking, room);
        }
    }

    @Override
    public PmsBookingRooms getRoomForItem(String itemId, Date atTime) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.rooms) {
                if (room.isEnded(atTime)) {
                    continue;
                }
                if (!room.isStarted(atTime)) {
                    continue;
                }
                if (room.bookingItemId.equals(itemId)) {
                    return room;
                }
            }
        }
        return null;
    }

    private Double calculateMonthlyPricing(String typeId) {
        HashMap<String, Double> priceRange = prices.dailyPrices.get(typeId);
        if (priceRange == null) {
            return 0.0;
        }
        Double defaultPrice = priceRange.get("default");
        if (defaultPrice == null) {
            defaultPrice = 0.0;
        }

        return defaultPrice;
    }

    private boolean bookingIsOK(PmsBooking booking) {
        finalize(booking);
        for (PmsBookingRooms room : booking.rooms) {
            if (room.booking == null) {
                messageManager.sendErrorNotification("Booking failure for booking: " + booking.id + ", rooms where not reserved in booking engine. address: " + storeManager.getMyStore().webAddress, null);
                return false;
            }
        }

        if (booking.rooms == null || booking.rooms.isEmpty()) {
            return false;
        }
        return true;
    }

    private void removeBeingProcessed(List<PmsBooking> result) {
        List<PmsBooking> toRemove = new ArrayList();
        for (PmsBooking booking : result) {
            if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
                toRemove.add(booking);
            }
        }
        result.removeAll(toRemove);
    }

    @Override
    public Integer getNumberOfAvailable(String itemType, Date start, Date end) {
        if (!isOpen(itemType, start, end)) {
            return 0;
        }
        return bookingEngine.getNumberOfAvailable(itemType, start, end);
    }

    private boolean isOpen(String itemType, Date start, Date end) {
        
        Session sess = getSession();
        if(sess != null && sess.currentUser != null) {
            if(sess.currentUser.isAdministrator()) {
                return true;
            }
        }
        
        List<TimeRepeaterData> openingshours = bookingEngine.getOpeningHours(itemType);
        if(openingshours.isEmpty()) {
            openingshours = bookingEngine.getOpeningHours(null);
        }
        
        if(openingshours.isEmpty()) {
            return true;
        }
        
        TimeRepeater repeater = new TimeRepeater();
        for(TimeRepeaterData res : openingshours) {
            LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
            for(TimeRepeaterDateRange range : ranges) {
                if(range.containsRange(start, end)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean canAdd(List<Booking> toCheck) {
        for(Booking book : toCheck) {
            if(!isOpen(book.bookingItemTypeId, book.startDate, book.endDate)) {
                return false;
            }
        }
        
        return bookingEngine.canAdd(toCheck);
    }

    private String getFromEmail() {
        String fromEmail = storeManager.getMyStore().configuration.emailAdress;
        if(!configuration.senderEmail.isEmpty()) {
            fromEmail = configuration.senderEmail;
        }
        return fromEmail;
    }

    private String getFromName() {
        String fromName = getFromEmail();
        if(!configuration.senderName.isEmpty()) {
            fromName = configuration.senderName;
        }
        return fromName;
    }

    private Double getPriceInPeriode(PmsBooking booking, PmsBookingRooms room, Date startDate, Date endDate) {
        Double price = null;
        if (booking.priceType.equals(PmsBooking.PriceType.monthly)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            int days = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(startDate)).getDays();
            price = calculateProgressivePrice(room.bookingItemTypeId, startDate, endDate, days, true, booking.priceType);
        } else if (booking.priceType.equals(PmsBooking.PriceType.daily)
                || booking.priceType.equals(PmsBooking.PriceType.interval)
                || booking.priceType.equals(PmsBooking.PriceType.progressive)) {
            price = room.price;
        } else if (booking.priceType.equals(PmsBooking.PriceType.weekly)) {
            price = (room.price / 7);
        }
        
        price = cartManager.calculatePriceForCoupon(booking.couponCode, price);
        return price;
    }

    private Order createOrderFromCart(PmsBooking booking) {

        User user = userManager.getUserById(booking.userId);
        if (user == null) {
            return null;
        }

        user.address.fullName = user.fullName;

        Order order = orderManager.createOrder(user.address);

        order.payment = new Payment();
        order.payment.paymentType = user.preferredPaymentType;
        order.userId = booking.userId;
        order.invoiceNote = booking.invoiceNote;

        if (configuration.substractOneDayOnOrder) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.rowCreatedDate);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            order.rowCreatedDate = cal.getTime();
        }

        if (order.cart.address == null || order.cart.address.address == null || order.cart.address.address.isEmpty()) {
            if (!user.company.isEmpty()) {
                Company company = userManager.getCompany(user.company.get(0));
                order.cart.address = company.address;
                order.cart.address.fullName = company.name;
            }
        }

        orderManager.saveOrder(order);
        return order;
    }

    private int getNumberOfMonthsBetweenDates(Date startDate, Date endDate) {
        int months = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        while(true) {
            cal.add(Calendar.MONTH, 1);
            if(cal.getTime().after(endDate)) {
                break;
            }
            if(cal.getTime().equals(endDate)) {
                break;
            }
            months++;
        }
        return months;
    }

    @Override
    public void undeleteBooking(String bookingId) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        List<Booking> toAdd = buildRoomsToAddToEngineList(booking);
        bookingEngine.addBookings(toAdd);
        booking.attachBookingItems(toAdd);
        booking.isDeleted = false;
        saveBooking(booking);
        logEntry("booking has been undeleted", bookingId, null);
    }

    private List<Booking> buildRoomsToAddToEngineList(PmsBooking booking) {
        List<Booking> bookingsToAdd = new ArrayList();
        List<PmsBookingRooms> removeRooms = new ArrayList();
        for (PmsBookingRooms room : booking.rooms) {
            Booking bookingToAdd = createBooking(room);
            if (!bookingEngine.canAdd(bookingToAdd)) {
                room.canBeAdded = false;
                removeRooms.add(room);
                BookingItemType item = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                String name = "";
                if (item != null) {
                    name = item.name;
                }
                String text = "Removed room: " + name + " since it can't be added: " + room.date.start + " - " + room.date.end;
                logEntry(text, booking.id, null);
                System.out.println(text);
                continue;
            }
            
            bookingsToAdd.add(bookingToAdd);
        }
        booking.rooms.removeAll(removeRooms);
        return bookingsToAdd;
    }

    @Override
    public void handleDoorControl(String doorId, List<AccessLog> accessLogs) throws Exception {
        PmsManagerDoorSurveilance sur = new PmsManagerDoorSurveilance(this);
        sur.handleDoorControl(doorId, accessLogs);
    }

    @Override
    public void checkDoorStatusControl() throws Exception {
        PmsManagerDoorSurveilance sur = new PmsManagerDoorSurveilance(this);
        sur.checkStatus();
    }

    @Override
    public List<PmsBooking> getAllBookingsForLoggedOnUser() {
        if(getSession().currentUser == null) {
            return new ArrayList();
        }
        String userId = getSession().currentUser.id;
        PmsBookingFilter filter = new PmsBookingFilter();
        List<PmsBooking> allBookings = getAllBookings(filter);
        List<PmsBooking> result = new ArrayList();
        for(PmsBooking booking : allBookings) {
            if(booking.userId != null && booking.userId.equals(userId)) {
                result.add(booking);
            }
        }
        return result;
    }

    private void checkSecurity(PmsBooking booking) {
        User loggedonuser = getSession().currentUser;
        
        if(loggedonuser != null && (booking.userId == null || booking.userId.isEmpty())) {
            return;
        }

        if(loggedonuser == null) {
            throw new ErrorException(26);
        }
        
        if(loggedonuser.isEditor() || loggedonuser.isAdministrator()) {
            return;
        }
        
        if(booking.userId.equals(loggedonuser.id)) {
            return;
        }
        
        throw new ErrorException(26);
    }

    @Override
    public List<Integer> updateRoomByUser(String bookingId, PmsBookingRooms room) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms oldRoom = booking.getRoom(room.pmsBookingRoomId);
        
        List<Integer> errors = new ArrayList();
        if(!oldRoom.date.start.equals(room.date.start)) {
            System.out.println("Need to set a new start date");
            if(room.isStarted()) {
                errors.add(1);
                room.date.start = oldRoom.date.start;
            }
        }
        if(!room.date.end.equals(room.date.end)) {
            if(room.isEnded()) {
                errors.add(2);
                room.date.end = oldRoom.date.end;
            }
        }
        
        if(!oldRoom.date.start.equals(room.date.start) || !oldRoom.date.end.equals(room.date.end)) {
            PmsBookingRooms res = changeDates(room.pmsBookingRoomId, bookingId, room.date.start, room.date.end);
            if(res == null) {
                errors.add(3);
            }
        }
        
        oldRoom.numberOfGuests = room.numberOfGuests;
        oldRoom.guests = room.guests;
        
        return errors;
    }

    private String getCouponCode(String couponCode) {
        Session loggedonuser = getSession();
        if(loggedonuser != null && loggedonuser.currentUser != null && loggedonuser.currentUser.couponId != null) {
            if(!loggedonuser.currentUser.couponId.isEmpty()) {
                Coupon coupon = cartManager.getCouponById(loggedonuser.currentUser.couponId);
                if(coupon != null) {
                    return coupon.code;
                }
            }
        }
        return couponCode;
    }

    @Override
    public List<PmsRoomSimple> getSimpleRooms(PmsBookingFilter filter) {
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this);
        return filtering.filterRooms(filter);
    }

    @Override
    public void setGuestOnRoom(List<PmsGuests> guests, String bookingId, String roomId) {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms room = booking.getRoom(roomId);
        room.guests = guests;
        room.numberOfGuests = guests.size();
        logEntry("Changed guest information", bookingId, roomId);
        saveBooking(booking);
    }

    @Override
    public void sendMessageToAllTodaysGuests(String message) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = PmsBookingFilter.PmsBookingFilterTypes.active;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(Calendar.HOUR, 1);
        end.set(Calendar.HOUR, 23);
        filter.startDate = start.getTime();
        filter.endDate = end.getTime();
        
        List<PmsRoomSimple> allRooms = getSimpleRooms(filter);
        String from = "GetShop";
        if(configuration.smsName != null && configuration.smsName.isEmpty()) {
            from = configuration.smsName;
        }
        for(PmsRoomSimple simple : allRooms) {
            for(PmsGuests guest : simple.guest) {
                messageManager.sendSms("sveve", guest.phone, message, guest.prefix);
            }
        }
    }

    public PmsConfiguration getConfigurationSecure() {
        return configuration;
    }

    @Override
    public void markKeyDeliveredForAllEndedRooms() {
        for(PmsBooking booking : bookings.values()) {
            boolean needsaving = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(room.isEnded() && !room.keyIsReturned) {
                    room.keyIsReturned = true;
                    needsaving = true;
                }
            }
            if(needsaving) {
                saveBooking(booking);
            }
        }
    }

    @Override
    public void changeInvoiceDate(String roomId, Date newDate) {
        PmsBooking booking = getBookingFromRoom(roomId);
        for(PmsBookingRooms room : booking.rooms) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                room.invoicedTo = newDate;
            }
        }
        saveBooking(booking);
    }

    private List<PmsBooking> filterByUser(List<PmsBooking> finalized, String userId) {
        if(userId == null || userId.isEmpty()) {
            return finalized;
        }
        
        List<PmsBooking> res = new ArrayList();
        for(PmsBooking booking : finalized) {
            if(booking.userId != null && booking.userId.equals(userId)) {
                res.add(booking);
            }
        }
        return res;
    }

}
