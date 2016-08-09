package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.RegistrationRules;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.Summary;
import biweekly.util.Duration;
import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ibm.icu.util.Calendar; 
import com.thundashop.core.amesto.AmestoManager;
import com.thundashop.core.amesto.AmestoSync;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
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
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GrafanaFeeder;
import com.thundashop.core.common.GrafanaManager;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;
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

    private HashMap<String, PmsBooking> bookings = new HashMap();
    private HashMap<String, Product> fetchedProducts = new HashMap();
    private HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo = new HashMap();
    private PmsPricing prices = new PmsPricing();
    private PmsConfiguration configuration = new PmsConfiguration();
    private List<String> repicientList = new ArrayList();
    private List<String> warnedAbout = new ArrayList();
    private List<PmsAdditionalTypeInformation> additionDataForTypes = new ArrayList();

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
    InvoiceManager invoiceManager;

    @Autowired
    ProductManager productManager;

    @Autowired
    FrameworkConfig frameworkConfig;

    @Autowired
    GetShopLockManager getShopLockManager;
    
    @Autowired
    PmsInvoiceManager pmsInvoiceManager;
    
    @Autowired
    GrafanaManager grafanaManager;
    
    private String specifiedMessage = "";
    Date lastOrderProcessed;
    private List<PmsLog> logentries = new ArrayList();
    private boolean initFinalized = false;
    private String orderIdToSend;
    private Date lastCheckForIncosistent;
    private String emailToSendTo;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking) dataCommon;
//                dumpBooking(booking);
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
         
        createScheduler("pmsprocessor", "* * * * *", CheckPmsProcessing.class);
        createScheduler("pmsprocessor2", "5 * * * *", CheckPmsProcessingHourly.class);
        
        if(applicationPool.getAvailableApplications().contains(applicationPool.getApplication("66b4483d-3384-42bb-9058-2ac915c77d80"))) {
            createScheduler("amestosync", "00 08 * * *", AmestoSync.class);
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

        Collections.sort(allGroups, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        for (BookingItemType type : allGroups) {
            if (!type.visibleForBooking) {
                continue;
            }
            
            Room roomToAdd = new Room();
            roomToAdd.type = type;
            
            PmsBookingRooms room = new PmsBookingRooms();
            room.bookingItemTypeId = type.id;
            room.date = new PmsBookingDateRange();
            room.date.start = start;
            room.date.end = end;
            
            String couponcode = getCouponCode("");
            setPriceOnRoom(room, couponcode, true, PmsBooking.PriceType.daily);
            
            roomToAdd.price = room.price;
            result.add(roomToAdd);
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.bookingItemTypeId == null || room.bookingItemTypeId.isEmpty()) {
                if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    room.bookingItemTypeId = bookingEngine.getBookingItem(room.bookingItemId).bookingItemTypeId;
                }
            }
        }
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            int totalDays = 1;
            if (room.date.end != null && room.date.start != null) {
                totalDays = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(room.date.end)).getDays();
            }
            
            room.count = totalDays;
            String couponCode = getCouponCode(booking.couponCode);
            setPriceOnRoom(room, couponCode, true, booking.priceType);
            room.updateBreakfastCount();
            
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
            if(!configuration.hasNoEndDate) {
                booking.sessionEndDate = range.end;
            }
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
        
        if(booking.getActiveRooms().isEmpty()) {
            return null;
        }
        notifyAdmin("booking_completed_" + booking.language, booking);
        if (!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }
        
        checkForMissingEndDate(booking);
        
        Integer result = 0;
        booking.isDeleted = false;
        
        createUserForBooking(booking);
        if (configuration.payAfterBookingCompleted) {
            createPrepaymentOrder(booking.id);
        }
        
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

                try {
                    processor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return booking;
            }
        } catch (Exception e) {
            messageManager.sendErrorNotification("Unknown booking exception occured for booking id: " + booking.id, e);
            e.printStackTrace();
        }
        return null; 
    }

    @Override
    public String createPrepaymentOrder(String bookingId) {
        return pmsInvoiceManager.createPrePaymentOrder(bookingId);
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

        if (!configuration.needConfirmation) {
            booking.confirmed = true;
        }

        User loggedonuser = userManager.getLoggedOnUser();
        if (loggedonuser != null && configuration.autoconfirmRegisteredUsers) {
            booking.confirmed = true;
        }
       if(loggedonuser != null && (loggedonuser.isAdministrator() || loggedonuser.isEditor())) {
            booking.confirmed = true;
        }
        
        booking.sessionId = "";

        saveBooking(booking);
        feedGrafana(booking);
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

                for (PmsBookingRooms room : booking.getActiveRooms()) {
                    if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                        BookingItem item = bookingEngine.getBookingItemUnfinalized(room.bookingItemId);
                        if (item != null && item.bookingItemName != null && item.bookingItemName.contains(filter.searchWord)) {
                            if(!result.contains(booking)) {
                                result.add(booking);
                            }
                        }
                    }
                }
            }
        } else if (filter.filterType == null || filter.filterType.isEmpty() || filter.filterType.equals("registered")) {
            for (PmsBooking booking : bookings.values()) {
                if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("active") || filter.filterType.equals("inhouse")) {
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
        } else {
            for (PmsBooking booking : bookings.values()) {
                if (!booking.isDeleted) {
                    result.add(booking);
                }
            }
        }

        removeInactive(filter, result);

        List<PmsBooking> finalized = finalizeList(result);

        finalized = sortList(finalized, filter.sorting);

        finalized = filterTypes(finalized, filter.typeFilter);
        finalized = filterByUser(finalized,filter.userId);
        finalized = filterByChannel(finalized,filter.channel);
        finalized = filterByBComRateManager(finalized,filter);

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
        
        long diff = 10900 * 1000;
        if(lastCheckForIncosistent != null) {
            diff = System.currentTimeMillis() - lastCheckForIncosistent.getTime();
        }
        if(diff > (10800 * 1000)) {
            checkForIncosistentBookings();
            lastCheckForIncosistent = new Date();
        }
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
            if(booking.getActiveRooms() == null) {
                continue;
            }
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.code != null && (room.code.equals(newcode) && !room.isEnded())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public PmsBooking finalize(PmsBooking booking) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.add(Calendar.HOUR_OF_DAY, -1);
        if (booking.sessionId != null && !booking.sessionId.isEmpty()) {
            if (!booking.rowCreatedDate.after(nowCal.getTime())) {
                hardDeleteBooking(booking);
                return null;
            }
        }
        
        if (!booking.payedFor && configuration.autoDeleteUnpaidBookings && 
                !booking.avoidAutoDelete &&
                booking.rowCreatedDate.before(nowCal.getTime())) {
                hardDeleteBooking(booking);
                return null;
        }
        
        
        
        if (booking.isDeleted) {
            booking.state = 2;
            return booking;
        }

        for (PmsBookingRooms room : booking.getActiveRooms()) {
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
                    room.item = bookingEngine.getBookingItemUnfinalized(room.bookingItemId);
                    room.bookingItemTypeId = room.item.bookingItemTypeId;
                }

                room.type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if (room.type != null) {
                    String productId = room.type.productId;
                    if (productId != null) {
                        Product product = getProduct(productId);
                        if (product != null && product.taxGroupObject != null) {
                            room.taxes = product.taxGroupObject.taxRate;
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
            if (booking.getActiveRooms() != null) {
                for (PmsBookingRooms room : booking.getActiveRooms()) {
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
            if(room.bookingId != null && !room.bookingId.isEmpty()) {
                bookingEngine.changeDatesOnBooking(room.bookingId, start, end);
            }
            Date oldStart = room.date.start;
            Date oldEnd = room.date.end;

            room.date.start = start;
            room.date.end = end;
            room.date.exitCleaningDate = null;
            room.date.cleaningDate = null;
            if(configuration.updatePriceWhenChangingDates) {
                setPriceOnRoom(room, "", true, booking.priceType);
            }
            saveBooking(booking);
            
            String logText = "New date set from " + convertToStandardTime(oldStart) + " - " + convertToStandardTime(oldEnd) + " to, " + convertToStandardTime(start) + " - " + convertToStandardTime(end);
            logEntry(logText, bookingId, null, roomId);
            doNotification("date_changed", booking, room);
            return room;
        } catch (BookingEngineException ex) {
//            ex.printStackTrace();
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
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
    public String setBookingItem(String roomId, String bookingId, String itemId, boolean split) {
        PmsBooking booking = getBooking(bookingId);
        if (booking == null) {
            return "Booking does not exists";
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            if(room.bookingItemId != null && room.bookingItemId.equals(itemId)) {
                //Why change into the same room?
                return "";
            }
             
            if (room == null) {
                return "Room does not exists";
            }
            if(split) {
                room = splitBookingIfNesesary(booking, room);
            }
            checkIfRoomShouldBeUnmarkedDirty(room, booking.id);
            if(room.bookingId != null && !room.bookingId.isEmpty()) {
                bookingEngine.changeBookingItemOnBooking(room.bookingId, itemId);
                resetBookingItem(room, itemId, booking);
            } else {
                BookingItem item = bookingEngine.getBookingItem(itemId);
                if(item != null) {
                    room.bookingItemId = item.id;
                    room.bookingItemTypeId = item.bookingItemTypeId;
                } else {
                    room.bookingItemId = null;
                }
            }
            finalize(booking);

            String from = "none";
            if (room.bookingItemId != null) {
                BookingItem oldItem = bookingEngine.getBookingItem(room.bookingItemId);
                if (oldItem != null) {
                    from = oldItem.bookingItemName;
                }
            }

            String logText = "";
            if (bookingEngine.getBookingItem(itemId) != null) {
                logText = "Changed room to " + bookingEngine.getBookingItem(itemId).bookingItemName + " from " + from;
            } else {
                logText = "Unassigned room from " + from;
            }
            
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

    @Override
    public String createOrder(String bookingId, NewOrderFilter filter) {
        if(configuration.autoCreateInvoices && !filter.autoGeneration) {
            filter.maxAutoCreateDate = filter.endInvoiceAt;
            filter.autoGeneration = true;
            filter.increaseUnits = configuration.increaseUnits;
            filter.prepayment = configuration.prepayment;
        }
        
        return pmsInvoiceManager.createOrder(bookingId, filter);
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
        
        toReturn.wubookusername = "";
        toReturn.wubookpassword = "";
        toReturn.wubookproviderkey = "";

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
        if(!key.contains("booking_completed")) {
            notifyAdmin(key, booking);
        }
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
                message = message.trim();
                message = message.replace("\n", "<br>\n");
            }
        }
        if (message == null || message.isEmpty()) {
            return "";
        }
        
        if(key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing") || key.startsWith("order_")) {
            message = message.replace("{orderid}", this.orderIdToSend);
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
            HashMap<String, String> attachments = new HashMap();
            
            if (key.startsWith("booking_confirmed")) {
                attachments.putAll(createICalEntry(booking));
            }
            if (key.startsWith("sendreciept")) {
                attachments.put("reciept.pdf", createInvoiceAttachment());
            }
            if (key.startsWith("sendinvoice")) {
                attachments.put("invoice.pdf", createInvoiceAttachment());
            }
            
            String recipientEmail = user.emailAddress;
            if(emailToSendTo != null) {
                recipientEmail = emailToSendTo;
                emailToSendTo = null;
            }
            
            messageManager.sendMailWithAttachments(recipientEmail, user.fullName, title, message, fromEmail, fromName, attachments);

            if (configuration.copyEmailsToOwnerOfStore) {
                String copyadress = storeManager.getMyStore().configuration.emailAdress;
                messageManager.sendMail(copyadress, user.fullName, title, message, fromEmail, fromName);
            }

            repicientList.add(user.emailAddress);
        }
    }

    private String notifyGuest(PmsBooking booking, String message, String type, String key, PmsBookingRooms roomToNotify) {
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (roomToNotify != null) {
                if (!room.pmsBookingRoomId.equals(roomToNotify.pmsBookingRoomId)) {
                    continue;
                }
            }
            
            if(room.guests == null || room.guests.isEmpty() || !bookingEngine.getConfig().rules.includeGuestData) {
                if (type.equals("email")) {
                    String email = userManager.getUserById(booking.userId).emailAddress;
                    if(emailToSendTo != null) {
                        email = emailToSendTo;
                        emailToSendTo = null;
                    }
                    
                    String name = userManager.getUserById(booking.userId).fullName;
                    String title = configuration.emailTitles.get(key);
                    title = formatMessage(title, booking, room, null);
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
                        if(title == null) {
                            title = "";
                        }
                        title = formatMessage(title, booking, room, guest);
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
        if(message != null) {
            message = message.trim();
        }
        PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter();

        if (this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }

        if (room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
        }
        message = formater.formatContactData(message, userManager.getUserById(booking.userId), null, booking);
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
        if(!filter.typeFilter.isEmpty()) {
            totalRooms = 0;
            for(String id : filter.typeFilter) {
                totalRooms += bookingEngine.getBookingItemsByType(id).size();
            }
        }
        
        PmsStatistics result = builder.buildStatistics(filter, totalRooms);
        result.salesEntries = builder.buildOrderStatistics(filter, orderManager);
        result.setView(filter);
        result.buildTotal();
        result.buildTotalSales();
        return result;
    }

    @Override
    public void addAddonToCurrentBooking(String itemtypeId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        Date start = null;
        Date end = null;
        for (PmsBookingRooms room : booking.getActiveRooms()) {
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
        booking.addRoom(room);
        setBooking(booking);
    }

    @Override
    public String removeFromBooking(String bookingId, String roomId) throws Exception {
        PmsBooking booking = getBookingUnsecure(bookingId);
        checkSecurity(booking);
        List<PmsBookingRooms> toRemove = new ArrayList();
        String roomName = "";
        String addResult = "";
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
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
            if(!remove.isDeleted()) {
                bookingEngine.deleteBooking(remove.bookingId);
                remove.delete();
                logEntry(roomName + " removed from booking ", bookingId, null);
            } else {
                try {
                    Booking tmpbook = createBooking(remove);
                    List<Booking> toAdd = new ArrayList();
                    toAdd.add(tmpbook);
                    bookingEngine.addBookings(toAdd);
                    remove.undelete();
                    booking.isDeleted = false;
                    remove.setBooking(tmpbook);
                    logEntry(roomName + " readded to booking ", bookingId, null);
                }catch(BookingEngineException ex) {
                    addResult = ex.getMessage();
                }
            }
        }
        saveObject(booking);

        if(booking.getActiveRooms().isEmpty()) {
            deleteBooking(booking.id);
        }
        return addResult;
    }

    @Override
    public void removeFromCurrentBooking(String roomId) throws Exception {
        PmsBooking booking = getCurrentBooking();
        ArrayList toRemove = new ArrayList();
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.pmsBookingRoomId.equals(roomId)) {
                toRemove.add(room);
            }
        }
        booking.removeRooms(toRemove);
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
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
            LinkedHashMap<Long, IntervalResultEntry> itemCountLine = new LinkedHashMap();
            for (BookingTimeLine tl : timelines) {
                IntervalResultEntry tmpres = new IntervalResultEntry();
                tmpres.bookingIds = tl.bookingIds;
                tmpres.count = tl.count;
                tmpres.time = tl.start.getTime();
                
                if(!tmpres.bookingIds.isEmpty()) {
                    PmsBooking booking = getBookingFromBookingEngineId(tmpres.bookingIds.get(0));
                    User user = userManager.getUserById(booking.userId);
                    tmpres.name = user.fullName;
                }
                
                itemCountLine.put(tl.start.getTime(), tmpres);
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

            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
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
                    return o1.getActiveRooms().get(0).date.start.compareTo(o2.getActiveRooms().get(0).date.start);
                }
            });
        } else if (sorting.equals("room") || sorting.equals("room_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    if (o1.getActiveRooms() == null || o1.getActiveRooms().isEmpty() || o1.getActiveRooms().get(0).item == null) {
                        return -1;
                    }
                    if (o2.getActiveRooms() == null || o2.getActiveRooms().isEmpty() || o2.getActiveRooms().get(0).item == null) {
                        return -1;
                    }
                    return o1.getActiveRooms().get(0).item.bookingItemName.compareTo(o2.getActiveRooms().get(0).item.bookingItemName);
                }
            });
        } else if (sorting.equals("price") || sorting.equals("price_desc")) {
            Collections.sort(result, new Comparator<PmsBooking>() {
                public int compare(PmsBooking o1, PmsBooking o2) {
                    return o1.getActiveRooms().get(0).price.compareTo(o2.getActiveRooms().get(0).price);
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
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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

        user.birthDay = result.get("user_birthday");
        
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
        curBooking.addRoom(room);

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

        curBooking.addRooms(allRooms);
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
        for (PmsBookingRooms room : curBooking.getActiveRooms()) {
            if (room.addedByRepeater) {
                toRemove.add(room);
            }
        }
        curBooking.removeRooms(toRemove);
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.isAddon) {
                toRemove.add(room);
            }
        }

        booking.getActiveRooms().removeAll(toRemove);

        List<PmsBookingRooms> allToAdd = new ArrayList();
        for (String addonId : booking.bookingEngineAddons) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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

        booking.addRooms(allToAdd);

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

        if(booking.sessionId == null || booking.sessionId.isEmpty()) {
            Booking bookingToAdd = createBooking(room);
            List<Booking> bookingToAddList = new ArrayList();
            bookingToAddList.add(bookingToAdd);
            if (!canAdd(bookingToAddList)) {
                return "The room can not be added, its not available.";
            }

            bookingEngine.addBookings(bookingToAddList);
            booking.addRoom(room);
            booking.attachBookingItems(bookingToAddList);
        } else {
            booking.addRoom(room);
        }
        
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
        setPriceOnRoom(room,  "", true, booking.priceType);

        String res = addBookingToBookingEngine(booking, room);
        if(!res.isEmpty()) {
            return res;
        }
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
        List<BookingItem> items = bookingEngine.getAvailbleItemsWithBookingConsidered(room.bookingItemTypeId, room.date.start, room.date.end, room.bookingId);
        Collections.sort(items, new Comparator<BookingItem>() {
            public int compare(BookingItem o1, BookingItem o2) {
                return o1.order.compareTo(o2.order);
            }
        });
        if (items.isEmpty()) {
            System.out.println("No items available?");
        } else {
            BookingItem item = null;
            for(BookingItem tmpitem : items) {
                item = tmpitem;
                PmsAdditionalItemInformation additionalroominfo = getAdditionalInfo(item.id);
                if(additionalroominfo.isClean()) {
                    break;
                }
            }
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {

            if (room.addedByRepeater) {
                boolean inStart = (room.date.start.after(data.data.firstEvent.start) || room.date.start.equals(data.data.firstEvent.start));
                boolean inEnd = (room.date.end.before(data.data.endingAt) || room.date.end.equals(data.data.endingAt));
                if (inStart && inEnd) {
                    toRemove.add(room);
                    bookingEngine.deleteBooking(room.bookingId);
                }
            }
        }
        booking.removeRooms(toRemove);

        List<PmsBookingRooms> rooms = getRoomsFromRepeaterData(data);
        List<PmsBookingRooms> roomsToReturn = new ArrayList();
        List<Booking> toAdd = new ArrayList();
        for (PmsBookingRooms room : rooms) {
            if (room.canBeAdded) {
                toAdd.add(room.booking);
                booking.addRoom(room);
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
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                    checkSecurity(booking);
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public void returnedKey(String roomId) {
        List<PmsBooking> result = getAllBookings(null);
        PmsBookingFilter filter = new PmsBookingFilter(); 
        removeInactive(filter, result);
        
        for (PmsBooking booking : result) {
            
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
                if (!room.isEndingToday() && room.keyIsReturned) {
                    if(!room.isEnded()) {
                        Calendar now = Calendar.getInstance();
                        
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(room.date.end);
                        cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
                        cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
                        room.date.end = cal.getTime();
                        updateBooking(room);
                        
                        User usr = userManager.getUserById(booking.userId);
                        BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                        String roomName = "";
                        if(item != null) {
                            roomName = item.bookingItemName;
                        }
                        String msg = "Key delivered for someone not checking out today, at room: " + roomName + ", booked by: " + usr.fullName;
                        String email = storeManager.getMyStore().configuration.emailAdress;
                        messageManager.sendMail(email, email, "Key delivery failed.", msg, email, email);
                    }
                }
                saveBooking(booking);
                return;
            }
        }

    }

    void warnAboutUnableToAutoExtend(String bookingItemName, String reason) {
        Calendar cal = Calendar.getInstance();
        Integer day = cal.get(Calendar.DAY_OF_YEAR);
        String warningString = bookingItemName + "-" + day;
        String copyadress = storeManager.getMyStore().configuration.emailAdress;
        messageManager.sendMail(copyadress, copyadress, "Unable to autoextend stay for room: " + bookingItemName, "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
        messageManager.sendMail("pal@getshop.com", copyadress, "Unable to autoextend stay for room: " + bookingItemName, "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
        warnedAbout.add(warningString);
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

        return pmsInvoiceManager.isSameDay(room.date.exitCleaningDate, toDate);
    }

    private List<PmsBookingRooms> excludeAlreadyAdded(List<PmsBookingRooms> allRooms, PmsBooking curBooking) {
        List<PmsBookingRooms> toremove = new ArrayList();
        for (PmsBookingRooms room : allRooms) {
            for (PmsBookingRooms alreadyAdded : curBooking.getActiveRooms()) {
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
                    return 0;
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
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
            for (PmsBookingRooms room : booking.getActiveRooms()) {
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
        List<PmsBooking> allbookings = new ArrayList(bookings.values());
        PmsBookingFilter filter = new PmsBookingFilter();
        removeInactive(filter, allbookings);
        for (PmsBooking booking : allbookings) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.isEnded(atTime)) {
                    continue;
                }
                if (!room.isStarted(atTime)) {
                    continue;
                }
                if (room.bookingItemId != null && room.bookingItemId.equals(itemId)) {
                    return room;
                }
            }
        }
        return null;
    }

    private boolean bookingIsOK(PmsBooking booking) {
        finalize(booking);
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.booking == null) {
                messageManager.sendErrorNotification("Booking failure for booking: " + booking.id + ", rooms where not reserved in booking engine. address: " + storeManager.getMyStore().webAddress, null);
                return false;
            }
        }

        if (booking.getActiveRooms() == null || booking.getActiveRooms().isEmpty()) {
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
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            Booking bookingToAdd = createBooking(room);
            if (!bookingEngine.canAdd(bookingToAdd) || doAllDeleteWhenAdded()) {
                room.canBeAdded = false;
                room.delete();
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
        return bookingsToAdd;
    }

    @Override
    public void checkDoorStatusControl() throws Exception {
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
        if(booking.sessionId != null && getSession().id.equals(booking.sessionId)) {
            return;
        }
        if(loggedonuser != null && (booking.userId == null || booking.userId.isEmpty())) {
            return;
        }

        if(booking.sessionId != null && 
                !booking.sessionId.isEmpty() &&
                getSession() != null && 
                getSession().id != null &&
                booking.sessionId.equals(getSession().id)) {
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
        List<PmsRoomSimple> res = filtering.filterRooms(filter);
        return res;
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
            for(PmsBookingRooms room : booking.getActiveRooms()) {
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
        for(PmsBookingRooms room : booking.getActiveRooms()) {
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
        
        User loggedon = userManager.getLoggedOnUser();
        if(loggedon == null) {
            return new ArrayList();
        }
        if(!loggedon.isAdministrator() && !loggedon.isEditor()) {
            if(!loggedon.id.equals(userId)) {
                return new ArrayList();
            }
        }
        
        List<PmsBooking> res = new ArrayList();
        for(PmsBooking booking : finalized) {
            if(booking.userId != null && booking.userId.equals(userId)) {
                res.add(booking);
            }
        }
        return res;
    }

    @Override
    public PmsBooking getBookingFromBookingEngineId(String bookingEngineId) {
        for(PmsBooking booking : bookings.values()) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.bookingId != null && room.bookingId.equals(bookingEngineId)) {
                    return booking;
                }
            }
        }
        return null;
    }

    /**
     * If a book has been started, it the old one need to keep staying on the old room ( for statistics etc)
     * Also, if a person moves between rooms, it has to gain access to two rooms at the same time.
     * @param booking
     * @param room
     * @return 
     */
    private PmsBookingRooms splitBookingIfNesesary(PmsBooking booking, PmsBookingRooms room) {
        if(!room.isStarted() || room.isStartingToday()) {
            return room;
        }

        Gson gson = new Gson();
        String copy = gson.toJson(room);
        
        room.date.end = new Date();
        bookingEngine.changeDatesOnBooking(room.bookingId, room.date.start, room.date.end);
        
        PmsBookingRooms newRoom = gson.fromJson(copy, PmsBookingRooms.class);
        newRoom.clear();
        
        addBookingToBookingEngine(booking, newRoom);
        
        return newRoom;
    }

    private String addBookingToBookingEngine(PmsBooking booking, PmsBookingRooms room) {
        Booking bookingToAdd = createBooking(room);

        List<Booking> bookingToAddList = new ArrayList();
        bookingToAddList.add(bookingToAdd);
        if (!canAdd(bookingToAddList)) {
            return "The room can not be added, its not available.";
        }

        bookingEngine.addBookings(bookingToAddList);
        booking.addRoom(room);
        booking.attachBookingItems(bookingToAddList);
        
        return "";
    }

    @Override
    public void sendPaymentLink(String orderId, String bookingId) {
        orderIdToSend = orderId;
        doNotification("booking_sendpaymentlink", bookingId);
    }

    @Override
    public void sendMissingPayment(String orderId, String bookingId) {
        orderIdToSend = orderId;
        doNotification("booking_paymentmissing", bookingId);
    }

    private void removeInactive(PmsBookingFilter filter, List<PmsBooking> result) {
        removeNotConfirmed(filter, result);
        removeDeleted(filter, result);
        removeBeingProcessed(result);
    }

    private boolean checkDate(PmsBooking booking) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.rowCreatedDate);
        if(cal.get(Calendar.DAY_OF_MONTH) == 25 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if(cal.get(Calendar.DAY_OF_MONTH) == 26 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if(cal.get(Calendar.DAY_OF_MONTH) == 27 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if(cal.get(Calendar.DAY_OF_MONTH) == 28 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if(cal.get(Calendar.DAY_OF_MONTH) == 29 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        return false;
    }

    private void dumpBooking(PmsBooking booking) {
        if(checkDate(booking)) {
            if((!booking.payedFor && booking.deleted != null) && (booking.sessionId == null || booking.sessionId.isEmpty())) {
                booking.dump();
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                    System.out.println("\t" + type.name + " - " + room.date.start + " frem til : " + room.date.end);
                }
            } 
            User user = userManager.getUserById(booking.userId);
            if(user != null) {
                System.out.println(user.fullName); 
            }
            System.out.println("-------");
        }
    }

    PmsPricing getPriceObject() {
        return prices;
    }

    HashMap<String, PmsBooking> getBookingMap() {
        return bookings;
    }
    
    @Override
    public void addAddonsToBooking(Integer type, String roomId, boolean remove) {
        
        PmsBooking booking = getBookingFromRoom(roomId);
        checkSecurity(booking);
        PmsBookingAddonItem addonConfig = configuration.addonConfiguration.get(type);
        
        List<String> roomIds = new ArrayList();
        if(roomId == null || roomId.isEmpty()) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                roomIds.add(room.pmsBookingRoomId);
            }
        } else {
            roomIds.add(roomId);
        }
        
        for(String tmpRoomId : roomIds) {
            PmsBookingRooms room = booking.getRoom(tmpRoomId);
            room.clearAddonType(type);
            changeTimeFromAddon(addonConfig, room, remove);
            if(remove) {
                continue;
            }
            
            if(addonConfig.isSingle) {
                if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, room, room.date.start);
                    room.addons.add(toAdd);
                } else if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, room, room.date.end);
                    room.addons.add(toAdd);
                } else {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, room, room.date.start);
                    room.addons.add(toAdd);
                }
            } else {
            
                Date start = pmsInvoiceManager.normalizeDate(room.date.start, true);
                Date end = pmsInvoiceManager.normalizeDate(room.date.end, false);

                while(true) {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, room, start);
                    room.addons.add(toAdd);
                    start = addTimeUnit(start, booking.priceType);
                    if(start.after(end)) {
                        break;
                    }
                }
            }
        }
        saveBooking(booking);
    }

    public PmsBookingAddonItem createAddonToAdd(PmsBookingAddonItem addonConfig, PmsBookingRooms room, Date date) {
        Product product = productManager.getProduct(addonConfig.productId);
        
        PmsBookingAddonItem toReturn = new PmsBookingAddonItem();
        toReturn.addonType = addonConfig.addonType;
        toReturn.price = product.price;
        toReturn.priceExTaxes = product.priceExTaxes;
        toReturn.productId = product.id;
        toReturn.date = date;
        if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST) {
            toReturn.count = room.numberOfGuests;
        }
        return toReturn;
    }

    private Date addTimeUnit(Date start, Integer priceType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        if(priceType == PmsBooking.PriceType.daily) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        } else if(priceType == PmsBooking.PriceType.weekly) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
        } else if(priceType == PmsBooking.PriceType.hourly) {
            cal.add(Calendar.HOUR, 1);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    @Override
    public void updateAddons(List<PmsBookingAddonItem> items, String bookingId) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        for(PmsBookingAddonItem item : items) {
            booking.updateItem(item);
        }
        saveBooking(booking);
    }
    
    @Override
    public PmsBooking getBookingFromRoomIgnoreDeleted(String roomId) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                if (room.pmsBookingRoomId.equals(roomId)) {
                    return booking;
                }
            }
        }
        return null;
    }

    private void setPriceOnRoom(PmsBookingRooms room, String couponCode, boolean avgPrice, Integer priceType) {
        room.price = pmsInvoiceManager.calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end, avgPrice, couponCode, priceType);
        LinkedHashMap<String, Double> priceMatrix = pmsInvoiceManager.buildPriceMatrix(room, couponCode, priceType);
        LinkedHashMap<String, Double> newMatrix = new LinkedHashMap();
        for(String key : priceMatrix.keySet()) {
            Double value = priceMatrix.get(key);
            if(room.priceMatrix.containsKey(key)) {
                value = room.priceMatrix.get(key);
            }
            newMatrix.put(key, value);
        }
        room.priceMatrix = newMatrix;
        room.taxes = pmsInvoiceManager.calculateTaxes(room.bookingItemTypeId);
    }

    private void createUserForBooking(PmsBooking booking) {
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
        } else {
            booking.registrationData.resultAdded = new LinkedHashMap();
        }
    }

    @Override
    public void splitBooking(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        if(booking.getActiveRooms().size() == 1) {
            return;
        }
        PmsBooking copy = booking.copy();
        PmsBookingRooms roomToSplit = null;
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                roomToSplit = room;
                break;
            }
        }
        
        if(roomToSplit == null) {
            return;
        }
        
        User user = userManager.getUserById(booking.userId);
        
        cartManager.clear();
        List<CartItem> allItemsToMove = new ArrayList();
        Order firstOrder = null;
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            if(order.closed) {
                continue;
            }
            firstOrder = order;
            List<CartItem> itemsToRemove = new ArrayList();
            for(CartItem item : order.cart.getItems()) {
                String refId = item.getProduct().externalReferenceId;
                if(refId != null && refId.equals(roomId)) {
                    itemsToRemove.add(item);
                }
            }
            for(CartItem toRemove : itemsToRemove) {
                order.cart.removeItem(toRemove.getCartItemId());
            }
            allItemsToMove.addAll(itemsToRemove);
            orderManager.saveOrder(order);
        }
        
        copy.removeAllRooms();
        copy.addRoom(roomToSplit);
        copy.id = null;
        copy.orderIds.clear();
        copy.rowCreatedDate = new Date();
        
        if(!allItemsToMove.isEmpty()) {
            cartManager.getCart().addCartItems(allItemsToMove);
            Order order = orderManager.createOrder(user.address);
            order.payment = firstOrder.payment;
            orderManager.saveOrder(order);
            copy.orderIds.add(order.id);
        }
        
        booking.removeRoom(roomToSplit);
        saveObject(copy);
        bookings.put(copy.id, copy);
        
        saveBooking(booking);
        saveBooking(copy);
        
    }

    @Override
    public void deleteAllBookings(String code) {
        if(!code.equals("23424242423423455ggbvvcx")) {
            return;
        }
        
        for(PmsBooking booking : bookings.values()) {
            deleteObject(booking);
        }
        
        for(Booking booking : bookingEngine.getAllBookings()) {
            bookingEngine.deleteBooking(booking.id);
        }
        bookings.clear();
        
        //Delete all orders.
        List<Order> allOrders = orderManager.getOrders(null, null, null);
        for(Order order : allOrders) {
            orderManager.forceDeleteOrder(order);
        }
        
        //Delete all users
        for(User user : userManager.getAllUsers()) {
            if(user.emailAddress.toLowerCase().contains("getshop")) {
                continue;
            }
            userManager.deleteUser(user.id);
        }
        
    }

    private void checkForMissingEndDate(PmsBooking booking) {
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.date.end == null) {
                room.date.end = createInifinteDate();
            }
        }
    }

    @Override
    public void sendMessage(String bookingId, String email, String title, String message) {
        message = configuration.emailTemplate.replace("{content}", message);
        messageManager.sendMail(email, "", title, message, getFromEmail(), getFromName());
        message = "Message sent to : " + email + " Message: " + message + ", title: " + title;
        logEntry(message, bookingId, null);
   }

    void setOrderIdToSend(String id) {
        this.orderIdToSend = id;
    }

    @Override
    public void sendCode(String phoneNumber, String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                String from = "GetShop";
                if(configuration.smsName != null && configuration.smsName.isEmpty()) {
                    from = configuration.smsName;
                }
                messageManager.sendSms("sveve", phoneNumber, "Code: " + room.code, "", from);
            }
        }
    }

    @Override
    public void updateAdditionalInformationOnRooms(PmsAdditionalItemInformation info) {
        saveObject(info);
        addiotionalItemInfo.put(info.id, info);
    }

    @Override
    public HashMap<String, String> getChannelMatrix() {
        HashMap<String, String> res = new HashMap();
        
        for(PmsBooking booking : bookings.values()) {
            if(booking.channel != null && !booking.channel.trim().isEmpty()) {
                res.put(booking.channel, booking.channel);
            }
        }
        
        for(String key : res.keySet()) {
            if(configuration.channelTranslations.containsKey(key)) {
                res.put(key, configuration.channelTranslations.get(key));
            }
        }
        
        return res;
    }

    private List<PmsBooking> filterByChannel(List<PmsBooking> finalized, String channel) {
        if(channel == null || channel.trim().isEmpty()) {
            return finalized;
        }
        
        List<PmsBooking> res = new ArrayList();
        for(PmsBooking booking : finalized) {
            if(booking.channel != null && booking.channel.equals(channel)) {
                res.add(booking);
            }
        }
        return res;
    }

    @Override
    public void endRoom(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        checkSecurity(booking);
        
        Date end = new Date();
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            for(CartItem item : order.cart.getItems()) {
                if(item.getProduct().externalReferenceId.equals(roomId)) {
                    if(item.getEndingDate().after(end)) {
                        end = item.getEndingDate();
                    }
                }
            }
        }
        
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                room.date.end = end;
                bookingEngine.changeDatesOnBooking(room.bookingId, room.date.start, room.date.end);
            }
        }
        
        saveBooking(booking);
    }

    @Override
    public List<CleaningStatistics> getCleaningStatistics(Date start, Date end) {
        List<BookingItem> items = bookingEngine.getBookingItems();
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        List<CleaningStatistics> toReturn = new ArrayList<CleaningStatistics>();
        Calendar cal = Calendar.getInstance();
        for(BookingItemType type : types) {
            HashMap<Integer, Double> result = new HashMap();
            for(BookingItem item : items) {
                if(item.bookingItemTypeId != null && !item.bookingItemTypeId.equals(type.id)) {
                    continue;
                }
                
                PmsAdditionalItemInformation additional = getAdditionalInfo(item.id);
                List<Date> cleaningDates = additional.getAllCleaningDates();
                for(Date date : cleaningDates) {
                    if(date.before(start)) {
                        continue;
                    }
                    if(date.after(end)) {
                        continue;
                    }
                    cal.setTime(date);
                    int weekday = cal.get(Calendar.DAY_OF_WEEK);
                    Double tmpCount = 0.0;
                    if(result.containsKey(weekday)) {
                        tmpCount = result.get(weekday);
                    }
                    tmpCount++;
                    result.put(weekday, tmpCount);
                }
            }
            
            CleaningStatistics stats = new CleaningStatistics();
            stats.cleanings = result;
            stats.typeId = type.id;
            toReturn.add(stats);
        }
        
        return toReturn;
    }

    private void updateBooking(PmsBookingRooms room) {
        bookingEngine.changeDatesOnBooking(room.bookingId, room.date.start, room.date.end);
    }

    public List<PmsBooking> getAllBookingsFlat() {
        return new ArrayList(bookings.values());
    }

    private Product getProduct(String productId) {
        if(fetchedProducts.containsKey(productId)) {
            return fetchedProducts.get(productId);
        }
        
        Product product = productManager.getProduct(productId);
        
        fetchedProducts.put(productId, product);
        
        return product;
    }

    @Override
    public void hourlyProcessor() {
        PmsManagerProcessor processor = new PmsManagerProcessor(this);
        processor.hourlyProcessor();
    }

    private List<PmsBooking> filterByBComRateManager(List<PmsBooking> finalized, PmsBookingFilter filter) {
        if(filter == null) {
            return finalized;
        }
        
        if(!filter.onlyUntransferredToBookingCom) {
            return finalized;
        }
        
        List<PmsBooking> res = new ArrayList();
        for(PmsBooking book : finalized) {
            if(!book.transferredToRateManager) {
                res.add(book);
            }
        }
        
        return res;
    }

    @Override
    public void massUpdatePrices(PmsPricing price, String bookingId) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            for(String day : room.priceMatrix.keySet()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = sdf.parse(day);
                LocalDate ldate = new LocalDate(date);
                int dayofweek = ldate.dayOfWeek().get();
                if(dayofweek == 1) { if(price.price_mon != null) room.priceMatrix.put(day, price.price_mon); }
                if(dayofweek == 2) { if(price.price_tue != null) room.priceMatrix.put(day, price.price_tue); }
                if(dayofweek == 3) { if(price.price_wed != null) room.priceMatrix.put(day, price.price_wed); }
                if(dayofweek == 4) { if(price.price_thu != null) room.priceMatrix.put(day, price.price_thu); }
                if(dayofweek == 5) { if(price.price_fri != null) room.priceMatrix.put(day, price.price_fri); }
                if(dayofweek == 6) { if(price.price_sat != null) room.priceMatrix.put(day, price.price_sat); }
                if(dayofweek == 7) { if(price.price_sun != null) room.priceMatrix.put(day, price.price_sun); }
            }
            room.calculateAvgPrice();
        }
    }

    private String createInvoiceAttachment() {
        String invoice = invoiceManager.getBase64EncodedInvoice(orderIdToSend);
        return invoice;
    }

    void setEmailToSendTo(String email) {
        emailToSendTo = email;
    }

    private void feedGrafana(PmsBooking booking) {
        HashMap<String, Object> toAdd = new HashMap();
        int guests = 0;
        int addons = 0;
        for(PmsBookingRooms room : booking.rooms) {
            guests += room.numberOfGuests;
            addons += room.addons.size();
        }
        toAdd.put("reservations", (Number)booking.rooms.size());
        toAdd.put("booking", 1);
        toAdd.put("guests", (Number)guests);
        toAdd.put("addons", (Number)addons);
        toAdd.put("storeid", (String)storeId);
        
        GrafanaFeeder feeder = new GrafanaFeeder();
        grafanaManager.addPoint("pmsmanager", "booking", toAdd);
    }

    
    @Override
    public List<PmsAdditionalTypeInformation> getAdditionalTypeInformation() throws Exception {
        return additionDataForTypes;
    }

    @Override
    public PmsAdditionalTypeInformation getAdditionalTypeInformationById(String typeId) throws Exception {
        for(PmsAdditionalTypeInformation add : additionDataForTypes) {
            if(add.typeId.equals(typeId)) {
                return add;
            }
        }
        PmsAdditionalTypeInformation newOne = new PmsAdditionalTypeInformation();
        newOne.typeId = typeId;
        additionDataForTypes.add(newOne);
        return newOne;
    }

    @Override
    public void saveAdditionalTypeInformation(PmsAdditionalTypeInformation info) throws Exception {
        PmsAdditionalTypeInformation current = getAdditionalTypeInformationById(info.typeId);
        current.update(info);
        saveObject(current);
    }

    private void changeTimeFromAddon(PmsBookingAddonItem addonConfig, PmsBookingRooms room, boolean remove) {
        if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
            String[] defaultStartSplitted = configuration.defaultStart.split(":");
            int hour = new Integer(defaultStartSplitted[0]);
            if(!remove) {
                hour = hour-3;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.start);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            room.date.start = cal.getTime();
            if(room.bookingId != null) {
                updateBooking(room);
            }
       }
        if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
            String[] defaultEndSplitted = configuration.defaultEnd.split(":");
            int hour = new Integer(defaultEndSplitted[0]);
            if(!remove) {
                hour = hour+3;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.end);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            room.date.end = cal.getTime();
            if(room.bookingId != null) {
                updateBooking(room);
            }
       }
    }

    @Override
    public void updateAddonsCountToBooking(Integer type, String roomId, Integer count) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);
        room.updateAddonCount(type, count);
        saveBooking(booking);
    }

    private boolean doAllDeleteWhenAdded() {
        if(userManager.getLoggedOnUser() != null && userManager.getLoggedOnUser().isAdministrator()) {
            return false;
        }
        return configuration.deleteAllWhenAdded;
    }
}
