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
import com.ibm.icu.util.Calendar; 
import com.thundashop.core.amesto.AmestoSync; 
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
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
import com.thundashop.core.common.GrafanaFeederImpl;
import com.thundashop.core.common.GrafanaManager;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsHandlerAbstract;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceGeneratorCartItem;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.utils.UtilManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PmsManager extends GetShopSessionBeanNamed implements IPmsManager {

    private HashMap<String, PmsBooking> bookings = new HashMap();
    private HashMap<String, Product> fetchedProducts = new HashMap();
    private HashMap<String, PmsAddonDeliveryLogEntry> deliveredAddons = new HashMap();
    private HashMap<String, PmsCareTaker> careTaker = new HashMap();
    private HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo = new HashMap();
    private HashMap<String, PmsPricing> priceMap = new HashMap();
    private HashMap<String, ConferenceData> conferenceDatas = new HashMap();
    private PmsConfiguration configuration = new PmsConfiguration();
    private List<String> repicientList = new ArrayList();
    private List<String> warnedAbout = new ArrayList();
    private List<PmsAdditionalTypeInformation> additionDataForTypes = new ArrayList();
    private String specifiedMessage = "";
    Date lastOrderProcessed;
    private List<PmsLog> logentries = new ArrayList();
    private boolean initFinalized = false;
    private String orderIdToSend;
    private Date lastCheckForIncosistent;
    private String emailToSendTo;
    private String phoneToSend;
    private String prefixToSend;
    private List<Order> tmpOrderList;
    
    private HashMap<String, PmsBookingFilter> savedFilters = new HashMap();
    
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
    DoorManager doorManager;

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
    UtilManager utilManager;
    
    @Autowired
    GrafanaManager grafanaManager;
    
    @Autowired
    GetShop getShop;
    

    @Override
    public void dataFromDatabase(DataRetreived data) {
        Calendar toCheck = Calendar.getInstance();
        
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking) dataCommon;
                bookings.put(booking.id, booking);
            }
            if (dataCommon instanceof ConferenceData) {
                ConferenceData conferenceRoomData = (ConferenceData) dataCommon;
                conferenceDatas.put(conferenceRoomData.id, conferenceRoomData);
            }
            if (dataCommon instanceof PmsPricing) {
                PmsPricing price = (PmsPricing) dataCommon;
                if(price.code.isEmpty()) {
                    price.code = "default";
                    saveObject(price);
                }
                priceMap.put(price.code, price);
            }
            if (dataCommon instanceof PmsCareTaker) {
                careTaker.put(dataCommon.id, (PmsCareTaker) dataCommon);
            }
            if (dataCommon instanceof PmsBookingFilter) {
                savedFilters.put(((PmsBookingFilter) dataCommon).filterName, (PmsBookingFilter)dataCommon);
            }
            if (dataCommon instanceof PmsConfiguration) {
                checkConvertLockConfigs((PmsConfiguration) dataCommon);
                PmsConfiguration toAdd = (PmsConfiguration) dataCommon;
                toCheck.setTime(toAdd.rowCreatedDate);
                if(toCheck.get(Calendar.YEAR) == 2017 && toCheck.get(Calendar.DAY_OF_YEAR) == 8) {
                    System.out.println("delete this configuration, failure to add today: " + toAdd.rowCreatedDate + "(" + getName() + ")");
                    deleteObject(dataCommon);
                } else {
                    if(configuration.rowCreatedDate == null || (configuration.rowCreatedDate.before(dataCommon.rowCreatedDate) || configuration.rowCreatedDate.equals(dataCommon.rowCreatedDate))) {
                        if(configuration.rowCreatedDate != null) { deleteObject(configuration); }
                        configuration = toAdd;
                    } else {
                        if(configuration.rowCreatedDate != null) { deleteObject(dataCommon); }
                        System.out.println(getName() + " : " +dataCommon.rowCreatedDate);
                    }
                }
            }
            if (dataCommon instanceof PmsAddonDeliveryLogEntry) {
                deliveredAddons.put(dataCommon.id, (PmsAddonDeliveryLogEntry) dataCommon);
            }
            if( dataCommon instanceof PmsAdditionalTypeInformation) {
                PmsAdditionalTypeInformation cur = (PmsAdditionalTypeInformation)dataCommon;
                boolean found = false;
                for(PmsAdditionalTypeInformation info : additionDataForTypes) {
                    if(info.typeId.equals(cur.typeId)) {
                        found = true;
                        deleteObject(dataCommon);
                    }
                }
                if(!found) {
                    additionDataForTypes.add(cur);
                }
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
         
        createScheduler("pmsmailstats", "1 23 * * *", PmsMailStatistics.class);
        createScheduler("pmsprocessor", "* * * * *", CheckPmsProcessing.class);
        createScheduler("pmsprocessor2", "5 * * * *", CheckPmsProcessingHourly.class);
        createScheduler("pmsprocessor3", "1,5,10,15,20,25,30,35,40,45,50,55 * * * *", CheckPmsFiveMin.class);
        
        if(applicationPool.getAvailableApplications().contains(applicationPool.getApplication("66b4483d-3384-42bb-9058-2ac915c77d80"))) {
            createScheduler("amestosync", "00 08 * * *", AmestoSync.class);
        }
    }

    @Override
    public void createAllVirtualOrders() {
        for (PmsBooking booking : bookings.values()) {
                orderManager.deleteVirtualOrders(booking.id);
                pmsInvoiceManager.createVirtualOrder(booking.id);
        }
    }
    
    @Override
    public void setSession(Session session) {
        super.setSession(session);
    }
    
    private PmsPricing getDefaultPriceObject() {
        PmsPricing price = priceMap.get("default");
        if(price != null) {
            return price;
        }
        return new PmsPricing();
    }
    
    public PmsPricing getPriceObject(String code) {
        PmsPricing object = priceMap.get(code);
        if(object == null) {
            object = priceMap.get("default");
        }
        return object;
    }
    
    @Override
    public List<Room> getAllRoomTypes(Date start, Date end) {
        User loggedon = userManager.getLoggedOnUser();
        boolean isAdmin = false;
        if(loggedon != null) {
            isAdmin = (loggedon.isAdministrator() || loggedon.isEditor());
        }
        
        List<Room> result = new ArrayList();
        List<BookingItemType> allGroups = bookingEngine.getBookingItemTypes();

        Collections.sort(allGroups, new Comparator<BookingItemType>() {
            public int compare(BookingItemType o1, BookingItemType o2) {
                return o1.order.compareTo(o2.order);
            }
        });

        for (BookingItemType type : allGroups) {
            if (!type.visibleForBooking && !isAdmin) {
                continue;
            }
            
            Room roomToAdd = new Room();
            roomToAdd.type = type;
            
            PmsBookingRooms room = new PmsBookingRooms();
            room.bookingItemTypeId = type.id;
            room.date = new PmsBookingDateRange();
            room.date.start = start;
            room.date.end = end;
            
            PmsBooking booking = new PmsBooking();
            booking.priceType = PmsBooking.PriceType.daily;
            setPriceOnRoom(room, true, booking);
            
            roomToAdd.price = room.price;
            result.add(roomToAdd);
        }

        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        if(booking.couponCode != null && !booking.couponCode.isEmpty()) {
            if(booking.discountType != null && booking.discountType.equals("coupon")) {
                Coupon cop = cartManager.getCoupon(booking.couponCode);
                if(cop == null) {
                    booking.couponCode = "";
                } else if(cop.channel != null && !cop.channel.isEmpty()) {
                    booking.channel = cop.channel;
                }
            }
        }
        
        if(booking.couponCode != null && booking.couponCode.contains(":")) {
            booking.channel = booking.couponCode.split(":")[0];
        }

        PmsPricing prices = getPriceObjectFromBooking(booking);
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
            if (room.date.end != null && room.date.start != null && !getConfigurationSecure().hasNoEndDate) {
                totalDays = Days.daysBetween(new LocalDate(room.date.start), new LocalDate(room.date.end)).getDays();
            }
            
            pmsInvoiceManager.updateAddonsByDates(room);
            pmsInvoiceManager.updatePriceMatrix(booking, room, Integer.SIZE);

            room.count = totalDays;
            setPriceOnRoom(room, true, booking);
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

        if(booking.isDeleted && booking.getActiveRooms().size() > 0) {
            booking.isDeleted = false;
        }
        
        saveObject(booking);
        bookings.put(booking.id, booking);
    }

    @Override
    public PmsBooking getCurrentBooking() {
        if (getSession() == null) {
            logPrint("Warning, no session set yet");
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
            hardDeleteBooking(currentBooking, "startbooking");
        }

        PmsBooking booking = new PmsBooking();

        try {
            booking.sessionId = getSession().id;
            setBooking(booking);
        } catch (Exception ex) {
            logPrintException(ex);
        }
        return booking;
    }

    @Override
    public PmsBooking completeCurrentBooking() {
        PmsBooking booking = getCurrentBooking();
        return doCompleteBooking(booking);
    }

    @Override
    public String createPrepaymentOrder(String bookingId) {
        return pmsInvoiceManager.createPrePaymentOrder(getBooking(bookingId));
    }

    private Integer completeBooking(List<Booking> bookingsToAdd, PmsBooking booking) throws ErrorException {
        boolean canAdd = canAdd(bookingsToAdd);
        if (!canAdd && !configuration.deleteAllWhenAdded) {
            return -2;
        }
        if(canAdd) {
            addDefaultAddons(booking);
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
        }
        
        booking.sessionId = "";
        verifyPhoneOnBooking(booking);
        saveBooking(booking);
        feedGrafana(booking);
        logPrint("Booking has been completed: " + booking.id);
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
        if (sessionId == null || sessionId.isEmpty()) {
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
        boolean unsettled = false;
        if(filter != null && filter.filterType != null && filter.filterType.equals("unsettled")) {
            unsettled = true;
            filter.filterType = "checkout";
        }
        if(filter != null && filter.searchWord != null) {
            filter.searchWord = filter.searchWord.trim();
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, 2016);
        
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
                    continue;
                } else if (booking.containsSearchWord(filter.searchWord)) {
                    result.add(booking);
                    continue;
                }

                for (PmsBookingRooms room : booking.getActiveRooms()) {
                    boolean found = false;
                    if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                        BookingItem item = bookingEngine.getBookingItemUnfinalized(room.bookingItemId);
                        if (item != null && item.bookingItemName != null && item.bookingItemName.contains(filter.searchWord)) {
                            if(!result.contains(booking)) {
                                result.add(booking);
                                found = true;
                            }
                        }
                        if(room.containsSearchWord(filter.searchWord)) {
                            result.add(booking);
                                found = true;
                        }
                    }
                    if(found) {
                       continue; 
                    }
                }
            }
        } else if (filter.filterType == null || filter.filterType.isEmpty() || filter.filterType.equals("registered")) {
            for (PmsBooking booking : bookings.values()) {
                if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                    if(filter.userId == null || filter.userId.isEmpty()) {
                        result.add(booking);
                    } else if(filter.userId.equals(booking.userId)) {
                        result.add(booking);
                    }
                }
            }
        } else if (filter.filterType.equals("active") || 
                filter.filterType.equals("inhouse") || 
                filter.filterType.equals("unpaid") || 
                filter.filterType.equals("afterstayorder")) {
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
        finalized = filterTypes(finalized, filter.typeFilter);
        finalized = filterByUser(finalized,filter.userId);
        finalized = filterByChannel(finalized,filter.channel);
        finalized = filterByBComRateManager(finalized,filter);
        finalized = filterByUnpaid(finalized,filter);
        if(unsettled) {
            finalized = filterByUnsettledAmounts(finalized);
        }
        
        //Dump bookings
        if(getConfiguration().usePriceMatrixOnOrder) {
            for(PmsBooking booking : finalized) {
                boolean needSave = false;
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(room.priceMatrix == null || room.priceMatrix.keySet().isEmpty()) {
                        pmsInvoiceManager.correctFaultyPriceMatrix(room, booking);
                        needSave = true;
                    }
                }
                if(needSave) {
                    logEntry("The pricematrix has been erased, it was rebuilt using the average price.", booking.id, null);
                    saveBooking(booking);
                }

            }
        }
        

        
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
    
    @Override
    public PmsBooking getBooking(String bookingId) {

        PmsBooking booking = bookings.get(bookingId);
        if (booking == null) {
            return null;
        }
        checkSecurity(booking);
        
        booking.calculateTotalCost();
        Double totalOrder = 0.0;
        for(String orderId : booking.orderIds) {
            Order order = orderManager.getOrder(orderId);
            totalOrder += orderManager.getTotalAmount(order);
        }
        Double diff = booking.getTotalPrice() - totalOrder;
        if(diff < 2 && diff > 2) {
            diff = 0.0;
        }
        booking.unsettled = diff;
        
        return finalize(booking);
    }

    
    private String generateCode() {
        for (int i = 0; i < 100000; i++) {
            int start = 1;
            int end = 10;
            for (int j = 0; j < configuration.getDefaultLockServer().codeSize - 1; j++) {
                start *= 10;
                end *= 10;
            }
            end = end - 1;

            Integer newcode = new Random().nextInt(end - start) + start;
            if (!codeExist(newcode)) {
                return newcode.toString();
            }
        }
        logPrint("Tried 100 000 times to generate a code without success");
        return null;
    }

    private boolean codeExist(int newcode) {
        for(PmsBooking booking : bookings.values()) {
            if(booking == null || booking.getActiveRooms() == null) {
                continue;
            }
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if(room == null) {
                    continue;
                }
                if(room.isEndedDaysAgo(7)) {
                    //If the room ended one week ago, the code can be reused.
                    continue;
                }
                if (room.code != null && (room.code.equals(newcode + ""))) {
                    return true;
                }
            }
        } 
        return false;
    }
    
    public PmsBooking finalize(PmsBooking booking) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.add(Calendar.HOUR_OF_DAY, -1);
        if (booking.sessionId != null && !booking.sessionId.isEmpty() && !booking.avoidAutoDelete) {
            if (!booking.rowCreatedDate.after(nowCal.getTime())) {
                hardDeleteBooking(booking, "finalize");
                return null;
            }
        }
        
        if (booking.isDeleted) {
            booking.state = 2;
            return booking;
        }
        boolean needSaving = false;
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if(room.code == null || room.code.isEmpty()) {
                room.code = generateCode();
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
            if(room.price.isNaN() || room.price.isInfinite()) {
                room.price = 0.0;
            }
        }
        
        for(String orderId : booking.orderIds) {
            if(!booking.incOrderIds.containsKey(orderId)) { 
                if(orderManager.orderExists(orderId)) {
                    Order order = orderManager.getOrder(orderId);
                    booking.incOrderIds.put(orderId, order.incrementOrderId);
                    needSaving = true;
                }
            }
        }
        
        if(needSaving) {
            saveBooking(booking);
        }
        
        PmsPricing prices = getPriceObjectFromBooking(booking);
        if(prices != null && prices.defaultPriceType == PmsBooking.PriceType.daily && configuration.requirePayments) {
            booking.calculateTotalCost();
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
            if(booking.source != null && !booking.source.isEmpty()) {
                continue;
            }
            if (!added.contains(booking.id)) {
                bookingEngine.deleteBooking(booking.id);
            }
        }
    }

    @Override
    public String setNewRoomType(String roomId, String bookingId, String newType) {
        PmsBooking booking = null;
        if(bookingId != null) {
            booking = getBooking(bookingId);
        } else {
            booking = getBookingFromRoom(roomId);
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            bookingEngine.changeBookingItemOnBooking(room.bookingId, "");
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
            logPrintException(ex);
            return ex.getMessage();
        }
        return "";

    }

    @Override
    public PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end) {
        if(start.after(end)) {
            return null;
        }
        PmsBooking booking = getBooking(bookingId);
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            Date oldStart = new Date();
            Date oldEnd = new Date();
            oldStart.setTime(room.date.start.getTime());
            oldEnd.setTime(room.date.end.getTime());        
            changeDatesOnRoom(room, start, end);
            String logText = "New date set from " + convertToStandardTime(oldStart) + " - " + convertToStandardTime(oldEnd) + " to, " + convertToStandardTime(start) + " - " + convertToStandardTime(end);
            logEntry(logText, bookingId, room.bookingItemId, roomId);
            doNotification("date_changed", booking, room);
            return room;
        } catch (BookingEngineException ex) {
//            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveBooking(PmsBooking booking) throws ErrorException {
        if (booking.id == null || booking.id.isEmpty()) {
            throw new ErrorException(1000015);
        }
        
        if(getConfigurationSecure().usePriceMatrixOnOrder) {
            try {
                boolean sent = false;
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(room.priceMatrix == null || room.priceMatrix.keySet().isEmpty()) {
                        pmsInvoiceManager.correctFaultyPriceMatrix(room, booking);
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        bookings.put(booking.id, booking);
        try {
            verifyPhoneOnBooking(booking);
        }catch(Exception e) {
            logPrintException(e);
        }
        
        if(booking.priceType == PmsBooking.PriceType.daily) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
            }
        }
        if(getConfiguration().createVirtualOrders) {
            pmsInvoiceManager.createVirtualOrder(booking.id);
        }
        saveObject(booking);
    }


    @Override
    public String setBookingItem(String roomId, String bookingId, String itemId, boolean split) {
        PmsBooking booking = getBookingFromRoom(roomId);
        if (booking == null) {
            return "Booking does not exists";
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            return setBookingItemAndDate(roomId, itemId, split, room.date.start, room.date.end);
        }catch(Exception e) {
            return e.getMessage();
        }
        
    }

    @Override
    public String setBookingItemAndDate(String roomId, String itemId, boolean split, Date start, Date end) {
        if(start.after(end)) {
            return "Date range is invalid, the date starts after it ends.";
        }
        PmsBooking booking = getBookingFromRoom(roomId);
        if (booking == null) {
            return "Booking does not exists";
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            if (room == null) {
                return "Room does not exists";
            }
            if(split) { 
                room = splitBookingIfNesesary(booking, room);
            }
            checkIfRoomShouldBeUnmarkedDirty(room, booking.id);
            if(room.bookingId != null && !room.bookingId.isEmpty() && !room.deleted && !booking.isDeleted) {
                bookingEngine.changeBookingItemAndDateOnBooking(room.bookingId, itemId, start, end);
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
            
            logEntry(logText, booking.id, null, roomId);
            if(room.isStarted() && !room.isEnded()) {
                doNotification("room_changed", booking, room);
            }
        } catch (BookingEngineException ex) {
            return ex.getMessage();
        }
        saveBooking(booking);
        return "";
    }

    @Override
    public PmsPricing getPrices(Date start, Date end) {
        return priceMap.get("default");
    }

    @Override
    public PmsPricing getPricesByCode(String code, Date start, Date end) {
        return getPriceObject(code);
    }

    @Override
    public PmsPricing setPrices(String code, PmsPricing newPrices) {
        PmsPricing prices = getPriceObject(code);
        prices.defaultPriceType = newPrices.defaultPriceType;
        prices.progressivePrices = newPrices.progressivePrices;
        prices.pricesExTaxes = newPrices.pricesExTaxes;
        prices.privatePeopleDoNotPayTaxes = newPrices.privatePeopleDoNotPayTaxes;
        prices.channelDiscount = newPrices.channelDiscount;
        prices.derivedPrices = newPrices.derivedPrices;
        prices.productPrices = newPrices.productPrices;
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
        filter.fromAdministrator = true;
        
        if(filter.endInvoiceAt == null) {
            PmsBooking booking = getBooking(bookingId);
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(filter.endInvoiceAt == null || filter.endInvoiceAt.before(room.date.end)) {
                    filter.endInvoiceAt = room.date.end;
                }
            }
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
        return toReturn;
    }

    @Override
    public void saveConfiguration(PmsConfiguration notifications) {
        if(configuration.rowCreatedDate != null && (notifications.id == null || !notifications.id.equals(configuration.id))) {
            logPrint("Tried to save an invalid configuration object");
            return;
        }
        this.configuration = notifications;
        notifications.finalize();
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
        emailToSendTo = null;
    }

    private String notify(String key, PmsBooking booking, String type, PmsBookingRooms room) {
        if(booking != null && booking.silentNotification) {
            return "Not notified, silent booking: " + type;
        }
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
        if((message == null || message.isEmpty()) && key.toLowerCase().contains("room_resendcode")) {
            message = "Code {code} room {roomName}.";
        }

        if(key.startsWith("booking_sendpaymentlink") || 
                key.startsWith("booking_unabletochargecard") || 
                key.startsWith("booking_paymentmissing") || 
                key.startsWith("order_")) {
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
            String phone = user.cellPhone;
            String prefix = user.prefix;
            if(phoneToSend != null) {
                phone = phoneToSend;
                prefix = prefixToSend;
                phoneToSend = null;
            }
            if(prefix != null && (prefix.equals("47") || prefix.equals("+47"))) {
                messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
            } else {
                messageManager.sendSms("nexmo", phone, message, prefix, configuration.smsName);
            }
            repicientList.add(phone);
        } else {
            String title = configuration.emailTitles.get(key);
            String fromName = getFromName();
            String fromEmail = getFromEmail();
            HashMap<String, String> attachments = new HashMap();
            
            if (key.startsWith("booking_confirmed")) {
                attachments.putAll(createICalEntry(booking));
            }
            if (key.startsWith("booking_completed")) {
                attachments.put("termsandcondition.html", createContractAttachment(booking.id));
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

            repicientList.add(recipientEmail);
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
                    if(prefix != null && (prefix.equals("47") || prefix.equals("+47"))) {
                        messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
                    } else {
                        messageManager.sendSms("nexmo", phone, message, prefix, configuration.smsName);
                    }
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

                        if(guest.prefix != null && (guest.prefix.equals("47") || guest.prefix.equals("+47"))) {
                            messageManager.sendSms("sveve", phone, message, guest.prefix, configuration.smsName);
                        } else {
                            messageManager.sendSms("nexmo", phone, message, guest.prefix, configuration.smsName);
                        }
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
        formater.setProductManager(productManager);

        if (this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }

        if (room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
            try {
                PmsAdditionalItemInformation addinfo = getAdditionalInfo(room.bookingItemId);
                message = message.replace("{roomDescription}", addinfo.textMessageDescription);
            }catch(Exception e) {}
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
        convertTextDates(filter);
        filter.filterType = "active";
        List<PmsBooking> allBookings = getAllBookings(filter);
        PmsPricing prices = getDefaultPriceObject();
        PmsStatisticsBuilder builder = new PmsStatisticsBuilder(allBookings, 
                prices.pricesExTaxes, 
                userManager,
                new ArrayList(bookings.values()), 
                addiotionalItemInfo,
                orderManager,
            bookingEngine);
        builder.setBudget(getConfigurationSecure().budget);
        int totalRooms = bookingEngine.getBookingItems().size();
        if(!filter.typeFilter.isEmpty()) {
            totalRooms = 0;
            for(String id : filter.typeFilter) {
                totalRooms += bookingEngine.getBookingItemsByType(id).size();
            }
        }
        
        if(filter.itemFilter != null && !filter.itemFilter.isEmpty()) {
            totalRooms = filter.itemFilter.size();
        }
        
        PmsStatistics result = builder.buildStatistics(filter, totalRooms);
        result.salesEntries = builder.buildOrderStatistics(filter, orderManager);
        result.setView(filter);
        result.buildTotal();
        result.buildTotalSales();

        
        HashMap<String, PmsDeliverLogStats> deliveryStats = new HashMap();
        for(PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
            if(filter.startDate.before(entry.rowCreatedDate) && filter.endDate.after(entry.rowCreatedDate)) {
                PmsDeliverLogStats res = new PmsDeliverLogStats();
                if(deliveryStats.containsKey(entry.productId)) {
                    res = deliveryStats.get(entry.productId);
                }
                res.productId = entry.productId;
                res.count++;
                res.priceInc += entry.price;
                deliveryStats.put(res.productId, res);
            }
        }
        
        result.deliveryStats = deliveryStats;
        
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
                    remove.credited = false;
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
                room.delete();
            }
        }
        
        logEntry("Deleted booking", bookingId, null);
        saveBooking(booking);
    }

    
    private void hardDeleteBooking(PmsBooking booking, String source) {
        System.out.println("Deleting, source: " + source);
        bookings.remove(booking.id);
        booking.deletedBySource = source;
        if(booking.sessionId == null || booking.sessionId.isEmpty()) {
            String text = "Booking which should not be deleted where tried deleted: " + "<br><br>, channel: " + booking.channel + ", wubook rescode: " + booking.wubookreservationid;
            text += "<br>";
            text += "<br>";
            text += booking.createSummary(bookingEngine.getBookingItemTypes());
            messageManager.sendErrorNotification(text, null);
        } else {
            deleteObject(booking);
        }
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
        logPrint("Arx is down");
    }

    @Override
    public PmsIntervalResult getIntervalAvailability(PmsIntervalFilter filter) {
        PmsIntervalResult res = new PmsIntervalResult();
        List<String> types = new ArrayList();
        if(filter.selectedDefinedFilter != null && !filter.selectedDefinedFilter.isEmpty()) {
            PmsBookingFilter bookingfilter = getPmsBookingFilter(filter.selectedDefinedFilter);
            types = bookingfilter.typeFilter;
        }
            
        for (BookingItemType type : bookingEngine.getBookingItemTypes()) {
            if(!types.isEmpty() && !types.contains(type.id)) {
                continue;
            }
            BookingTimeLineFlatten line = bookingEngine.getTimelines(type.id, filter.start, filter.end);
            res.typeTimeLines.put(type.id, line.getTimelines(filter.interval-21600, 21600));
        }

        List<BookingItem> items = bookingEngine.getBookingItems();

        for (BookingItem item : items) {
            if(!types.isEmpty() && !types.contains(item.bookingItemTypeId)) {
                continue;
            }
            BookingTimeLineFlatten line = bookingEngine.getTimeLinesForItem(filter.start, filter.end, item.id);
            List<BookingTimeLine> timelines = line.getTimelines(filter.interval-21600, 21600);
            LinkedHashMap<Long, IntervalResultEntry> itemCountLine = new LinkedHashMap();
            for (BookingTimeLine tl : timelines) {
                IntervalResultEntry tmpres = new IntervalResultEntry();
                tmpres.bookingIds = tl.bookingIds;
                tmpres.count = tl.count;
                tmpres.time = tl.start.getTime();
                
                if(!tmpres.bookingIds.isEmpty()) {
                    Booking bookingEngineBooking = bookingEngine.getBooking(tmpres.bookingIds.get(0));
                    if(bookingEngineBooking.source != null && !bookingEngineBooking.source.isEmpty()) {
                        tmpres.name = bookingEngineBooking.source;
                    } else {
                        PmsBooking booking = getBookingFromBookingEngineId(tmpres.bookingIds.get(0));
                        if (booking != null && booking.userId != null) {
                            User user = userManager.getUserById(booking.userId);
                            if(user != null) {
                                tmpres.name = user.fullName;
                            }
                        }
                    }
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
    
    @Override
    public Boolean isUsedToday(String itemId) {
        PmsAdditionalItemInformation addiotionalInfo = getAdditionalInfo(itemId);
        return addiotionalInfo.isUsedToday();
    }

    @Override
    public PmsAdditionalItemInformation getAdditionalInfo(String itemId) {
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
    public void forceMarkRoomAsCleaned(String itemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        String userId = null;
        if(getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
        }
        additional.markCleaned(userId);
        saveAdditionalInfo(additional);
        List<Booking> allBookings = bookingEngine.getAllBookingsByBookingItem(itemId);
        List<Booking> bookingsToDelete = new ArrayList();
        for(Booking book : allBookings) {
            if(book.source != null && book.source.equals("cleaning")) {
                if(book.bookingItemId.equals(itemId)) {
                    bookingsToDelete.add(book);
                }
            }
        }
        for(Booking remove : bookingsToDelete) {
            bookingEngine.deleteBooking(remove.id);
        }
    }
    
    @Override
    public void markRoomAsCleaned(String itemId) {
        if(getConfiguration().whenCleaningEndStayForGuestCheckinOut) {
            endStay(itemId);
        }
        
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        Date start = new Date();
        Calendar end = Calendar.getInstance();
        end.setTime(start);
        boolean bookingStartingToday = bookingEngine.hasBookingsStartingBetweenTime(start, end.getTime(), itemId);
        boolean itemInUse = bookingEngine.itemInUseBetweenTime(start, end.getTime(), itemId);

        if (bookingStartingToday || !itemInUse) {
            //Only mark room cleaned if a new booking is 
            forceMarkRoomAsCleaned(itemId);
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
        start.set(Calendar.HOUR_OF_DAY, 17);
        
        Calendar end = start.getInstance();
        end.setTime(start.getTime());
        end.set(Calendar.HOUR_OF_DAY, 18);

        additionalInfo.isClean();
        additionalInfo.inUseByCleaning = false;
        additionalInfo.inUse = bookingEngine.itemInUseBetweenTime(start.getTime(), end.getTime(), additionalInfo.itemId);
        if(additionalInfo.inUse) {
            BookingTimeLineFlatten timeline = bookingEngine.getTimeLinesForItem(start.getTime(), end.getTime(), additionalInfo.itemId);
            for(Booking book : timeline.getBookings()) {
                if(book.source != null && book.source.equals("cleaning")) {
                    additionalInfo.inUseByCleaning = true;
                }
            }
        }
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
//
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
        if (room.isEndingToday(day) && !getConfiguration().autoExtend) {
            return false;
        }
        if (!room.isActiveOnDay(day)) {
            return false;
        }
        int days = Days.daysBetween(new LocalDate(room.date.cleaningDate), new LocalDate(day)).getDays();
        if (days == 0 && room.isSameDay(room.date.start, room.date.cleaningDate)) {
            return false;
        }
        int interval = configuration.cleaningInterval;
        if (room.intervalCleaning != null && room.intervalCleaning != 0) {
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
        String vat = result.get("company_vatNumber");
        if(vat != null) {
            vat = vat.trim();
            List<Company> companies = userManager.getCompaniesByVatNumber(vat);
            if(!companies.isEmpty()) {
                return companies.get(0);
            }
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
        if (getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.DAILY)) {
            calStart.add(Calendar.HOUR_OF_DAY, -4);
        }
        calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultTimeStart[0]));
        calStart.set(Calendar.MINUTE, new Integer(defaultTimeStart[1]));
        calStart.set(Calendar.SECOND, 0);

        PmsBookingDateRange range = new PmsBookingDateRange();
        range.start = calStart.getTime();

        if (getConfiguration().bookingTimeInterval.equals(PmsConfiguration.PmsBookingTimeInterval.DAILY)) {
            calStart.add(Calendar.HOUR_OF_DAY, -3);
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
    public String addBookingItemType(String bookingId, String type, Date start, Date end, String guestInfoFromRoom) {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms room = new PmsBookingRooms();
        BookingItemType typeToAdd = bookingEngine.getBookingItemType(type);
        room.bookingItemTypeId = type;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.guests.add(new PmsGuests());
        setPriceOnRoom(room, true, booking);

        
        if(guestInfoFromRoom != null && !guestInfoFromRoom.isEmpty()) {
            PmsBooking bookingforguest = getBookingFromRoom(guestInfoFromRoom);
            PmsBookingRooms roomforGuest = bookingforguest.findRoom(guestInfoFromRoom);
            Gson gson = new Gson();
            String copyroom = gson.toJson(roomforGuest);
            PmsBookingRooms copiedRoom = gson.fromJson(copyroom, PmsBookingRooms.class);
            room.guests = copiedRoom.guests;
        }
        
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
        
        if(!getConfigurationSecure().avoidRandomRoomAssigning) {
            long seed = System.nanoTime();
            Collections.shuffle(items, new Random(seed));
            Collections.shuffle(items, new Random(seed));
        }
            
        if (items.isEmpty()) {
            logPrint("No items available?");
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
            PmsBooking booking = getBookingFromRoomSecure(room.pmsBookingRoomId);
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

        PmsLog log = new PmsLog();
        log.bookingId = bookingId;
        log.bookingItemId = itemId;
        log.roomId = roomId;
        log.logText = logText;
        logEntryObject(log);
    }

    @Override
    public List<PmsLog> getLogEntries(PmsLog filter) {
        
        List<PmsLog> res = new ArrayList();
        if(filter != null) {
            for (PmsLog log : logentries) {
                if(filter.tag != null && !filter.tag.isEmpty()) {
                    if(log.tag == null || !log.tag.equals(filter.tag)) {
                        continue;
                    }
                }
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
                if(o2.rowCreatedDate != null && o1.rowCreatedDate != null) {
                   return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
                }
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
        PmsBooking booking = getBookingFromRoomSecure(pmsBookingRoomId);
        if(booking == null) {
            return null;
        }
        checkSecurity(booking);
        return booking;
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
        if(getConfigurationSecure().autoExtend && !room.keyIsReturned) {
            return false;
        }
//        if (room.date.exitCleaningDate == null) {
            room.date.exitCleaningDate = room.date.end;
            if (configuration.cleaningNextDay) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.date.exitCleaningDate);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                room.date.exitCleaningDate = cal.getTime();
            }
//        }

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
                
                if(!room.addedToArx && getConfiguration().hasLockSystem()) {
                    continue;
                }

                String ownerMail = storeManager.getMyStore().configuration.emailAdress;
                String addressMail = storeManager.getMyStore().webAddress;
                if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
                    logPrint("Booking started without item, Owner: " + ownerMail + ", address:" + addressMail);
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
        if(interval == 0) {
            return;
        }
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.pmsBookingRoomId.equals(roomId)) {
                    room.intervalCleaning = interval;
                    logEntry("New cleaning interval set to " + interval, booking.id, room.bookingItemId);
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
            if(!getConfigurationSecure().hasLockSystem()) {
                add.markDirty();
            }
            saveObject(add);
            if(room.isStarted() && !room.isEnded()) {
                doNotification("room_changed", booking, room);
            }
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

        boolean deleteWhenAdded = getConfigurationSecure().deleteAllWhenAdded;
        if(!deleteWhenAdded) {
            if (booking.getActiveRooms() == null || booking.getActiveRooms().isEmpty()) {
                return false;
            }
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
            if(getConfigurationSecure().hasNoEndDate && room.date.end == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.date.start);
                cal.add(Calendar.YEAR, 100);
                room.date.end = cal.getTime();
            }
            if (!bookingEngine.canAdd(bookingToAdd) || doAllDeleteWhenAdded()) {
                if(getConfigurationSecure().supportRemoveWhenFull) {
                    room.canBeAdded = false;
                    room.delete();
                }
                BookingItemType item = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                String name = "";
                if (item != null) {
                    name = item.name;
                }
                String text = "Removed room: " + name + " since it can't be added: " + "<br><br>, channel: " + booking.channel + ", wubook rescode: " + booking.wubookreservationid;
                text += "<br>";
                text += "<br>";
                text += booking.createSummary(bookingEngine.getBookingItemTypes());
                if(booking.userId != null) {
                    User user = userManager.getUserById(booking.userId);
                    if(user != null) {
                        text += "User : " + userManager.getUserById(booking.userId).fullName;
                    }
                }
                logEntry(text, booking.id, null);
                if(!getConfigurationSecure().supportRemoveWhenFull) {
                    messageManager.sendErrorNotification("Failed to add room, since its full, this should not happend and happends when people are able to complete a booking where its fully booked, " + text, null);
                }
            }
            
            if(!room.isDeleted() && room.canBeAdded) {
                bookingsToAdd.add(bookingToAdd);
            }
        }
        return bookingsToAdd;
    }

    @Override
    public void checkDoorStatusControl() throws Exception {
        PmsManagerDoorSurveilance doorsurv = new PmsManagerDoorSurveilance(this);
        doorsurv.checkStatus();
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
        if(booking == null) {
            logPrint("Nullbooking happened, on checksecurity");
        }
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
            logPrint("Need to set a new start date");
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

    private Coupon getCouponCode(PmsBooking booking) {
        String couponCode = booking.couponCode;
        if(booking.discountType == null || booking.discountType.isEmpty() || booking.discountType.equals("none")) {
            return null;
        }
        if(booking.discountType.equals("partnership")) {
            if(couponCode.contains(":")) {
                String[] couponCodes = couponCode.split(":");
                if(couponCodes.length > 1) {
                    couponCode = "partnership:" + couponCodes[0];
                }
            }
        }
        
        return cartManager.getCoupon(couponCode); 
    }

    @Override
    public List<PmsRoomSimple> getSimpleRooms(PmsBookingFilter filter) {
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        List<PmsRoomSimple> res = filtering.filterRooms(filter);
        doSorting(res, filter);
        if(filter.includeCleaningInformation) {
            for(PmsRoomSimple r : res) {
                if(r.bookingItemId != null && !r.bookingItemId.isEmpty()) {
                    r.roomCleaned = isClean(r.bookingItemId);
                    r.hasBeenCleaned = (r.roomCleaned || isUsedToday(r.bookingItemId));
                }
            }
        }
        return res;
    }


    @Override
    public List<PmsRoomSimple> getRoomsNeedingIntervalCleaningSimple(Date day) {
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        List<PmsBookingRooms> rooms = getRoomsNeedingIntervalCleaning(day);
        List<PmsRoomSimple> res = new ArrayList();
        for(PmsBookingRooms r : rooms) {
            PmsBooking booking = getBookingFromRoom(r.pmsBookingRoomId);
            PmsRoomSimple converted = filtering.convertRoom(r, booking);
            converted.roomCleaned = isClean(r.bookingItemId);
            converted.hasBeenCleaned = (converted.roomCleaned || isUsedToday(r.bookingItemId));

            res.add(converted);
        }
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
        start.set(Calendar.HOUR_OF_DAY, 1);
        end.set(Calendar.HOUR_OF_DAY, 23);
        filter.startDate = start.getTime();
        filter.endDate = end.getTime();
        
        List<PmsRoomSimple> allRooms = getSimpleRooms(filter);
        String from = "GetShop";
        if(configuration.smsName != null && configuration.smsName.isEmpty()) {
            from = configuration.smsName;
        }
        for(PmsRoomSimple simple : allRooms) {
            for(PmsGuests guest : simple.guest) {
                if(guest.prefix.equals("47") || guest.prefix.equals("+47")) {
                    messageManager.sendSms("sveve", guest.phone, message, guest.prefix);
                } else {
                    messageManager.sendSms("nexmo", guest.phone, message, guest.prefix);
                }
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
    public void sendPaymentLink(String orderId, String bookingId, String email, String prefix, String phone) {
        orderIdToSend = orderId;
        emailToSendTo = email;
        prefixToSend = prefix;
        phoneToSend = phone;
        doNotification("booking_sendpaymentlink", bookingId);
    }

    @Override
    public void failedChargeCard(String orderId, String bookingId) {
        orderIdToSend = orderId;
        doNotification("booking_unabletochargecard", bookingId);
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

    public void dumpBooking(PmsBooking booking) {
        if(checkDate(booking)) {
            if((!booking.payedFor && booking.deleted != null) && (booking.sessionId == null || booking.sessionId.isEmpty())) {
                booking.dump();
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                    logPrint("\t" + type.name + " - " + room.date.start + " frem til : " + room.date.end);
                }
            } 
            User user = userManager.getUserById(booking.userId);
            if(user != null) {
                logPrint(user.fullName); 
            }
            logPrint("-------");
        }
    }

    HashMap<String, PmsBooking> getBookingMap() {
        return bookings;
    }
    
    @Override
    public void addAddonsToBooking(Integer type, String roomId, boolean remove) {
        boolean foundRoom = true;
        PmsBooking booking = getBookingFromRoomSecure(roomId);
        if(booking == null) {
            foundRoom = false;
            booking = getBooking(roomId);
        }
        checkSecurity(booking);
        PmsBookingAddonItem addonConfig = configuration.addonConfiguration.get(type);
        
        List<String> roomIds = new ArrayList();
        if(!foundRoom) {
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


            List<PmsBookingAddonItem> result = createAddonForTimePeriodeWithDiscount(type, room, booking);
            for(PmsBookingAddonItem toReturn : result) {
                if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST || addonConfig.dependsOnGuestCount) {
                    toReturn.count = room.numberOfGuests;
                }
            }
            
            room.addons.addAll(result);
            setAddonPricesOnRoom(room, booking);
            updateRoomPriceFromAddons(room, booking);
        }
        saveBooking(booking);
    }

    public PmsBookingAddonItem createAddonToAdd(PmsBookingAddonItem addonConfig, Date date) {
        
        Product product = productManager.getProduct(addonConfig.productId);
            PmsBookingAddonItem toReturn = new PmsBookingAddonItem();
            toReturn.addonType = addonConfig.addonType;
            if(product != null) {
                toReturn.price = product.price;
                toReturn.priceExTaxes = product.priceExTaxes;
                toReturn.productId = product.id;
            } else {
                logPrint("Null propduct, product with id : " + (addonConfig.productId) + " has been lost / deleted.");
            }
            toReturn.date = date;
            toReturn.descriptionWeb = addonConfig.descriptionWeb;
            toReturn.isAvailableForBooking = addonConfig.isAvailableForBooking;
            toReturn.isAvailableForCleaner = addonConfig.isAvailableForCleaner;
            toReturn.isActive = addonConfig.isActive;
            toReturn.isIncludedInRoomPrice = addonConfig.isIncludedInRoomPrice;
            if(addonConfig.price > 0) {
                toReturn.price = addonConfig.price;
                toReturn.priceExTaxes = addonConfig.priceExTaxes;
            }
        
        return toReturn;
    }
    
    @Override
    public List<PmsBookingAddonItem> getAddonsWithDiscount(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        PmsPricing prices = getPriceObjectFromBooking(booking);

        List<PmsBookingAddonItem> allAddons = new ArrayList(getConfiguration().addonConfiguration.values());
        List<PmsBookingAddonItem> result = new ArrayList();
        for(PmsBookingAddonItem item : allAddons) {
            List<PmsBookingAddonItem> addons = createAddonForTimePeriodeWithDiscount(item.addonType, room, booking);
            PmsBookingAddonItem addon = createAddonToAdd(item, room.date.start);
            addon.count = item.count;

            Double price = 0.0;
            Integer count = 0;
            for(PmsBookingAddonItem tmp : addons) {
                double tmpPrice = tmp.price;
                if(prices.productPrices.containsKey(item.productId)) {
                    tmpPrice = prices.productPrices.get(item.productId);
                }
                price += tmpPrice;
                count += tmp.count;
            }
            if(count > 0) {
                addon.price = price / count;
            }
            result.add(addon);
        }
        return result;
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

    private void setPriceOnRoom(PmsBookingRooms room, boolean avgPrice, PmsBooking booking) {
        room.price = pmsInvoiceManager.calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end, avgPrice, booking);
        if(getConfigurationSecure().usePriceMatrixOnOrder) {
            room.price = pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
            if(room.price.isNaN() || room.price.isInfinite()) {
                room.price = 0.0;
            }
        }
        room.taxes = pmsInvoiceManager.calculateTaxes(room.bookingItemTypeId);
        setAddonPricesOnRoom(room, booking);
        updateRoomPriceFromAddons(room, booking);
    }

    private void createUserForBooking(PmsBooking booking) {
        if(getSession() != null && getSession().currentUser != null) {
            booking.bookedByUserId = getSession().currentUser.id;
        }
        
        if(getSession() != null && getSession().currentUser != null && getSession().currentUser.isCustomer()) {
            booking.userId = getSession().currentUser.id;
        }
        
        if (booking.userId == null || booking.userId.isEmpty()) {
            User newuser = null;
            Company curcompany = createCompany(booking);
            if (curcompany != null) {
                List<User> existingUsers = userManager.getUsersThatHasCompany(curcompany.id);
                if(!existingUsers.isEmpty()) {
                    newuser = existingUsers.get(0);
                } else {
                    newuser = createUser(booking);
                    curcompany = userManager.saveCompany(curcompany);
                    newuser.company.add(curcompany.id);
                }
                newuser.fullName = curcompany.name;
                newuser.emailAddress = curcompany.email;
                newuser.cellPhone = curcompany.phone;
                newuser.prefix = curcompany.prefix;
                newuser.address = curcompany.address;
                newuser.isCompanyOwner = true;

                userManager.saveUserSecure(newuser);
                booking.userId = newuser.id;
            } else {
                newuser = createUser(booking);
                booking.userId = newuser.id;
            }
        } else {
            booking.registrationData.resultAdded = new LinkedHashMap();
        }
        
        PmsUserDiscount disc = pmsInvoiceManager.getDiscountsForUser(booking.userId);
        if(disc != null && disc.supportInvoiceAfter) {
            booking.createOrderAfterStay = true;
        }
    }

    @Override
    public void splitBooking(List<String> roomIds) {
        PmsBooking booking = getBookingFromRoom(roomIds.get(0));
        if(booking.getActiveRooms().size() == 1) {
            return;
        }
        PmsBooking copy = booking.copy();
        List<PmsBookingRooms> roomsToSplit = new ArrayList();
        for(String roomId : roomIds) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.pmsBookingRoomId.equals(roomId)) {
                    roomsToSplit.add(room);
                    break;
                }
            }
        }
        
        if(roomsToSplit.isEmpty()) {
            return;
        }
        
        User user = userManager.getUserById(booking.userId);
        Order firstOrder = null;
        if(!booking.orderIds.isEmpty()) {
            firstOrder = orderManager.getOrder(booking.orderIds.get(0));
        }
        List<CartItem> allItemsToMove = pmsInvoiceManager.removeOrderLinesOnOrdersForBooking(booking.id, roomIds);
        
        copy.removeAllRooms();
        copy.addRooms(roomsToSplit);
        copy.id = null;
        copy.orderIds.clear(); 
        copy.rowCreatedDate = new Date();
        
        if(!allItemsToMove.isEmpty()) {
            cartManager.getCart().addCartItems(allItemsToMove);
            orderManager.deleteVirtualOrders(booking.id);
            Order order = orderManager.createOrder(user.address);
            if(firstOrder != null) {
                order.payment = firstOrder.payment;
            }
            orderManager.saveOrder(order);
            copy.orderIds.add(order.id);
        }
        
        booking.removeRooms(roomsToSplit);
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
        Date now = new Date();
        List<PmsBooking> allToRemove = new ArrayList();
       for(PmsBooking booking : bookings.values()) {
            if(booking.isActiveOnDay(now) && booking.userId.equals("fastchecking_user")) {
                continue;
            }
            deleteObject(booking);
            allToRemove.add(booking);
        }
        
       for(PmsBooking remove : allToRemove) {
           for(PmsBookingRooms room : remove.getActiveRooms()) {
                bookingEngine.deleteBooking(room.bookingId);
           }
           bookings.remove(remove.id);
        }
        
        orderManager.deleteAllOrders();
        userManager.deleteAllUsers();
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
                doNotification("room_resendcode", booking, room);
            }
        }
    }

    @Override
    public void updateAdditionalInformationOnRooms(PmsAdditionalItemInformation info) {
        List<PmsAdditionalItemInformation> toRemove = new ArrayList();
        for(PmsAdditionalItemInformation test : addiotionalItemInfo.values()) {
            if(test.itemId.equals(info.itemId)) {
                System.out.println("Match: " + info.itemId);
                toRemove.add(test);
            }
        }
        
        for(PmsAdditionalItemInformation remove : toRemove) {
            deleteObject(remove);
            addiotionalItemInfo.remove(remove.id);
        }
        
        saveObject(info);
        addiotionalItemInfo.put(info.itemId, info);
    }

    @Override
    public HashMap<String, String> getChannelMatrix() {
        HashMap<String, String> res = new HashMap();
        HashMap<String, PmsChannelConfig> getChannels = configuration.getChannels();
        for(String key : getChannels.keySet()) {
            if(getChannels.get(key).channel != null && !getChannels.get(key).channel.isEmpty()) {
                res.put(key, getChannels.get(key).channel);
            }
        }
        for(PmsBooking booking : bookings.values()) {
            if(booking.channel != null && !booking.channel.trim().isEmpty()) {
                res.put(booking.channel, booking.channel);
            }
        }
        
        for(String key : res.keySet()) {
            if(getChannelConfig(key).humanReadableText != null && 
                    !configuration.getChannelConfiguration(key).humanReadableText.isEmpty()) {
                res.put(key, configuration.getChannelConfiguration(key).humanReadableText);
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
                if(item.bookingItemTypeId == null || !item.bookingItemTypeId.equals(type.id)) {
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
                    weekday--;
                    if(weekday == -1) {
                        weekday = 7;
                    }
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
        saveBooking(booking);
    }

    private String createInvoiceAttachment() {
        String invoice = invoiceManager.getBase64EncodedInvoice(orderIdToSend);
        return invoice;
    }
    
    private String createContractAttachment(String bookingId) {
        String contract = "";
        try {
            contract = getContract(bookingId);
            contract = invoiceManager.base64Encode(contract);
            
        }catch(Exception e) {
            
        }
        return contract;
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
        
        GrafanaFeederImpl feeder = new GrafanaFeederImpl();
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
            if(getConfigurationSecure().isArx()) {
                room.addedToArx = false;
            }
            room.date.start = cal.getTime();
            if(room.bookingId != null) {
                updateBooking(room);
            }
       }
        if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
            String[] defaultEndSplitted = configuration.defaultEnd.split(":");
            Integer addHours = getConfigurationSecure().numberOfHoursToExtendLateCheckout;
            int hour = new Integer(defaultEndSplitted[0]);
            if(!remove) {
                hour = hour+addHours;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.end);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            room.date.end = cal.getTime();
            if(getConfigurationSecure().isArx()) {
                room.addedToArx = false;
            }
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

    @Override
    public void createChannel(String channel) {
        getChannelConfig(channel);
    }
    
    private PmsChannelConfig getChannelConfig(String channel) {
        if(!getConfigurationSecure().channelExists(channel)) {
            getConfigurationSecure().getChannelConfiguration(channel);
            saveObject(getConfigurationSecure());
        }
        return getConfigurationSecure().getChannelConfiguration(channel);
    }

    @Override
    public void removeChannel(String channel) {
        getConfigurationSecure().removeChannel(channel);
    }

    @Override
    public PmsBooking getBookingWithOrderId(String orderId) {
        for(PmsBooking booking : bookings.values()) {
            for(String oId : booking.orderIds) {
                if(oId.equals(orderId)) {
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public void mergeBookingsOnOrders() {
        List<PmsBooking> toRemove = new ArrayList();
        List<String> processed = new ArrayList();
        HashSet test = new HashSet();
        
        for(PmsBooking booking : bookings.values()) {
            test.add(booking.id);
            System.out.println("Merging: " + booking.id);
            boolean found = false;
            for(PmsBooking booking1 : bookings.values()) {
                if(test.contains(booking1.id)) {
                    continue;
                }
                for(String orderId : booking1.orderIds) {
                    if(booking.orderIds.contains(orderId)) {
                        booking.rooms.addAll(booking1.getAllRoomsIncInactive());
                        toRemove.add(booking1);
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
               saveBooking(booking);
            }
        }
        for(PmsBooking book : toRemove) {
            bookings.remove(book.id);
            hardDeleteBooking(book, "mergebooking");
        }
    }

    private void doSorting(List<PmsRoomSimple> res, PmsBookingFilter filter) {
        if(filter.sorting == null || filter.sorting.isEmpty()) {
            return;
        }
        
        if(filter.sorting.equals("room")) {
            Collections.sort(res, new Comparator<PmsRoomSimple>(){
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2){
                    return o1.room.compareTo(o2.room);
                }
           });
        }
    }

    private boolean verifyPhoneOnBooking(PmsBooking booking) {
        String countryCode = booking.countryCode;
        if(countryCode == null || countryCode.isEmpty()) {
            countryCode = "NO";
        }
        
        if(booking.userId != null) {
            User user = userManager.getUserById(booking.userId);
            if(user == null) {
                return false;
            }
            String prefix = user.prefix;
            
            HashMap<String, String> res = SmsHandlerAbstract.validatePhone("+"+ prefix,user.cellPhone, countryCode);
            if(res != null) {
                prefix = res.get("prefix");
                String phone = res.get("phone");
                if(prefix != null && phone != null) {
                    boolean save = false;
                    if(!prefix.equals(user.prefix)) { user.prefix = prefix;save=true; }
                    if(!phone.equals(user.cellPhone)) { user.cellPhone = phone;save=true; }
                    if(save) { userManager.saveUserSecure(user); }
                }
            } else {
                //Warn about wrong phone number.
            }
        }
        
        boolean save = false;
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            for(PmsGuests guest : room.guests) {
                HashMap<String, String> res = SmsHandlerAbstract.validatePhone("+"+ guest.prefix,guest.phone, countryCode);
                if(res != null) {
                    String prefix = res.get("prefix");
                    String phone = res.get("phone");
                    if(prefix != null && phone != null) {
                        if(!prefix.equals(guest.prefix)) { guest.prefix = prefix;save=true; }
                        if(!phone.equals(guest.phone)) { guest.phone = phone;save=true; }
                    }
                } else {
                    //Warn about wrong phone number.
                }
            }
        }
        return save;
    }

    @Override
    public void checkIfGuestHasArrived() throws Exception {
        if(!this.getConfigurationSecure().hasLockSystem()) {
            return;
        }
        if(getConfigurationSecure().isArx()) {
            long end = System.currentTimeMillis();
            long start = end - (1000*60*10);
            HashMap<String, List<AccessLog>> doors = doorManager.getLogForAllDoor(start,end);
            for(List<AccessLog> log : doors.values()) {
                for(AccessLog l : log) {
                    if(l.card != null && !l.card.isEmpty()) {
                        markAsArrived(l.card);
                    }
                }
            }
        }
        if(getConfigurationSecure().isGetShopHotelLock()) {
            long end = System.currentTimeMillis();
            long start = end - (1000*60*10);
            HashMap<String, List<AccessLog>> doors = doorManager.getLogForAllDoor(start, end);
            List<BookingItem> items = bookingEngine.getBookingItems();
            for(String deviceId : doors.keySet()) {
                List<AccessLog> log = doors.get(deviceId);
                for(AccessLog l : log) {
                    
                    BookingItem item = null;
                    for(BookingItem tmpItem : items) {
                        if(tmpItem.bookingItemAlias != null && tmpItem.bookingItemAlias.equals(deviceId)) {
                            item = tmpItem;
                        }
                    }
                    
                    if(item == null) {
                        continue;
                    }
                    
                    for(PmsBooking booking : bookings.values()) {
                        for(PmsBookingRooms room : booking.rooms) {
                            if(!room.isStarted() || room.isEnded() || room.checkedin) {
                                continue;
                            }
                            if(room.bookingItemId == null || !room.bookingItemId.equals(item.id)) {
                                continue;
                            }

                            room.checkedin = true;
                            saveBooking(booking);
                        }
                    }
                }
            }
        }
    }

    private void markAsArrived(String card) {
        for(PmsBooking booking : bookings.values()) {
            for(PmsBookingRooms room : booking.rooms) {
                if(!room.isStarted() || room.isEnded()) {
                    continue;
                }
                if(room.code != null && room.code.equals(card) && !room.checkedin) {
                    room.checkedin = true;
                    saveBooking(booking);
                }
            }
        }
    }

    public PmsBooking getBookingFromRoomSecure(String pmsBookingRoomId) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                    return booking;
                }
            }
        }
        return null;
    }

    @Override
    public void logEntryObject(PmsLog log) {
        if(log.logText == null || log.logText.trim().isEmpty()) {
            return;
        }
        String userId = "";
        if (getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
        }
        if(log.bookingItemId != null) {
            BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
            if (item != null) {
                log.bookingItemType = item.bookingItemTypeId;
            }
        }
        log.userId = userId;
        logentries.add(log);
        saveObject(log);
        
        
        if(log.tag != null && log.tag.equals("mobileapp")) {
            List<String> emailsToNotify = configuration.emailsToNotify.get("applogentry");
            if(emailsToNotify != null) {
                for(String email : emailsToNotify) {
                    String text = "";
                    text += "<br/>Store email: " + getStoreEmailAddress();
                    text += "<br/>Store name: " + getStoreName();
                    text += "<br/>Store default address: " + getStoreDefaultAddress();
                    text += "<br/>Entry added:<br>"+log.logText;
                    
                    messageManager.sendMailWithDefaults(email, email, "App log entry added", text);
                }
            }
        }

    }

    @Override
    public void tryAddToEngine(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        addBookingToBookingEngine(booking, room);
        saveBooking(booking);
    }

    @Override
    public void sendStatistics() throws Exception {
        User user = userManager.getInternalApiUser();
        String webAddress = storePool.getStore(storeId).getDefaultWebAddress();
        
        PmsMailStatistics mailer = new PmsMailStatistics(webAddress, user.username, user.metaData.get("password"), null, "");
        Thread t = new Thread(mailer, "My Thread");
        t.start();
    }

    public void checkForRoomsToClose() {
        if(!getConfigurationSecure().automaticallyCloseRoomIfDirtySameDay) {
            return;
        }
        Integer closeHour = getConfigurationSecure().closeRoomNotCleanedAtHour;
        Date start = new Date();
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        if(hourOfDay < closeHour || hourOfDay > 20) {
            return;
        }
        
        cal.setTime(start);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();
        
        
        
        List<BookingItem> items = bookingEngine.getAllAvailbleItems(start, end);
        
        List<PmsAdditionalItemInformation> additionals = getAllAdditionalInformationOnRooms();
        HashMap<String, PmsAdditionalItemInformation> additionalMap = new HashMap();
        for(PmsAdditionalItemInformation t : additionals) {
            additionalMap.put(t.itemId, t);
        }
        
        for(BookingItem item : items) {
            PmsAdditionalItemInformation additional = additionalMap.get(item.id);
            if(!additional.isClean()) {
                System.out.println("We need to close down room: " + item.bookingItemName);
                closeItem(item.id, start, end, "cleaning");
            }
        }
    }

    public boolean closeItem(String id, Date start, Date end, String source) {
        BookingItem item = bookingEngine.getBookingItem(id);
        
        Booking booking = new Booking();
        booking.source = source;
        booking.startDate = start;
        booking.endDate = end;
        booking.bookingItemId = id;
        booking.bookingItemTypeId = item.bookingItemTypeId;
        
        List<Booking> toAdd = new ArrayList();
        toAdd.add(booking);
        if(!bookingEngine.canAdd(booking)) {
            return false;
        }
        
        bookingEngine.addBookings(toAdd);
        return true;
    }

    @Override
    public String generateNewCodeForRoom(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.findRoom(roomId);
        room.code = generateCode();
        room.addedToArx = false;
        if(room.isStarted() && !room.isEnded()) {
            forceMarkRoomAsCleaned(room.bookingItemId);
        }
        saveBooking(booking);
        processor();
        return room.code;
    }

    public List<PmsBookingAddonItem> getAddonsForRoom(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);
        for(PmsBookingAddonItem item : room.addons) {
            if(item.productId != null && !item.productId.isEmpty()) {
                Product product = productManager.getProduct(item.productId);
                product.updateTranslation(getSession().language);
                item.name = product.name;
            }
        }
        return room.addons;
    }

    
    @Override
    public void addProductToRoom(String productId, String pmsRoomId, Integer count) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.findRoom(pmsRoomId);
        if(count == 0) {
            List<PmsBookingAddonItem> toRemove = new ArrayList();
            for(PmsBookingAddonItem item : room.addons) {
                if(item.productId.equals(productId)) {
                    toRemove.add(item);
                }
            }
            if(!toRemove.isEmpty()) {
                room.addons.removeAll(toRemove);
            }
        } else {
            PmsBookingAddonItem item = new PmsBookingAddonItem();
            for(PmsBookingAddonItem test : getConfigurationSecure().addonConfiguration.values()) {
                if(test.productId != null && test.productId.equals(productId)) {
                    item = test;
                    break;
                }
            }
            
            List<PmsBookingAddonItem> addons = createAddonForTimePeriode(item.addonType, room.date.start, room.date.end, booking.priceType);
            for(PmsBookingAddonItem addon : addons) {
                addon.count = count;
            }
            room.addons.addAll(addons);
        }
        saveBooking(booking);
    }
    
    public void addAddonsToBookingWithCount(Integer type, String pmsBookingRoomId, boolean b, int count) {
        addAddonsToBooking(type, pmsBookingRoomId, b);
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            if(room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                for(PmsBookingAddonItem item : room.addons) {
                    if(item.addonType.equals(type)) {
                        item.count = count;
                    }
                }
            }
        }
        saveBooking(booking);
    }

    @Override
    public List<PmsBookingAddonItem> getAddonsAvailable() {
        HashMap<Integer, PmsBookingAddonItem> addons = getConfigurationSecure().addonConfiguration;
        List<PmsBookingAddonItem> result = new ArrayList();
        for(PmsBookingAddonItem item : addons.values()) {
            if(item.productId != null && !item.productId.isEmpty()) {
                Product product = productManager.getProduct(item.productId);
                product.updateTranslation(getSession().language);
                item.name = product.name;
                result.add(item);
            }
        }
        return result;
    }

    public PmsBooking doCompleteBooking(PmsBooking booking) {
        if(getConfigurationSecure().notifyGetShopAboutCriticalTransactions) {
            messageManager.sendErrorNotification("Booking completed.", null);
        }
        if(booking.getActiveRooms().isEmpty()) {
            return null;
        }
        notifyAdmin("booking_completed_" + booking.language, booking);
        if (!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }
        
        try {
            checkForMissingEndDate(booking);

            Integer result = 0;
            booking.isDeleted = false;

            List<Booking> bookingsToAdd = buildRoomsToAddToEngineList(booking);
            Coupon coupon = getCouponCode(booking);
            if(coupon != null) {
                cartManager.subtractTimesLeft(coupon.code);
            }
            createUserForBooking(booking);
            if (configuration.payAfterBookingCompleted && canAdd(bookingsToAdd) && !booking.createOrderAfterStay) {
                booking.priceType = getPriceObjectFromBooking(booking).defaultPriceType;
                pmsInvoiceManager.createPrePaymentOrder(booking);
            }
            
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
                return booking;
            }
        } catch (Exception e) {
            messageManager.sendErrorNotification("This should never happen and need to be investigated : Unknown booking exception occured for booking id: " + booking.id, e);
            e.printStackTrace();
        }
        return null; 
    }

    @Override
    public List<SimpleInventory> getSimpleInventoryList(String roomName) {
        List<BookingItem> items = bookingEngine.getBookingItems();
        String itemId = null;
        for(BookingItem item : items) {
            if(item.bookingItemName.equals(roomName)) {
                itemId = item.id;
            }
        }
        List<SimpleInventory> res = new ArrayList();
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        for(PmsInventory inv : additional.inventory.values()) {
            SimpleInventory simpleInventory = new SimpleInventory();
            simpleInventory.name = productManager.getProduct(inv.productId).name;
            simpleInventory.productId = inv.productId;
            simpleInventory.originalCount = inv.count;
            res.add(simpleInventory);
        }
        return res;
    }

    @Override
    public void reportMissingInventory(List<SimpleInventory> inventories, String itemId, String roomId) {
        for(SimpleInventory simple : inventories) {
            if(getConfigurationSecure().autoAddMissingItemsToRoom && roomId != null) {
                addProductToRoom(simple.productId, roomId, 0);
                if(simple.missingCount > 0) {
                    if(!hasCaretakerTask(itemId, simple.productId)) {
                        addProductToRoom(simple.productId, roomId, simple.missingCount);
                        PmsCareTaker newEntry = new PmsCareTaker();
                        newEntry.description = "Missing inventory";
                        newEntry.inventoryProductId = simple.productId;
                        newEntry.roomId = itemId;
                        saveCareTakerJob(newEntry);
                    }
                } else {
                    removeCaretakerMissingInventory(itemId, simple.productId);
                }
            }
        }
    }

    @Override
    public void removeAddonFromRoomById(String addonId, String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.findRoom(roomId);
        PmsBookingAddonItem toRemove = null;
        for(PmsBookingAddonItem item : room.addons) {
            if(item.addonId.equals(addonId)) {
                toRemove = item;
            }
        }
        if(toRemove != null) {
            room.addons.remove(toRemove);
        }
        saveBooking(booking);
    }

    @Override
    public void saveCareTakerJob(PmsCareTaker job) {
        if(job.id == null || job.id.isEmpty()) {
            List<String> emails = getConfigurationSecure().emailsToNotify.get("caretaker");
            if(emails != null) {
                finalizeCareTakerJob(job);
                for(String email : emails) {
                    String msg = "Task: " + job.description;
                    if(job.productName != null) {
                        msg += ", " + job.productName;
                    }
                    if(job.roomName != null) {
                        msg += ", " + job.roomName;
                    }
                    messageManager.sendMail(email, email, "Caretaker task added", msg, email, email);
                }
            }
        }
        
        saveObject(job);
        careTaker.put(job.id, job);
    }

    @Override
    public void removeCareTakerJob(String jobId) {
        PmsCareTaker object = careTaker.get(jobId);
        deleteObject(object);
    }

    @Override
    public List<PmsCareTaker> getCareTakerJobs() {
        List<PmsCareTaker> res = new ArrayList(careTaker.values());
        for(PmsCareTaker taker : res) {
            finalizeCareTakerJob(taker);
        }
        return res;
    }

    @Override
    public PmsCareTaker getCareTakerJob(String id) {
        return careTaker.get(id);
    }

    private void removeCaretakerMissingInventory(String itemId, String productId) {
        if(itemId == null) {
            return;
        }
        
        List<PmsCareTaker> toRemove = new ArrayList();
        for(PmsCareTaker taker : careTaker.values()) {
            if(taker.dateCompleted != null) {
                continue;
            }
            if(taker.inventoryProductId.equals(productId) && taker.roomId.equals(itemId)) {
                toRemove.add(taker);
            }
        }
        
        for(PmsCareTaker remove : toRemove) {
            deleteObject(remove);
            careTaker.remove(remove.id);
        }
    }

    @Override
    public void completeCareTakerJob(String id) {
        PmsCareTaker job = getCareTakerJob(id);
        job.dateCompleted = new Date();
        saveObject(job);
    }

    private void finalizeCareTakerJob(PmsCareTaker taker) {
        if(taker.inventoryProductId != null) {
            taker.productName = productManager.getProduct(taker.inventoryProductId).name;
        }
        if(taker.roomId != null) {
            taker.roomName = bookingEngine.getBookingItem(taker.roomId).bookingItemName;
        }
        if(taker.dateCompleted != null) {
            taker.completed = true;
        }
    }

    private boolean hasCaretakerTask(String itemId, String productId) {
        if(itemId == null || productId == null) {
            return false;
        }
        for(PmsCareTaker taker : careTaker.values()) {
            if(taker.dateCompleted == null && taker.inventoryProductId.equals(productId) && taker.roomId.equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sendConfirmation(String email, String bookingId) {
        emailToSendTo = email;
        doNotification("booking_completed", bookingId);
    }

    @Override
    public List<RoomCleanedInformation> getAllRoomsNeedCleaningToday() {
        RoomCleanedInformation cleaning = new RoomCleanedInformation();
        List<RoomCleanedInformation> result = new LinkedList();
        List<PmsAdditionalItemInformation> allRooms = getAllAdditionalInformationOnRooms();
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "checkout";
        filter.startDate = new Date();
        filter.endDate = new Date();
        
        List<PmsRoomSimple> checkoutRooms = getSimpleRooms(filter);
        List<PmsBookingRooms> intervalCleanings = getRoomsNeedingIntervalCleaning(new Date());
        
        for(PmsAdditionalItemInformation room : allRooms) {
            RoomCleanedInformation info = new RoomCleanedInformation();
            info.roomId = room.itemId;
            
            boolean checkoutToday = false;
            boolean needInterval = false;
            
            for(PmsRoomSimple simple : checkoutRooms) {
                if(simple.bookingItemId != null && simple.bookingItemId.equals(room.itemId)) {
                    checkoutToday = true;
                }
            }
            for(PmsBookingRooms tmproom : intervalCleanings) {
                if(tmproom.bookingItemId.equals(room.itemId)) {
                    needInterval = true;
                }
            }
            
            if(room.inUse && !checkoutToday) {
                info.cleaningState = RoomCleanedInformation.CleaningState.inUse;
            } else if(room.isClean() || room.isUsedToday()) {
                info.cleaningState = RoomCleanedInformation.CleaningState.isClean;
            } else {
                info.cleaningState = RoomCleanedInformation.CleaningState.needCleaning;
            }
            if(needInterval && !room.isClean(true)) {
                info.cleaningState = RoomCleanedInformation.CleaningState.needIntervalCleaning;
            }
            result.add(info);
        }
        
        return result;
    }

    @Override
    public List<PmsRoomSimple> getMyRooms() {
        PmsBookingSimpleFilter filter = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        String userId = getSession().currentUser.id;
        List<PmsRoomSimple> result = new ArrayList();
        for(PmsBooking booking : bookings.values()) {
            if(booking.userId.equals(userId)) {
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(room.isDeleted() || booking.isDeleted) {
                        continue;
                    }
                    PmsRoomSimple res = filter.convertRoom(room, booking);
                    result.add(res);
                }
            }
        }
        return result;
    }

    @Override
    public void endRoomWithDate(String pmsRoomId, Date date) {
        List<PmsRoomSimple> rooms = getMyRooms();
        boolean found = false;
        for(PmsRoomSimple r : rooms) {
            if(r.pmsRoomId.equals(pmsRoomId)) {
                found = true;
            }
        }
        if(found) {
            PmsBooking booking = getBookingFromRoom(pmsRoomId);
            for(PmsBookingRooms r : booking.rooms) {
                if(r.pmsBookingRoomId.equals(pmsRoomId)) {
                    r.requestedEndDate = date;
                }
            }
            saveBooking(booking);
        }
    }

    
    
    private List<PmsBookingAddonItem> createAddonForTimePeriodeWithDiscount(Integer type, PmsBookingRooms room, PmsBooking booking) {
        List<PmsBookingAddonItem> result = createAddonForTimePeriode(type, room.date.start, room.date.end, booking.priceType);
        
        for(PmsBookingAddonItem item : result) {
            if(cartManager.couponIsValid(booking.couponCode, item.date, item.date, item.productId)) {
                item.price = cartManager.calculatePriceForCouponWithoutSubstract(booking.couponCode, item.price);
            }
            
        }
        
        return result;
    }
    
    private List<PmsBookingAddonItem> createAddonForTimePeriode(Integer type, Date start, Date end, Integer priceType) {
        List<PmsBookingAddonItem> result = new ArrayList();
        PmsBookingAddonItem addonConfig = configuration.addonConfiguration.get(type);
        if(addonConfig.isSingle) {
            if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start);
                result.add(toAdd);
            } else if(addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, end);
                result.add(toAdd);
            } else {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start);
                result.add(toAdd);
            }
        } else {
            start = pmsInvoiceManager.normalizeDate(start, true);
            if(end != null) {
                end = pmsInvoiceManager.normalizeDate(end, false);
                while(true) {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start);
                    result.add(toAdd);
                    start = addTimeUnit(start, priceType);
                    if(start.after(end)) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void endStay(String itemId) {
        for(PmsBooking booking : bookings.values()) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                if(room.bookingItemId != null && room.bookingItemId.equals(itemId)) {
                    if(room.isEndingToday() && !room.isEnded()) {
                        changeDatesOnRoom(room, room.date.start, new Date());
                        logEntry("Cleaning program is ending the stay.", booking.id, itemId, room.pmsBookingRoomId);
                    }
                }
            }
        }
    }

    private void changeDatesOnRoom(PmsBookingRooms room, Date start, Date end) {
        PmsBooking booking = getBookingFromRoom(room.pmsBookingRoomId);
        Date now = new Date();
        if(!room.isStartingToday() && room.isStarted() && (!room.isEnded() || room.isEndingToday())
                && (start.before(now) && end.after(now))) {
            //This is extending a stay, we need to remove cleaning and mark it as cleaned.
            forceMarkRoomAsCleaned(room.bookingItemId);
        }
        if(room.bookingId != null && !room.bookingId.isEmpty()) {
            bookingEngine.changeDatesOnBooking(room.bookingId, start, end);
        }
        room.date.start = start;
        room.date.end = end;
        room.date.exitCleaningDate = null;
        room.date.cleaningDate = null;
        if(room.addedToArx) {
            if(room.isStarted() && !room.isEnded()) {
                forceMarkRoomAsCleaned(room.bookingItemId);
                room.addedToArx = false;
                if(getConfigurationSecure().isGetShopHotelLock() && !room.isEnded()) {
                    room.addedToArx = true;
                }
            }
        }

        if(configuration.updatePriceWhenChangingDates) {
            setPriceOnRoom(room, true, booking);
        }
        pmsInvoiceManager.updateAddonsByDates(room);
        saveBooking(booking);
    }

    HashMap<String, PmsPricing> getAllPrices() {
        return priceMap;
    }
    
    @Override
    public List<String> getpriceCodes() {
        HashMap<String, PmsPricing> allPrices = priceMap;
        List<String> codes = new ArrayList(allPrices.keySet());
        return codes;
    }

    @Override
    public void createNewPricePlan(String code) {
        HashMap<String, PmsPricing> current = priceMap;
        if(current.containsKey(code)) {
            return;
        }
        
        PmsPricing price = new PmsPricing();
        price.code = code;
        priceMap.put(code, price);
        saveObject(price);
    }

    @Override
    public void deletePricePlan(String code) {
        PmsPricing prices = priceMap.get(code);
        priceMap.remove(code);
        deleteObject(prices);
    }

    @Override
    public void updateAddonsBasedOnGuestCount(String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);
        HashMap<Integer, String> types = new HashMap();
        for(PmsBookingAddonItem item : room.addons) {
            types.put(item.addonType, "1");
        }
        
        for(Integer type : types.keySet()) {
            if(type == 1 || getConfiguration().addonConfiguration.get(type).dependsOnGuestCount) {
                addAddonsToBooking(type, pmsRoomId, false);
            }
        }
        
    }

    @Override
    public void resetPriceForRoom(String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);
        room.priceMatrix = new LinkedHashMap();
        pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
    }

    private List<PmsBooking> filterByUnpaid(List<PmsBooking> finalized, PmsBookingFilter filter) {
        if(filter.filterType == null || !filter.filterType.equals("unpaid")) {
            return finalized;
        }
        
        List<PmsBooking> unpaidBookings = new ArrayList();
        
        for(PmsBooking booking : finalized) {
            if(!booking.payedFor) {
                unpaidBookings.add(booking);
            }
        }
        
        return unpaidBookings;
    }

    @Override
    public List<PmsBookingAddonViewItem> getItemsForView(String viewId, Date date) {
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        
        PmsMobileView view = getConfiguration().mobileViews.get(viewId);
        
        startCal.setTime(date);
        endCal.setTime(date);
        
        startCal.add(Calendar.DAY_OF_YEAR, view.daysDisplacement);
        endCal.add(Calendar.DAY_OF_YEAR, view.daysDisplacement);
        
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        
        Date startDate = startCal.getTime();
        Date endDate = endCal.getTime();
        
        List<PmsBookingAddonViewItem> items = new ArrayList();
        
        for(PmsBooking booking : bookings.values()) {
            for(PmsBookingRooms room : booking.getActiveRooms()) {
                for(PmsBookingAddonItem item : room.addons) {
                    if(!view.products.contains(item.productId)) {
                        continue;
                    }
                    boolean toBeAdded = item.date.after(startDate) && item.date.before(endDate);
                    if(view.viewType == PmsMobileView.PmsMobileViewType.ALLACTIVE) {
                        if(room.isActiveOnDay(date)) {
                            toBeAdded = true;
                        }
                    }
                    if(toBeAdded) {
                        if(view.paidFor && !pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId)) {
                            continue;
                        }
                        PmsBookingAddonViewItem toAdd = new PmsBookingAddonViewItem();
                        toAdd.count = item.count;
                        toAdd.addonId = item.addonId;
                        toAdd.productName = productManager.getProduct(item.productId).name;
                        if(!room.guests.isEmpty()) {
                            toAdd.name = room.guests.get(0).name;
                        }
                        
                        toAdd.delivered = 0;
                        for(PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
                            if(entry.addonId.equals(item.addonId)) {
                                toAdd.delivered++;
                            }
                        }
                        
                        BookingItem bookingItem = bookingEngine.getBookingItem(room.bookingItemId);
                        if(bookingItem != null) {
                            toAdd.roomName = bookingItem.bookingItemName;
                        }
                        toAdd.owner = userManager.getUserById(booking.userId).fullName;
                        items.add(toAdd);
                    }
                }
            }
        }
        
        if(view.sortType == PmsMobileView.PmsMobileSortyType.BYOWNER) {
            Collections.sort(items, new Comparator<PmsBookingAddonViewItem>(){
                public int compare(PmsBookingAddonViewItem s1, PmsBookingAddonViewItem s2) {
                    return s1.owner.compareTo(s2.owner);
                }
            });
        }
        if(view.sortType == PmsMobileView.PmsMobileSortyType.BYROOM) {
            Collections.sort(items, new Comparator<PmsBookingAddonViewItem>(){
                public int compare(PmsBookingAddonViewItem s1, PmsBookingAddonViewItem s2) {
                    if(s1.roomName == null || s2.roomName == null) {
                        return 0;
                    }
                    return s1.roomName.compareTo(s2.roomName);
                }
            });
        }
        
        return items;
    }

    PmsBooking getBookingUnfinalized(String bookingId) {
        return bookings.get(bookingId);
    }

    public PmsPricing getPriceObjectFromBooking(PmsBooking booking) {
        String code = "default";
        Coupon coupon = getCouponCode(booking);
        if(coupon != null) {
            code = coupon.priceCode;
        }
        PmsPricing priceplan = getPriceObject(code);
        if(priceplan == null) {
            priceplan = new PmsPricing();
        }
        return priceplan;
    }

    @Override
    public void addCartItemToRoom(CartItem item, String pmsBookingRoomId, String addedBy) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
       
        Product product = item.getProduct();
        
        PmsBookingAddonItem addon = new PmsBookingAddonItem();
        addon.productId = product.id;
        addon.count = item.getCount();
        addon.price = product.price;
        addon.isSingle = true;
        addon.priceExTaxes = product.priceExTaxes;
        addon.variations = product.variationCombinations;
        addon.date = item.getStartingDate();
        addon.addedBy = addedBy;
        if(addon.date == null) {
            addon.date = new Date();
        }
        addon.description = product.description;
        
        room.addons.add(addon);
        saveBooking(booking);
    }

    private void updateRoomPriceFromAddons(PmsBookingRooms room, PmsBooking booking) {
        setAddonPricesOnRoom(room, booking);
        
        for(PmsBookingAddonItem item : room.addons) {
            if(isPackage(item)) {
                Double roomPrice = getRoomPriceFromPackage(item);
                room.price = roomPrice;
                for(String key : room.priceMatrix.keySet()) {
                    room.priceMatrix.put(key, roomPrice);
                }
            }
        }
    }
    
    private void setAddonPricesOnRoom(PmsBookingRooms room, PmsBooking booking) {
        PmsPricing priceplan = getPriceObjectFromBooking(booking);
        for(PmsBookingAddonItem item : room.addons) {
            if(priceplan.productPrices.containsKey(item.productId)) {
                item.price = priceplan.productPrices.get(item.productId);
            }
        }
    }

    private boolean isPackage(PmsBookingAddonItem item) {
        return false;
    }

    private Double getRoomPriceFromPackage(PmsBookingAddonItem item) {
        return 10.0;
    }

    private PmsBookingAddonItem getAddonOriginalItem(PmsBookingAddonItem item) {
        for(PmsBookingAddonItem item2 : getConfiguration().addonConfiguration.values()) {
            if(item2.addonType == item.addonType) {
                return item2;
            }
        }
        return null;
    }

    private void addDefaultAddons(PmsBooking booking) {
        HashMap<Integer, PmsBookingAddonItem> addons = getConfigurationSecure().addonConfiguration;
        for(PmsBookingRooms room : booking.getActiveRooms()) {
            for(PmsBookingAddonItem item : addons.values()) {
                if(room.bookingItemTypeId != null) {
                    if(item.includedInBookingItemTypes.contains(room.bookingItemTypeId)) {
                        addAddonsToBooking(item.addonType, room.pmsBookingRoomId, false);
                    }
                }
            }
        }
    }

    @Override
    public void removeAddonFromRoom(String id, String pmsBookingRooms) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRooms);
        PmsBookingRooms room = booking.getRoom(pmsBookingRooms);
        PmsBookingAddonItem toremove = null;
        for(PmsBookingAddonItem item : room.addons) {
            if(item.addonId.equals(id)) {
                toremove = item;
            }
        }
        room.addons.remove(toremove);
        saveBooking(booking);
    }

    @Override
    public void markAddonDelivered(String id) {
        for(PmsBooking booking : bookings.values()) {
            for(PmsBookingRooms room : booking.rooms) {
                for(PmsBookingAddonItem item : room.addons) {
                    if(item.addonId.equals(id)) {
                        PmsAddonDeliveryLogEntry entry = new PmsAddonDeliveryLogEntry();
                        entry.addonId = item.addonId;
                        entry.pmsBookingRoomId = room.pmsBookingRoomId;
                        entry.price = item.price;
                        entry.priceEx = item.priceExTaxes;
                        entry.productId = item.productId;
                        entry.productName = productManager.getProduct(item.productId).name;
                        entry.owner = userManager.getUserById(booking.userId).fullName;
                        BookingItem bitem = bookingEngine.getBookingItem(room.bookingItemId);
                        entry.roomName = "";
                        if(bitem != null) {
                            entry.roomName = bitem.bookingItemName;
                        }
                        saveObject(entry);
                        deliveredAddons.put(entry.id, entry);
                    }
                }
            }
        }
    }

    @Override
    public void deleteDeliveryLogEntry(String id) {
        PmsAddonDeliveryLogEntry object = deliveredAddons.get(id);
        Calendar cal = Calendar.getInstance();
        cal.setTime(object.rowCreatedDate);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        if(cal.getTime().after(new Date())) {
            return;
        }
        deleteObject(object);
        deliveredAddons.remove(id);
    }

    @Override
    public List<PmsAddonDeliveryLogEntry> getDeliveryLog(List<String> productIds, Date start, Date end) {
        List<PmsAddonDeliveryLogEntry> res = new ArrayList();
        for(PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
            if(productIds.contains(entry.productId)) {
                if(entry.rowCreatedDate.after(start) && entry.rowCreatedDate.before(end)) {
                    res.add(entry);
                }
            }
        }
        return res;
    }

    @Override
    public List<PmsAddonDeliveryLogEntry> getDeliveryLogByView(String viewId, Date start, Date end) {
        PmsMobileView view = getConfiguration().mobileViews.get(viewId);
        return getDeliveryLog(view.products, start, end);
    }

    private void checkConvertLockConfigs(PmsConfiguration pmsConfiguration) {
        PmsLockServer res = pmsConfiguration.lockServerConfigs.get("default");
        if(res == null) {
            pmsConfiguration.convertLockConfigToDefault();
            saveObject(pmsConfiguration);
        }
    }

    @Override
    public List<PmsRoomSimple> getAllRoomsThatHasAddonsOfType(String type) {
        PmsBookingSimpleFilter filter = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        
        List<PmsBooking> bookingsWithResturantAddons = bookings.values().stream()
                .filter(booking -> !booking.payedFor && !booking.isDeleted)
                .filter(booking -> booking.hasAddonOfType(type))
                .collect(Collectors.toList());
        
        bookingsWithResturantAddons.stream().forEach(booking -> finalize(booking));
        
        List<PmsRoomSimple> simpleRooms = new ArrayList();
        for (PmsBooking booking : bookingsWithResturantAddons) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.hasAddonOfType(type)) {
                    simpleRooms.add(filter.convertRoom(room, booking));
                }
            }
        }
    
        return simpleRooms;
    }

    public void removeAddon(String addonId) {
        for (PmsBooking booking : bookings.values()) {
            boolean removed = false;
            for (PmsBookingRooms room : booking.rooms) {
                boolean addonRemoved = room.decreaseAddonAndRemoveIfEmpty(addonId);
                if (addonRemoved) {
                    saveBooking(booking);
                    return;
                }
            }
        }
    }

    public PmsBookingAddonItem getBaseAddon(String productId) {
        for(PmsBookingAddonItem item : getConfigurationSecure().addonConfiguration.values()) {
            if(item != null && item.productId != null && item.productId.equals(productId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public ConferenceData getConferenceData(String bookingId) {
        ConferenceData data = conferenceDatas.values()
                .stream()
                .filter(conf -> conf.bookingId != null && conf.bookingId.equals(bookingId))
                .findFirst()
                .orElse(new ConferenceData());
        
        data.bookingId = bookingId;
                
        finalize(data);
        return data;
    }

    private void finalize(ConferenceData data) {
        PmsBooking booking = getBooking(data.bookingId);
        if (booking != null) {
            data.attendeesCount = booking.getTotalGuestCount();
            data.date = booking.getStartDate();
            if (booking.userId != null && !booking.userId.isEmpty() && userManager.getUserById(booking.bookedByUserId) != null) {
                data.nameOfEvent = userManager.getUserById(booking.userId).fullName;
            }
        }
    }

    @Override
    public void saveConferenceData(ConferenceData data) {
        ConferenceData old = getConferenceData(data.bookingId);
        data.id = old.id;
        saveObject(data);
        conferenceDatas.put(data.id, data);
    }

    @Override
    public List<ConferenceData> getFutureConferenceData() {
        List<ConferenceData> retList = conferenceDatas.values().stream()
                .filter(conference -> conference.days != null && !conference.days.isEmpty())
                .filter(conf -> isInFuture(conf))
                .collect(Collectors.toList());
        
        retList.stream().forEach(conf -> finalize(conf));
        
        return retList;
    }
    
    private boolean isInFuture(ConferenceData data) {
        PmsBooking booking = getBooking(data.bookingId);
        
        return booking != null && (!booking.isEnded() || booking.isActiveOnDay(new Date()));
    }

    private List<PmsBooking> filterByUnsettledAmounts(List<PmsBooking> finalized) {
        List<PmsBooking> result = new ArrayList();
        for(PmsBooking test : finalized) {
            if(!test.payedFor) {
                continue;
            }
            test.calculateTotalCost();
            double total = test.getTotalPrice();
            double orderTotal = 0.0;
            for(String orderId : test.orderIds) {
                Order order = orderManager.getOrderSecure(orderId);
                orderTotal += orderManager.getTotalAmount(order);
            }
            double diff = total - orderTotal;
            if(diff < -3 || diff > 3) {
                test.totalUnsettledAmount = diff;
                result.add(test);
            }
        }
        
        return result;
    }

    public List<String> getListOfAllRoomsThatHasPriceMatrix() {
        List<String> ids = new ArrayList();
        
        for (PmsBooking book : bookings.values()) {
            for (PmsBookingRooms room :  book.rooms) {
                if (room.priceMatrix != null && !room.priceMatrix.isEmpty()) {
                    ids.add(room.pmsBookingRoomId);
                }
            }
        }
        
        return ids;
    }

    @Override
    public void markRoomDirty(String itemId) throws Exception {
        PmsAdditionalItemInformation item = getAdditionalInfo(itemId);
        item.forceMarkDirty();
        saveObject(item);
    }
    
    @Override
    public Map<Long, List<ConferenceData>> getGroupedConferenceData() {
        List<ConferenceData> futureConfData = getFutureConferenceData();
        List<ConferenceData> eachDayConfData = new ArrayList();
        
        futureConfData.stream().forEach(fut -> eachDayConfData.addAll(fut.getForEachDay()));
        eachDayConfData.stream().forEach(fut -> finalize(fut));
        
        Map<Long, List<ConferenceData>> result = eachDayConfData.stream()
            .collect(
                Collectors.groupingBy(e -> e.days.get(0).getParsedDate() != null 
                        ? e.days.get(0).getParsedDate().getTime() 
                        : e.date.getTime(), Collectors.toList())
            );

        
        return result;
    }

    @Override
    public void saveFilter(String name, PmsBookingFilter filter) {
        if(savedFilters.get(name) != null) {
            PmsBookingFilter tmpFilter = savedFilters.get(name);
            filter.id = tmpFilter.id;
        }
        filter.filterName = name;
        saveObject(filter);
        savedFilters.put(name, filter);
    }

    @Override
    public PmsBookingFilter getPmsBookingFilter(String name) {
        return savedFilters.get(name);
    }

    @Override
    public List<PmsBookingFilter> getAllPmsFilters() {
        return new ArrayList(savedFilters.values());
    }

    @Override
    public void deletePmsFilter(String name) {
        PmsBookingFilter object = savedFilters.get(name);
        if(object != null) {
            savedFilters.remove(name);
            deleteObject(object);
        }
    }

    private void convertTextDates(PmsBookingFilter filter) {
        System.out.println("This is converting");
        if(filter.startDate == null && filter.startDateAsText != null) {
            filter.startDate = convertTextDate(filter.startDateAsText);
        }
        if(filter.endDate == null && filter.endDateAsText != null) {
            filter.endDate = convertTextDate(filter.endDateAsText);
        }
    }

    
    @Override
    public Date convertTextDate(String text) {
        Calendar now = Calendar.getInstance();
        switch(text) {
            case "startofyear":
                now.set(Calendar.DAY_OF_YEAR, 1);
                break;
            case "startofmonth":
                now.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case "startofweek":
                now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                now.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "endofyear":
                now.set(Calendar.MONTH, Calendar.DECEMBER); // 11 = december
                now.set(Calendar.DAY_OF_MONTH, 31); // new years eve                
                break;
            case "endofmonth":
                now.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
                break;
            case "endofweek":
                now.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
        }
        switch(text) {
            case "startofyear":
            case "startofmonth":
            case "startofday":
            case "startofweek":
                now.set(Calendar.HOUR_OF_DAY, 00);
                now.set(Calendar.MINUTE, 00);
                now.set(Calendar.SECOND, 00);
                now.set(Calendar.MILLISECOND, 00);
                break;
            case "endofyear":
            case "endofmonth":
            case "endofday":
            case "endofweek":
                now.set(Calendar.HOUR_OF_DAY, 23);
                now.set(Calendar.MINUTE, 59);
                now.set(Calendar.SECOND, 59);
                now.set(Calendar.MILLISECOND, 59);
                break;
        }
        
        return now.getTime();
    }
}