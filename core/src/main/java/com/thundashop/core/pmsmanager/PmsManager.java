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
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.arx.AccessLog;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingTimeLineFlatten;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.BookingTimeLine;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.AddonsInclude;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.BookingEngineException;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FilterOptions;
import com.thundashop.core.common.FilteredData;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GrafanaFeederImpl;
import com.thundashop.core.common.GrafanaManager;
import com.thundashop.core.common.Session;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.GetShop;
import com.thundashop.core.getshoplock.GetShopDeviceLog;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.getshoplocksystem.GetShopLockSystemManager;
import com.thundashop.core.getshoplocksystem.LockCode;
import com.thundashop.core.getshoplocksystem.LockGroup;
import com.thundashop.core.getshoplocksystem.MasterUserSlot;
import com.thundashop.core.gsd.DevicePrintRoomCode;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.messagemanager.SmsHandlerAbstract;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.OrderShipmentLogEntry;
import com.thundashop.core.pdf.InvoiceManager;
import com.thundashop.core.pga.PgaManager;
import com.thundashop.core.pmseventmanager.PmsEventManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.ratemanager.BookingComRateManagerManager;
import com.thundashop.core.webmanager.WebManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.ticket.Ticket;
import com.thundashop.core.ticket.TicketManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserCard;
import com.thundashop.core.utils.BrRegEngine;
import com.thundashop.core.utils.UtilManager;
import com.thundashop.core.wubook.WubookManager;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import java.util.UUID;
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
    private HashMap<String, String> bookingIdMap = new HashMap();
    private HashMap<String, Product> fetchedProducts = new HashMap();
    private HashMap<String, PmsAddonDeliveryLogEntry> deliveredAddons = new HashMap();
    private HashMap<String, PmsCareTaker> careTaker = new HashMap();
    private HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo = new HashMap();
    private HashMap<String, PmsPricing> priceMap = new HashMap();
    private HashMap<String, ConferenceData> conferenceDatas = new HashMap();
    private HashMap<String, FailedWubookInsertion> failedWubooks = new HashMap();
    private HashMap<String, PmsRoomTypeAccessory> accesories = new HashMap();
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
    private boolean warnedAboutAutoassigning = false;

    private HashMap<String, PmsBookingFilter> savedFilters = new HashMap();

    @Autowired
    WubookManager wubookManager;

    @Autowired
    BookingEngine bookingEngine;

    @Autowired
    MessageManager messageManager;

    @Autowired
    UserManager userManager;

    public OrderManager orderManager;

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
    GetShopLockSystemManager getShopLockSystemManager;

    @Autowired
    PmsInvoiceManager pmsInvoiceManager;

    @Autowired
    UtilManager utilManager;

    @Autowired
    GrafanaManager grafanaManager;

    @Autowired
    PmsEventManager pmsEventManager;

    @Autowired
    GetShop getShop;

    @Autowired
    StoreApplicationPool storeApplicationPool;

    @Autowired
    BookingComRateManagerManager bookingComRateManagerManager;

    @Autowired
    BrRegEngine brRegEngine;

    @Autowired
    private TicketManager ticketManager;

    @Autowired
    private SmsHistoryManager smsHistoryManager;

    @Autowired
    WebManager webManager;
    
    @Autowired
    private PgaManager pgaManager;

    @Autowired
    private GdsManager gdsManager;
    
    @Autowired
    Database dataBase;
    private Date virtualOrdersCreated;
    private Date startedDate;

    private PmsBookingAutoIncrement autoIncrement = new PmsBookingAutoIncrement();
    private String messageToSend;
    private boolean tmpFixed = false;
    private String overrideNotificationTitle;
    private List<String> warnedAboutNotAddedToBookingEngine = new ArrayList();
    private boolean convertedDiscountSystem = false;

    @Autowired
    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public HashMap<String, PmsCareTaker> getCareTakerTasks() {
        return careTaker;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        Calendar toCheck = Calendar.getInstance();

        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBooking) {
                PmsBooking booking = (PmsBooking) dataCommon;
                if(booking.nonrefundable) { booking.setAllRoomsNonRefundable(); }
                bookings.put(booking.id, booking);
            }
            if (dataCommon instanceof ConferenceData) {
                ConferenceData conferenceRoomData = (ConferenceData) dataCommon;
                conferenceDatas.put(conferenceRoomData.id, conferenceRoomData);
            }
            if (dataCommon instanceof FailedWubookInsertion) {
                FailedWubookInsertion failure = (FailedWubookInsertion) dataCommon;
                failedWubooks.put(failure.wubookResId, failure);
            }
            if (dataCommon instanceof PmsBookingAutoIncrement) {
                autoIncrement = (PmsBookingAutoIncrement) dataCommon;
            }
            if (dataCommon instanceof PmsRoomTypeAccessory) {
                accesories.put(dataCommon.id, (PmsRoomTypeAccessory) dataCommon);
            }
            if (dataCommon instanceof PmsPricing) {
                PmsPricing price = (PmsPricing) dataCommon;
                if (price.code.isEmpty()) {
                    price.code = "default";
                    saveObject(price);
                }
                if (priceMap.containsKey(price.code)) {
                    for (int i = 0; i < 10; i++) {
                        String code = price.code + "_" + i;
                        if (!priceMap.containsKey(code)) {
                            price.code = code;
                            saveObject(price);
                            break;
                        }
                        break;
                    }
                }
                priceMap.put(price.code, price);
            }
            if (dataCommon instanceof PmsCareTaker) {
                careTaker.put(dataCommon.id, (PmsCareTaker) dataCommon);
            }
            if (dataCommon instanceof PmsBookingFilter) {
                savedFilters.put(((PmsBookingFilter) dataCommon).filterName, (PmsBookingFilter) dataCommon);
            }
            if (dataCommon instanceof PmsConfiguration) {
                checkConvertLockConfigs((PmsConfiguration) dataCommon);
                PmsConfiguration toAdd = (PmsConfiguration) dataCommon;
                toCheck.setTime(toAdd.rowCreatedDate);
                if (toCheck.get(Calendar.YEAR) == 2017 && toCheck.get(Calendar.DAY_OF_YEAR) == 8) {
                    System.out.println("delete this configuration, failure to add today: " + toAdd.rowCreatedDate + "(" + getName() + ")");
                    deleteObject(dataCommon);
                } else if (configuration.rowCreatedDate == null || (configuration.rowCreatedDate.before(dataCommon.rowCreatedDate) || configuration.rowCreatedDate.equals(dataCommon.rowCreatedDate))) {
                    if (configuration.rowCreatedDate != null) {
                        deleteObject(configuration);
                    }
                    configuration = toAdd;
                } else if (configuration.rowCreatedDate != null) {
                    deleteObject(dataCommon);
                }
            }
            if (dataCommon instanceof PmsAddonDeliveryLogEntry) {
                deliveredAddons.put(dataCommon.id, (PmsAddonDeliveryLogEntry) dataCommon);
            }
            if (dataCommon instanceof PmsAdditionalTypeInformation) {
                PmsAdditionalTypeInformation cur = (PmsAdditionalTypeInformation) dataCommon;
                boolean found = false;
                for (PmsAdditionalTypeInformation info : additionDataForTypes) {
                    if (info.typeId.equals(cur.typeId)) {
                        found = true;
                        deleteObject(dataCommon);
                    }
                }
                if (!found) {
                    additionDataForTypes.add(cur);
                }
            }
            if (dataCommon instanceof PmsLog) {
                PmsLog entry = (PmsLog) dataCommon;
                if (entry.logText.contains("Automarking booking as paid for, since no orders has been added") || entry.logText.equals("Booking saved / updated") || entry.logText.contains("booking has been deleted")) {
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
        createScheduler("pmsprocessor3", "7,13,33,53 * * * *", CheckPmsFiveMin.class);
    }

    @Override
    public void createAllVirtualOrders() {

        if (virtualOrdersCreated != null) {
            return;
        }
        createAllVirtualOrdersForPeriode(null, null);
        virtualOrdersCreated = new Date();
    }

    @Override
    public void setSession(Session session) {
        super.setSession(session);
    }

    private PmsPricing getDefaultPriceObject() {
        PmsPricing price = priceMap.get("default");
        if (price != null) {
            return price;
        }
        return new PmsPricing();
    }

    public PmsPricing getPriceObject(String code) {
        PmsPricing object = priceMap.get(code);
        if (object == null) {
            object = priceMap.get("default");
        }
        return object;
    }

    @Override
    public List<PmsBookingRooms> getAllRoomTypes(Date start, Date end) {
        if (numberOfYearsBetween(start, end) > 5) {
            return new ArrayList();
        }
        User loggedon = userManager.getLoggedOnUser();
        boolean isAdmin = false;
        boolean displayExTaxes = false;
        if (loggedon != null) {
            isAdmin = (loggedon.isAdministrator() || loggedon.isEditor());
            displayExTaxes = loggedon.showExTaxes;
        }

        List<PmsBookingRooms> result = new ArrayList();
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

            PmsBookingRooms roomToAdd = new PmsBookingRooms();
            roomToAdd.type = type;

            PmsBookingRooms room = new PmsBookingRooms();
            room.bookingItemTypeId = type.id;
            room.date = new PmsBookingDateRange();
            room.date.start = start;
            room.date.end = end;

            PmsBooking booking = new PmsBooking();
            booking.priceType = PmsBooking.PriceType.daily;

            setPriceOnRoom(room, true, booking);

            if (displayExTaxes) {
                Product product = productManager.getProduct(type.productId);
                room.price = room.price / ((100 + product.taxGroupObject.taxRate) / 100);
                room.priceWithoutDiscount = room.priceWithoutDiscount / ((100 + product.taxGroupObject.taxRate) / 100);
            }

            roomToAdd.price = room.price;
            roomToAdd.priceWithoutDiscount = room.priceWithoutDiscount;
            result.add(roomToAdd);
        }

        return result;
    }

    @Override
    public void setBooking(PmsBooking booking) throws Exception {
        setBookingByAdmin(booking, false);
    }
    
    public void setBookingByAdmin(PmsBooking booking, boolean keepRoomPrices) throws Exception {
        if (booking.couponCode != null && !booking.couponCode.isEmpty()) {
            if (booking.discountType != null && booking.discountType.equals("coupon")) {
                Coupon cop = cartManager.getCoupon(booking.couponCode);
                if (cop == null) {
                    booking.couponCode = "";
                } else if (cop.channel != null && !cop.channel.isEmpty()) {
                    booking.channel = cop.channel;
                }
            }
            PmsUserDiscount userDiscount = pmsInvoiceManager.getUserDiscountByCouponCode(booking.couponCode);
            if(userDiscount != null) {
                if(userManager.doesUserExsist(userDiscount.userId)) {
                    booking.userId = userDiscount.userId;
                }
            }
        }

        if (booking.couponCode != null && booking.couponCode.contains(":")) {
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
            HashMap<String, Double> currentPriceMatrix = new HashMap(room.priceMatrix);
            pmsInvoiceManager.updateAddonsByDates(room);
            pmsInvoiceManager.updatePriceMatrix(booking, room, Integer.SIZE);
            addDiscountAddons(room, booking);
            room.count = totalDays;
            if (room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty() && !room.bookingItemTypeId.equals("waiting_list")) {
                setPriceOnRoom(room, true, booking);
            }
            room.updateBreakfastCount();
            if(keepRoomPrices) {
                for(String day : room.priceMatrix.keySet()) {
                    if(currentPriceMatrix.containsKey(day)) {
                        room.priceMatrix.put(day, currentPriceMatrix.get(day));
                    }
                }
            }

            for (PmsGuests guest : room.guests) {
                if (guest.prefix != null) {
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                    guest.prefix = guest.prefix.replace("+", "");
                }
            }
        }

        if (booking.sessionStartDate == null) {
            booking.sessionStartDate = getConfigurationSecure().getDefaultStart(new Date());
            if (!configuration.hasNoEndDate) {
                booking.sessionEndDate = getConfigurationSecure().getDefaultEnd(new Date(System.currentTimeMillis() + (86400*1000)));
            }
        }

        if (booking.isDeleted && booking.getActiveRooms().size() > 0) {
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

        addPrintablePrice(result);

        result.calculateTotalCost();

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

    private Integer completeBooking(List<Booking> bookingsToAdd, PmsBooking booking, boolean canAdd) throws ErrorException {
        if(canAdd) {
            bookingEngine.addBookings(bookingsToAdd);
            booking.attachBookingItems(bookingsToAdd);
        }
        booking.sessionId = null;
        if (booking.registrationData.resultAdded.get("company_invoicenote") != null) {
            booking.invoiceNote = booking.registrationData.resultAdded.get("company_invoicenote");
        }

        if (!needConfirmation(booking)) {
            booking.confirmed = true;
        }

        User loggedonuser = userManager.getLoggedOnUser();
        if (loggedonuser != null && configuration.autoconfirmRegisteredUsers) {
            booking.confirmed = true;
        }
        if (loggedonuser != null && (loggedonuser.isAdministrator() || loggedonuser.isEditor())) {
            booking.confirmed = true;
        }

        booking.sessionId = "";
        verifyPhoneOnBooking(booking, true);
        booking.deleted = null;
        booking.completedDate = new Date();
        saveBooking(booking);
        feedGrafana(booking);
        logPrint("Booking has been completed: " + booking.id);

        User user = userManager.getUserById(booking.userId);
        user.lastBooked = new Date();
        userManager.saveUserSecure(user);

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

    @Override
    public List<PmsBooking> getAllBookings(PmsBookingFilter filter) {
        gsTiming("start");
        if (filter != null && filter.bookingId != null && !filter.bookingId.isEmpty()) {
            List<PmsBooking> res = new ArrayList();
            res.add(getBooking(filter.bookingId));
            return res;
        }

        if (!getConfigurationSecure().exposeUnsecureBookings) {
            if (getSession() == null || getSession().currentUser == null || getSession().currentUser == null) {
                return new ArrayList();
            }
            //This method is exposed unsecure, an additional check needs to be added here.
            if (!getSession().currentUser.isAdministrator()) {
                if (!getSession().currentUser.id.equals(filter.userId)) {
                    return new ArrayList();
                }
            }
        }

        return getAllBookingsInternal(filter);
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
        if (lastCheckForIncosistent != null) {
            diff = System.currentTimeMillis() - lastCheckForIncosistent.getTime();
        }
        if (diff > (10800 * 1000)) {
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
        if(!convertedDiscountSystem) { 
            cartManager.checkIfNeedsToConvertToNewCouponSystem(bookingEngine.getBookingItemTypes()); convertedDiscountSystem = true; 
        }
        
        PmsBooking booking = bookings.get(bookingId);
        if (booking == null) {
            return null;
        }
        checkSecurity(booking);

        pmsInvoiceManager.validateInvoiceToDateForBooking(booking, new ArrayList());
        booking.calculateTotalCost();
        Double totalOrder = 0.0;
        for (String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            totalOrder += orderManager.getTotalAmount(order);
        }
        Double diff = booking.getTotalPrice() - totalOrder;
        if (diff < 2 && diff > 2) {
            diff = 0.0;
        }
        booking.unsettled = diff;

        for (PmsBookingRooms room : booking.getActiveRooms()) {
            try {
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                if (type != null) {
                    room.maxNumberOfGuests = bookingEngine.getBookingItemType(room.bookingItemTypeId).size;
                }
            } catch (Exception e) {
                logPrintException(e);
            }
        }

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
        for (PmsBooking booking : bookings.values()) {
            if (booking == null || booking.getActiveRooms() == null) {
                continue;
            }
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room == null) {
                    continue;
                }
                if (room.isEndedDaysAgo(7)) {
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
        if (booking == null) {
            return null;
        }
        if(!warnedAboutNotAddedToBookingEngine.contains(booking.id) && booking.hasRoomsNotAddedToBookingEngine() && !booking.isWubook()) {
            String toWarn = "Booking with pms booking id not found in booking engine: " + booking.id + "<br>";
            warnedAboutNotAddedToBookingEngine.add(booking.id);
            if(getConfigurationSecure().bookingProfile != null && getConfiguration().bookingProfile.equals("hotel")) {
                messageManager.sendErrorNotificationToEmail("pal@getshop.com", toWarn, null);
            }
        }
        
        
        Calendar nowCal = Calendar.getInstance();
        if(booking.isTerminalBooking()) {
            nowCal.add(Calendar.MINUTE, -15);
        } else {
            nowCal.add(Calendar.HOUR_OF_DAY, -1);
        }
        
        if (booking.secretBookingId == null || booking.secretBookingId.isEmpty()) {
            booking.secretBookingId = UUID.randomUUID().toString();
            saveObject(booking);
        }

        if ((booking.sessionId != null && !booking.sessionId.isEmpty() && !booking.avoidAutoDelete) || (booking.isTerminalBooking() && !booking.isOld(60))) {
            Date deletionDate = nowCal.getTime();
            boolean hardDelete = (booking.rowCreatedDate.before(deletionDate) && (booking.completedDate == null || booking.completedDate.before(deletionDate)));
            if (hardDelete && !booking.payedFor) {
                hardDeleteBooking(booking, "finalize");
                return null;
            }
            if(hardDelete) { 
                booking.avoidAutoDelete = true; 
                saveObject(booking);
            }
        }

        if (booking.isDeleted) {
            booking.state = 2;
            return booking;
        }
        boolean needSaving = false;

        if (booking.incrementBookingId == null) {
            booking.incrementBookingId = getAutoIncrementBookingId();
            needSaving = true;
        }

        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            room.checkAddons();
            if (room.price.isNaN() || room.price.isInfinite()) {
                room.price = 0.0;
            }
            if (room.priceMatrix != null) {
                for (String offset : room.priceMatrix.keySet()) {
                    Double res = room.priceMatrix.get(offset);
                    if (res != null && res.isInfinite() || res.isNaN()) {
                        room.priceMatrix.put(offset, 0.0);
                    }
                }
            }
        }

        for (PmsBookingRooms room : booking.getActiveRooms()) {
            for (PmsBookingAddonItem item : room.addons) {
                item.finalize();
            }

            if (room.bookingItemTypeId != null
                    && productManager.getProduct(room.bookingItemTypeId) != null
                    && productManager.getProduct(room.bookingItemTypeId).taxGroupObject != null) {
                room.taxes = productManager.getProduct(room.bookingItemTypeId).taxGroupObject.taxRate;
            }
            if (room.code == null || room.code.isEmpty()) {
                room.code = generateCode();
            }
            if (room.date != null) {
                if (room.date.start != null) {
                    room.date.startTimeStamp = room.date.start.getTime();
                }
                if (room.date.end != null) {
                    room.date.endTimeStamp = room.date.end.getTime();
                }
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

        for (String orderId : booking.orderIds) {
            if (!booking.incOrderIds.containsKey(orderId)) {
                if (orderManager.orderExists(orderId)) {
                    Order order = orderManager.getOrderSecure(orderId);
                    booking.incOrderIds.put(orderId, order.incrementOrderId);
                    needSaving = true;
                }
            }
        }

        if (needSaving) {
            saveBooking(booking);
        }

        PmsPricing prices = getPriceObjectFromBooking(booking);
        if (prices != null && prices.defaultPriceType == PmsBooking.PriceType.daily && (configuration.getRequirePayments() || storeManager.isPikStore())) {
            booking.calculateTotalCost();
        }

        if (booking != null) {
            booking.startDate = booking.getStartDate();
            booking.endDate = booking.getEndDate();
        }

        booking.isAddedToEventList = pmsEventManager.isChecked(booking.id);

        booking.makeUniqueIds();
        
        return booking;
    }

    private Integer getAutoIncrementBookingId() {
        autoIncrement.incrementBookingId++;
        saveObject(autoIncrement);
        return autoIncrement.incrementBookingId;
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
            if (booking.source != null && !booking.source.isEmpty()) {
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
        if (bookingId != null) {
            booking = getBooking(bookingId);
        } else {
            booking = getBookingFromRoom(roomId);
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            if (room.isDeleted()) {
                room.bookingItemTypeId = newType;
            } else {
                bookingEngine.changeBookingItemOnBooking(room.bookingId, "");
                bookingEngine.changeTypeOnBooking(room.bookingId, newType);
                room.bookingItemTypeId = newType;
            }
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
            logEntry(logText, bookingId, null, roomId, "changetype");
        } catch (BookingEngineException ex) {
            logPrintException(ex);
            return ex.getMessage();
        }
        return "";

    }

    @Override
    public PmsBookingRooms changeDates(String roomId, String bookingId, Date start, Date end) {
        if (start.after(end)) {
            return null;
        }
        PmsBooking booking = null;
        if(bookingId != null) {
            booking = getBooking(bookingId);
        } else {
            booking = getBookingFromRoom(roomId);
        }
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            Date oldStart = new Date();
            Date oldEnd = new Date();
            oldStart.setTime(room.date.start.getTime());
            oldEnd.setTime(room.date.end.getTime());
            changeDatesOnRoom(room, start, end);

            String logText = "New date set from " + convertToStandardTime(oldStart) + " - " + convertToStandardTime(oldEnd) + " to, " + convertToStandardTime(start) + " - " + convertToStandardTime(end);
            logEntry(logText, bookingId, room.bookingItemId, roomId, "changestay");
            if (!booking.isSameDay(end, oldEnd) || !booking.isSameDay(start, oldStart)) {
                doNotification("date_changed", booking, room);
            }
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

        bookings.put(booking.id, booking);

        try {
            verifyPhoneOnBooking(booking, false);
        } catch (Exception e) {
            logPrintException(e);
        }

        saveObject(booking);
        bookingUpdated(booking.id, "modified", null);
    }

    @Override
    public String setBookingItem(String roomId, String bookingId, String itemId, boolean split) {
        PmsBooking booking = getBookingFromRoom(roomId);
        if (booking == null) {
            return "Booking does not exists";
        }
        Date now = new Date();
        try {
            PmsBookingRooms room = booking.findRoom(roomId);
            if (now.before(room.date.start) || room.isSameDay(room.date.start, now)) {
                split = false;
            }
            return setBookingItemAndDate(roomId, itemId, split, room.date.start, room.date.end);
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    @Override
    public String setBookingItemAndDate(String roomId, String itemId, boolean split, Date start, Date end) {
        if (start.after(end)) {
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

            String from = "none";
            if (room.bookingItemId != null) {
                BookingItem oldItem = bookingEngine.getBookingItem(room.bookingItemId);
                if (oldItem != null) {
                    from = oldItem.bookingItemName + "";
                }
            }

            if (split) {
                room = splitBookingIfNesesary(booking, room);
                start = room.date.start;
                end = room.date.end;
            }
            room.lastBookingChangedItem = new Date();
            checkIfRoomShouldBeUnmarkedDirty(room, booking.id);
            if (room.bookingId != null && !room.bookingId.isEmpty() && !room.deleted && !booking.isDeleted) {
                bookingEngine.changeBookingItemAndDateOnBooking(room.bookingId, itemId, start, end);
                resetBookingItem(room, itemId, booking);
            } else {
                BookingItem item = bookingEngine.getBookingItem(itemId);
                if (item != null) {
                    room.bookingItemId = item.id;
                    room.bookingItemTypeId = item.bookingItemTypeId;
                } else {
                    room.bookingItemId = null;
                }
            }
            if(room.deleted) {
                room.date.start = start;
                room.date.end = end;
                room.bookingId = null;
            }

            finalize(booking);

            String logText = "";
            if (bookingEngine.getBookingItem(itemId) != null) {
                String newName = bookingEngine.getBookingItem(itemId).bookingItemName;
                logText = "Changed room to " + newName + " from " + from;
                bookingUpdated(booking.id, "room_changed", room.pmsBookingRoomId);

                for (String orderId : booking.orderIds) {
                    Order order = orderManager.getOrderSecure(orderId);
                    if (!order.closed && !newName.isEmpty()) {
                        for (CartItem item : order.cart.getItems()) {
                            if (item.getProduct().externalReferenceId != null && item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                                item.getProduct().additionalMetaData = newName;
                            }
                        }
                    }
                }
            } else {
                logText = "Unassigned room from " + from;
            }

            logEntry(logText, booking.id, null, roomId, "changestayitem");
            if (room.isStarted() && !room.isEnded()) {
                doNotification("room_changed", booking, room);
            }
            
            pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
            List<PmsBookingRooms> list = new ArrayList();
            list.add(room);
            addDefaultAddonsToRooms(list);
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
        prices.derivedPricesChildren = newPrices.derivedPricesChildren;
        prices.productPrices = newPrices.productPrices;
        prices.longTermDeal = newPrices.longTermDeal;
        prices.coveragePrices = newPrices.coveragePrices;
        prices.coverageType = newPrices.coverageType;
        if (newPrices.code != null && !newPrices.code.isEmpty()) {
            if (priceMap.containsKey(prices.code)) {
                priceMap.remove(prices.code);
            } else {
                priceMap.remove("default");
            }
            prices.code = newPrices.code;
            priceMap.put(prices.code, prices);
        }

        for (String typeId : newPrices.dailyPrices.keySet()) {
            HashMap<String, Double> priceMap = newPrices.dailyPrices.get(typeId);
            for (String date : priceMap.keySet()) {
                HashMap<String, Double> existingPriceRange = prices.dailyPrices.get(typeId);
                if (existingPriceRange == null) {
                    existingPriceRange = new HashMap();
                    prices.dailyPrices.put(typeId, existingPriceRange);
                }
                Double price = priceMap.get(date);
                if (price == -999999.0) {
                    if (existingPriceRange.containsKey(date)) {
                        existingPriceRange.remove(date);
                    }
                } else {
                    existingPriceRange.put(date, priceMap.get(date));
                }
            }
        }
        saveObject(prices);

        logEntry("Prices updated", null, null);

        return prices;
    }

    @Override
    public String createOrder(String bookingId, NewOrderFilter filter) {
        if (configuration.autoCreateInvoices && !filter.autoGeneration) {
            filter.maxAutoCreateDate = filter.endInvoiceAt;
            filter.autoGeneration = true;
            filter.increaseUnits = configuration.increaseUnits;
            filter.prepayment = configuration.getPrepayment();
        }
        filter.fromAdministrator = true;

        if (filter.endInvoiceAt == null) {
            PmsBooking booking = getBooking(bookingId);
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (filter.endInvoiceAt == null || filter.endInvoiceAt.before(room.date.end)) {
                    filter.endInvoiceAt = room.date.end;
                }
                for (PmsBookingAddonItem item : room.addons) {
                    if (filter.endInvoiceAt == null || filter.endInvoiceAt.before(item.date)) {
                        filter.endInvoiceAt = item.date;
                    }
                }
            }

            for (String orderId : booking.orderIds) {
                Order order = orderManager.getOrderSecure(orderId);
                Date orderEnd = order.getEndDateByItems();
                if (orderEnd != null && (filter.endInvoiceAt == null || filter.endInvoiceAt.before(orderEnd))) {
                    filter.endInvoiceAt = orderEnd;
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
        if (configuration.rowCreatedDate != null && (notifications.id == null || !notifications.id.equals(configuration.id))) {
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

    public void doNotificationFromProcessor(String key, PmsBooking booking, PmsBookingRooms room) {
        phoneToSend = null;
        emailToSendTo = null;
        prefixToSend = null;
        doNotification(key, booking, room);
    }
    
    public void doNotification(String key, PmsBooking booking, PmsBookingRooms room) {
        repicientList.clear();
        String addNotificationSent = key;
        try {
            feedGrafanaNotificationDone(key);
        } catch (Exception e) {
            logPrintException(e);
        }

        String message = notify(key, booking, "sms", room, booking.language);
        List<String> smsRecp = repicientList;
        String repssms = "";
        for (String rep : smsRecp) {
            repssms += rep + ", ";
        }
        repicientList.clear();

        String message2 = notify(key, booking, "email", room, booking.language);
        notifyAdmin(key, booking, booking.language);

        specifiedMessage = "";
        List<String> emailRecp = repicientList;

        String repemail = "";
        for (String rep : emailRecp) {
            repemail += rep + ", ";
        }

        String roomId = null;
        if (room != null) {
            roomId = room.pmsBookingRoomId;
        }
        boolean logged = false;
        if (message != null && !message.isEmpty()) {
            logEntry(message + " recipients: " + repssms, booking.id, null, roomId, key + "_sms");
            logged = true;
        }
        if (message2 != null && !message2.isEmpty()) {
            logEntry(message2 + " recipients: " + repemail, booking.id, null, roomId, key + "_email");
            logged = true;
        }
        emailToSendTo = null;
        booking.notificationsSent.add(addNotificationSent);
        
        if(!logged && key != null && (key.contains("booking_completed") || key.contains("sendreciept") || key.contains("sendinvoice") || key.contains("room_added_to_arx"))) {
            logEntry("Message not sent since it has not been configured properly yet.", booking.id, null, roomId, key);
        }
        
    }

    public String getMessage(String bookingId, String key) {
        orderIdToSend = null;

        PmsBooking booking = getBooking(bookingId);
        String message = getMessageToSend(key, "sms", booking, booking.language);
        message = formatMessage(message, booking, null, null);
        return message;
    }

    private String notify(String key, PmsBooking booking, String type, PmsBookingRooms room, String language) {
        if (booking != null && booking.silentNotification) {
            return "Not notified, silent booking: " + type;
        }

        if (key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing")) {
            room = getRoomFromOrderToSend();
        }

        String message = getMessageToSend(key, type, booking, language);
        message = formatMessage(message, booking, room, null);

        if (message == null || message.trim().isEmpty()) {
            return "";
        }

        if (room != null) {
            notifyGuest(booking, message, type, key, room, language);
        } else {
            notifyBooker(booking, message, type, key, language);
        }
        return message;
    }

    private void notifyBooker(PmsBooking booking, String message, String type, String key, String lang) throws ErrorException {
        User user = userManager.getUserById(booking.userId);

        Order order = null;
        if (key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing")) {
            order = orderManager.getOrderSecure(orderIdToSend);
            order.sentToCustomerDate = new Date();
        }

        if (type.equals("sms")) {
            String phone = user.cellPhone;
            String prefix = user.prefix;
            if (phoneToSend != null) {
                phone = phoneToSend;
                prefix = prefixToSend;
                phoneToSend = null;
            }

            if (order != null) {
                order.sentToPhone = phone;
                order.sentToPhonePrefix = prefix;
                
                OrderShipmentLogEntry e = new OrderShipmentLogEntry();
                e.type = key;
                e.date = new Date();
                e.address = prefix + "-" + phone;
                order.shipmentLog.add(e);
            }

            if (prefix != null && (prefix.equals("47") || prefix.equals("+47"))) {
                messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
            } else {
                messageManager.sendSms("nexmo", phone, message, prefix, configuration.smsName);
            }
            repicientList.add(phone);
        } else {
            String title = getMessageTitle(key, lang);
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
            boolean specificEmail = false;
            if (emailToSendTo != null) {
                recipientEmail = emailToSendTo;
                specificEmail = true;
                emailToSendTo = null;
            }

            if (key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing")) {
                if (order != null) {
                    order.recieptEmail = recipientEmail;
                    order.sentToEmail = recipientEmail;
                    
                    OrderShipmentLogEntry e = new OrderShipmentLogEntry();
                    e.type = key;
                    e.date = new Date();
                    e.address = recipientEmail;
                    order.shipmentLog.add(e);

                    orderManager.saveOrder(order);
                }
            }

            messageManager.sendMailWithAttachments(recipientEmail, user.fullName, title, message, fromEmail, fromName, attachments);

            if (booking.registrationData.resultAdded.containsKey("company_email")) {
                String companyEmail = booking.registrationData.resultAdded.get("company_email");
                if (companyEmail != null && companyEmail.contains("@") && !specificEmail && !companyEmail.equals(recipientEmail)) {
                    messageManager.sendMailWithAttachments(companyEmail, user.fullName, title, message, fromEmail, fromName, attachments);
                }
            }

            if (configuration.copyEmailsToOwnerOfStore) {
                String copyadress = storeManager.getMyStore().configuration.emailAdress;
                messageManager.sendMail(copyadress, user.fullName, title, message, fromEmail, fromName);
            }

            repicientList.add(recipientEmail);
        }

        if (order != null) {
            orderManager.saveOrder(order);
        }
    }

    private String notifyGuest(PmsBooking booking, String message, String type, String key, PmsBookingRooms roomToNotify, String lang) {
        Order order = null;
        if (orderIdToSend != null) {
            order = orderManager.getOrderSecure(orderIdToSend);
            order.sentToCustomerDate = new Date();
        }

        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (roomToNotify != null) {
                if (!room.pmsBookingRoomId.equals(roomToNotify.pmsBookingRoomId)) {
                    continue;
                }
            }

            if (room.guests == null || room.guests.isEmpty() || !bookingEngine.getConfig().rules.includeGuestData) {
                if (type.equals("email")) {
                    String email = userManager.getUserById(booking.userId).emailAddress;
                    if (emailToSendTo != null) {
                        email = emailToSendTo;
                        emailToSendTo = null;
                    }

                    if (key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing")) {
                        if (order != null) {
                            order.recieptEmail = email;
                            order.sentToEmail = email;
                            orderManager.saveOrder(order);
                        }
                    }

                    String name = userManager.getUserById(booking.userId).fullName;
                    String title = getMessageTitle(key, lang);
                    title = formatMessage(title, booking, room, null);
                    messageManager.sendMail(email, name, title, message, getFromEmail(), getFromName());
                    repicientList.add(email);
                } else {
                    String phone = userManager.getUserById(booking.userId).cellPhone;
                    String prefix = userManager.getUserById(booking.userId).prefix;

                    if (phoneToSend != null) {
                        phone = phoneToSend;
                        prefix = prefixToSend;
                        phoneToSend = null;
                    }

                    if (prefix != null && (prefix.equals("47") || prefix.equals("+47"))) {
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
                        if (emailToSendTo != null && !emailToSendTo.isEmpty()) {
                            email = emailToSendTo;
                            emailToSendTo = null;
                        }
                        if (email == null || email.isEmpty()) {
                            logEntry("Email not sent due to no email set for guest " + guest.name, booking.id, null);
                            continue;
                        }
                        String title = configuration.emailTitles.get(key);
                        if (title == null) {
                            title = "";
                        }
                        if (title.isEmpty() && key.startsWith("room_resendcode")) {
                            title = "Code for your room";
                        }

                        title = formatMessage(title, booking, room, guest);
                        if (email != null && email.contains("@") && message != null && !message.trim().isEmpty()) {
                            messageManager.sendMail(email, guest.name, title, message, getFromEmail(), getFromName());
                            repicientList.add(email);

                            if (key.startsWith("booking_sendpaymentlink") || key.startsWith("booking_paymentmissing")) {
                                if (order != null) {
                                    order.recieptEmail = email;
                                }
                            }

                        }
                    } else {
                        String phone = guest.phone;
                        String prefix = guest.prefix;

                        if (phoneToSend != null) {
                            phone = phoneToSend;
                            prefix = prefixToSend;
                            phoneToSend = null;
                        }

                        if (phone == null || phone.isEmpty()) {
                            logEntry("Sms not sent due to no phone number set for guest " + guest.name, booking.id, null);
                            continue;
                        }

                        if (prefix != null && (prefix.equals("47") || prefix.equals("+47"))) {
                            messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
                        } else {
                            messageManager.sendSms("nexmo", phone, message, prefix, configuration.smsName);
                        }
                        repicientList.add(phone);
                    }
                }
            }
        }
        if (order != null) {
            orderManager.saveOrder(order);
        }
        return message;
    }

    private void notifyAdmin(String key, PmsBooking booking, String langauge) {
        String message = configuration.adminmessages.get(key + "_" + langauge);
        if (message == null) {
            for(String k : configuration.adminmessages.keySet()) {
                if(k.startsWith(key)) {
                    message = configuration.adminmessages.get(k);
                    break;
                }
            }
        }
        if (message == null) {
            return;
        }

        message = formatMessage(message, booking, null, null);
        String email = storeManager.getMyStore().configuration.emailAdress;
        String phone = storeManager.getMyStore().configuration.phoneNumber;

        if (!configuration.sendAdminTo.isEmpty()) {
            email = configuration.sendAdminTo;
        }
        logEntry("Notified admin :" + message, booking.id, null);
        messageManager.sendMail(email, "Administrator", "Notification", message, getFromEmail(), getFromName());
        messageManager.sendSms("sveve", phone, message, "47");
    }

    private String formatMessage(String message, PmsBooking booking, PmsBookingRooms room, PmsGuests guest) {
        if (message != null) {
            message = message.trim();
        }
        PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter();
        formater.setProductManager(productManager);
        formater.setConfig(getConfigurationSecure());

        if (this.specifiedMessage != null && message != null) {
            String specifiedmsg = this.specifiedMessage.replace("\n", "<br>\n");
            message = message.replace("{personalMessage}", specifiedmsg);
        }

        if (room != null) {
            message = formater.formatRoomData(message, room, bookingEngine);
            try {
                PmsAdditionalItemInformation addinfo = getAdditionalInfo(room.bookingItemId);
                message = message.replace("{roomDescription}", addinfo.textMessageDescription);
            } catch (Exception e) {
            }
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
        if (hasNoBookings()) {
            return new PmsStatistics();
        }
        if (!filter.includeNonBookable && filter.typeFilter.isEmpty()) {
            List<BookingItemType> types = bookingEngine.getBookingItemTypes();
            for (BookingItemType type : types) {
                if (type.visibleForBooking) {
                    filter.typeFilter.add(type.id);
                }
            }
        }

        convertTextDates(filter);
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        filter.startDate = cal.getTime();
        int startYear = cal.get(Calendar.YEAR);

        cal.setTime(filter.endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        filter.endDate = cal.getTime();

        filter.filterType = "active";
        List<PmsBooking> allBookings = getAllBookings(filter);
        gsTiming("After get all bookings");
        PmsPricing prices = getDefaultPriceObject();
        PmsStatisticsBuilder builder = new PmsStatisticsBuilder(allBookings,
                prices.pricesExTaxes,
                userManager,
                new ArrayList(bookings.values()),
                addiotionalItemInfo,
                orderManager,
                bookingEngine);
        builder.setBudget(getConfigurationSecure().budget);

        int totalRooms = getTotalRoomsBasedOnFilter(filter);

        PmsStatistics result = builder.buildStatistics(filter, totalRooms, pmsInvoiceManager, bookingEngine.getAllBookings());
        gsTiming("After after build statistics");
        if (storeId.equals("123865ea-3232-4b3b-9136-7df23cf896c6") || filter.includeOrderStatistics) {
            result.salesEntries = builder.buildOrderStatistics(filter, orderManager);
        }
        if (getConfigurationSecure().getUsePriceMatrixOnOrder() && (storeId.equals("75e5a890-1465-4a4a-a90a-f1b59415d841") || storeId.equals("fcaa6625-17da-447e-b73f-5c07b9b7d382") || startYear >= 2018)) {
            setTotalFromIncomeReport(result, filter);
        }
        gsTiming("After after setting income report");
        result.setView(filter);
        result.buildTotal();
        result.buildTotalSales();

        HashMap<String, PmsDeliverLogStats> deliveryStats = new HashMap();
        for (PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
            if (filter.startDate.before(entry.rowCreatedDate) && filter.endDate.after(entry.rowCreatedDate)) {
                PmsDeliverLogStats res = new PmsDeliverLogStats();
                if (deliveryStats.containsKey(entry.productId)) {
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

    private int getTotalRoomsBasedOnFilter(PmsBookingFilter filter) {
        int totalRooms = bookingEngine.getBookingItems().size();
        if (!filter.typeFilter.isEmpty()) {
            totalRooms = 0;
            for (String id : filter.typeFilter) {
                totalRooms += bookingEngine.getBookingItemsByType(id).size();
            }
        }
        if (filter.itemFilter != null && !filter.itemFilter.isEmpty()) {
            totalRooms = filter.itemFilter.size();
        }
        return totalRooms;
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
                room.addedToWaitingList = false;
                if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                    roomName = bookingEngine.getBookingItem(room.bookingItemId).bookingItemName + " (" + convertToStandardTime(room.date.start) + " - " + convertToStandardTime(room.date.end) + ")";
                } else if (room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
                    roomName = bookingEngine.getBookingItemType(room.bookingItemTypeId).name + " (" + convertToStandardTime(room.date.start) + " - " + convertToStandardTime(room.date.end) + ")";
                }
                toRemove.add(room);
            }
        }
        for (PmsBookingRooms remove : toRemove) {
            if (!remove.isDeleted() && !remove.isOverBooking()) {
                bookingEngine.deleteBooking(remove.bookingId);
                remove.delete();
                logEntry(roomName + " removed from booking ", bookingId, null);
            } else {
                try {
                    Booking tmpbook = createBooking(remove);
                    List<Booking> toAdd = new ArrayList();
                    toAdd.add(tmpbook);
                    if(bookingEngine.canAdd(toAdd)) {
                        bookingEngine.addBookings(toAdd);
                        remove.undelete();
                        booking.isDeleted = false;
                        remove.unmarkOverBooking();
                        remove.credited = false;
                        remove.setBooking(tmpbook);
                        remove.deleted = false;
                        if (remove.priceMatrix.keySet().isEmpty()) {
                            setPriceOnRoom(remove, true, booking);
                        }
                        logEntry(roomName + " readded to booking ", bookingId, null);
                    } else {
                        logEntry(roomName + " tried readding booking but not possible ", bookingId, null);
                    }
                } catch (BookingEngineException ex) {
                    addResult = ex.getMessage();
                }
            }
        }

        saveObject(booking);

        if (booking.getActiveRooms().isEmpty()) {
            deleteBooking(booking.id);
        }

        bookingUpdated(bookingId, "room_removed", roomId);

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
            for(String k : configuration.contracts.keySet()) {
                contract = configuration.contracts.get(k);
                if(contract != null && contract.isEmpty()) {
                    break;
                }
            }
        }
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

        boolean deletedByChannel = false;
        boolean askedToDoUpdate = false;
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            if (booking.channel != null && booking.channel.contains("wubook_")) {
                askedToDoUpdate = wubookManager.forceUpdateOnAvailability(room);
                deletedByChannel = true;
            }
        }

        if (deletedByChannel) {
            if (!askedToDoUpdate) {
                logPrint("A booking from wubook has been deleted; " + booking.id);
            }
        }

        logEntry("Deleted booking", bookingId, null);
        saveBooking(booking);
    }

    private void hardDeleteBooking(PmsBooking booking, String source) {
        logPrint("Deleting, source: " + source + " id: " + booking.id);
        bookings.remove(booking.id);
        booking.deletedBySource = source;
        
        
        for(PmsBookingRooms r : booking.rooms) {
            if(r.bookingId != null && !r.bookingId.isEmpty()) {
                //Also delete bookings from booking engine.
                bookingEngine.deleteBooking(r.bookingId);
                r.bookingId = null;
            }
        }

        for (String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            order.bookingHasBeenDeleted = true;
            orderManager.saveOrder(order);
        }

        if ((booking.sessionId == null || booking.sessionId.isEmpty()) && !booking.isTerminalBooking()) {
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
                if (booking.isDeleted && !booking.hasOverBooking()) {
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
        
        Date defaultStart = getConfigurationSecure().getDefaultStart(new Date());
        Date defaultEnd = getConfigurationSecure().getDefaultEnd(new Date());
        
        Calendar defaultStartCal = Calendar.getInstance();
        defaultStartCal.setTime(defaultStart);
        Calendar defaultEndCal = Calendar.getInstance();
        defaultEndCal.setTime(defaultEnd);
        
        long diffInMillis = (defaultEnd.getTime() - defaultStart.getTime());
        int diff = (int)(diffInMillis / 1000);
        
        Calendar toAdjust = Calendar.getInstance();
        toAdjust.setTime(filter.start);
        toAdjust.set(Calendar.HOUR_OF_DAY, defaultStartCal.get(Calendar.HOUR_OF_DAY));
        toAdjust.set(Calendar.MINUTE, defaultStartCal.get(Calendar.MINUTE));
        toAdjust.set(Calendar.SECOND, 0);
        toAdjust.set(Calendar.MILLISECOND, 0);
        toAdjust.add(Calendar.SECOND, 1);
        filter.start = toAdjust.getTime();
        
        toAdjust.setTime(filter.end);
        toAdjust.set(Calendar.HOUR_OF_DAY, defaultEndCal.get(Calendar.HOUR_OF_DAY));
        toAdjust.set(Calendar.MINUTE, defaultEndCal.get(Calendar.MINUTE));
        toAdjust.set(Calendar.SECOND, 0);
        toAdjust.set(Calendar.MILLISECOND, 0);
        filter.end = toAdjust.getTime();
        
        List<String> types = new ArrayList();
        if (filter.selectedDefinedFilter != null && !filter.selectedDefinedFilter.isEmpty()) {
            PmsBookingFilter bookingfilter = getPmsBookingFilter(filter.selectedDefinedFilter);
            types = bookingfilter.typeFilter;
        }
        gsTiming("Checking types");
        for (BookingItemType type : bookingEngine.getBookingItemTypes()) {
            if (!types.isEmpty() && !types.contains(type.id)) {
                continue;
            }
            BookingTimeLineFlatten line = bookingEngine.getTimelines(type.id, filter.start, filter.end);
            res.typeTimeLines.put(type.id, line.getTimelines(filter.interval - (int)diff, (int)diff));
        }
        gsTiming("Checking types 2");

        List<BookingItem> items = bookingEngine.getBookingItems();
        gsTiming("Checking types 3");

        List<BookingTimeLineFlatten> lines = bookingEngine.getTimeLinesForItemWithOptimalIngoreErrors(filter.start, filter.end);
        gsTiming("Checking types 4");

        for (BookingItem item : items) {
            if (!types.isEmpty() && !types.contains(item.bookingItemTypeId)) {
                continue;
            }

            BookingTimeLineFlatten line = lines.stream()
                    .filter(li -> li.bookingItemId != null && li.bookingItemId.equals(item.id))
                    .findFirst()
                    .orElse(null);

            gsTiming("Before looping");
            LinkedHashMap<Long, IntervalResultEntry> retLines = getTimeLine(line, filter);
            res.itemTimeLines.put(item.id, retLines);
        }
        
        List<BookingTimeLineFlatten> overflowedLines = lines.stream()
                .filter(l -> l.overFlow)
                .collect(Collectors.toList());
        
        int j = 0;
        for (BookingTimeLineFlatten line : overflowedLines) {
            j++;
            LinkedHashMap<Long, IntervalResultEntry> retLines = getTimeLine(line, filter);
            res.overFlowLines.add(retLines);
        }
        
        return res;
    }

    private LinkedHashMap<Long, IntervalResultEntry> getTimeLine(BookingTimeLineFlatten line, PmsIntervalFilter filter) throws ErrorException {
        List<BookingTimeLine> timelines = line.getTimelines(filter.interval - 21600, 21600);
        LinkedHashMap<Long, IntervalResultEntry> itemCountLine = new LinkedHashMap();
        for (BookingTimeLine tl : timelines) {
            IntervalResultEntry tmpres = new IntervalResultEntry();
            tmpres.bookingIds = tl.bookingIds;
            tmpres.count = tl.count;
            tmpres.time = tl.start.getTime();
            tmpres.typeId = line.getBookingItemTypeId();
            
            addVirtuallyAssignedBookingIds(tmpres, tl);
            
            if (!tmpres.bookingIds.isEmpty()) {
                Booking bookingEngineBooking = bookingEngine.getBooking(tmpres.bookingIds.get(0));
                tmpres.state = bookingEngineBooking.getSourceState();
                
                if (bookingEngineBooking.source != null && !bookingEngineBooking.source.isEmpty()) {
                    tmpres.name = bookingEngineBooking.source;
                } else {
                    PmsBooking booking = getBookingFromBookingEngineId(tmpres.bookingIds.get(0));
                    if (booking != null && booking.userId != null) {
                        User user = userManager.getUserById(booking.userId);
                        if (user != null) {
                            tmpres.name = user.fullName;
                        }
                        
                        for (PmsBookingRooms room : booking.rooms) {
                            if (bookingEngineBooking.id.equals(room.bookingId)) {
                                tmpres.roomId = room.pmsBookingRoomId;
                            }
                        }
                    }
                }
            }
            
            itemCountLine.put(tl.start.getTime(), tmpres);
        }
        return itemCountLine;
    }

    private void addVirtuallyAssignedBookingIds(IntervalResultEntry tmpres, BookingTimeLine tl) {
        tmpres.virtuallyAssigned = tl.bookingIds.stream()
                .map(id -> bookingEngine.getBooking(id))
                .filter(booking -> booking.bookingItemId == null || booking.bookingItemId.isEmpty())
                .map(booking -> booking.id)
                .collect(Collectors.toList());
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
        String cleanersName = "";
        if (getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
            cleanersName = getSession().currentUser.fullName;
        }
        additional.markCleaned(userId);
        saveAdditionalInfo(additional);

        List<Booking> allBookings = bookingEngine.getAllBookingsByBookingItem(itemId);
        List<Booking> bookingsToDelete = new ArrayList();
        for (Booking book : allBookings) {
            if (book.source != null && book.source.equals("cleaning")) {
                if (book.bookingItemId.equals(itemId)) {
                    bookingsToDelete.add(book);
                }
            }
        }
        for (Booking remove : bookingsToDelete) {
            bookingEngine.deleteBooking(remove.id);
            wubookManager.setAvailabilityChanged(remove.startDate, remove.endDate);
        }

        changeCheckoutTimeForGuestOnRoom(itemId);
        PmsRoomSimple currentBookerOnRoom = getCurrentRoomOnItem(itemId);
        if (currentBookerOnRoom != null) {
            logEntry("Cleaned by " + cleanersName, currentBookerOnRoom.bookingId, itemId, currentBookerOnRoom.pmsRoomId, "cleaning");
        } else {
            logEntry("Cleaned by " + cleanersName, null, itemId, null, "cleaning");
        }
    }

    @Override
    public void markRoomAsCleaned(String itemId) {
        markRoomCleanInternal(itemId, true);
    }

    @Override
    public void markRoomAsCleanedWithoutLogging(String itemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        additional.markCleanedWithoutLogging();
        saveAdditionalInfo(additional);
    }

    void markRoomAsDirty(String bookingItemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(bookingItemId);
        PmsRoomSimple currentBookerOnRoom = getCurrentRoomOnItem(bookingItemId);
        if (currentBookerOnRoom != null) {
            additional.markDirty(currentBookerOnRoom.pmsRoomId);
        } else {
            additional.markDirty(null);
        }
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

        if (additionalInfo.inUse) {
            BookingTimeLineFlatten timeline = bookingEngine.getTimeLinesForItem(start.getTime(), end.getTime(), additionalInfo.itemId);
            for (Booking book : timeline.getBookings()) {
                if (book.source != null && book.source.equals("cleaning")) {
                    additionalInfo.inUseByCleaning = true;
                    break;
                }
            }
            for (Booking book : timeline.getBookings()) {
                additionalInfo.closed = false;
                additionalInfo.closedByCleaningProgram = false;
                if (book.source != null && book.source.toLowerCase().contains("closed")) {
                    additionalInfo.closed = true;
                    break;
                }
                if (book.source != null && book.source.toLowerCase().contains("cleaning")) {
                    additionalInfo.closedByCleaningProgram = true;
                    break;
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

        result = sortRooms(result);

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
            if (!booking.isActiveOnDay(day)) {
                continue;
            }
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (needIntervalCleaning(room, day)) {
                    finalize(booking);
                    rooms.add(room);
                }
            }
        }
        rooms = sortRooms(rooms);

        return rooms;
    }

    public boolean needIntervalCleaning(PmsBookingRooms room, Date day) {
        if (room.date.cleaningDate == null) {
            room.date.cleaningDate = room.date.start;
        }

        if (getAdditionalInfo(room.bookingItemId).hideFromCleaningProgram) {
            return false;
        }

        if (room.isEndingToday(day) && !getConfiguration().autoExtend) {
            return false;
        }

        if (room.isStartingToday(day) && !getConfiguration().autoExtend) {
            return false;
        }
        if (!room.isActiveOnDay(day)) {
            if (getConfiguration().autoExtend && room.isEndingToday(day)) {
                //This is atleast a safe bet.
            } else {
                return false;
            }
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
        user.relationship = result.get("user_relationship");
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
        if (user.relationship == null) {
            user.relationship = "";
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
        if (vat != null) {
            vat = vat.trim();
            List<Company> companies = userManager.getCompaniesByVatNumber(vat);
            if (!companies.isEmpty()) {
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
        LinkedList<TimeRepeaterDateRange> lines = createAvailabilityLines(bookingTypeId, TimeRepeaterData.TimePeriodeType.open);
        LinkedList<TimeRepeaterDateRange> closedLines = createAvailabilityLines(bookingTypeId, TimeRepeaterData.TimePeriodeType.closed);

        DateTime timer = new DateTime(startTime);
        List<Integer> result = new ArrayList();
        while (true) {
            if (hasRange(lines, timer, closedLines)) {
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

    private LinkedList<TimeRepeaterDateRange> createAvailabilityLines(String bookingItemId, Integer type) {
        if (bookingItemId != null && bookingItemId.isEmpty()) {
            bookingItemId = null;
        }
        LinkedList<TimeRepeaterDateRange> result = new LinkedList<TimeRepeaterDateRange>();
        List<TimeRepeaterData> repeaters = bookingEngine.getOpeningHoursWithType(bookingItemId, type);
        if (repeaters.isEmpty()) {
            repeaters = bookingEngine.getOpeningHoursWithType(null, type);
        }

        if (type.equals(TimeRepeaterData.TimePeriodeType.closed)) {
            repeaters.addAll(bookingEngine.getOpeningHoursWithType(null, type));
        }

        for (TimeRepeaterData repeater : repeaters) {
            TimeRepeater generator = new TimeRepeater();
            result.addAll(generator.generateRange(repeater));
        }

        return result;
    }

    private boolean hasRange(LinkedList<TimeRepeaterDateRange> lines, DateTime timer, LinkedList<TimeRepeaterDateRange> closed) {
        for (TimeRepeaterDateRange line : closed) {
            if (line.isBetweenTime(timer.toDate())) {
                return false;
            }
        }
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

        String[] defaultTimeStart = getConfiguration().getDefaultStart().split(":");
        String[] defaultEndStart = getConfiguration().getDefaultEnd().split(":");

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

        if (booking.sessionId == null || booking.sessionId.isEmpty()) {
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
        booking.rooms.add(room);
        BookingItemType typeToAdd = bookingEngine.getBookingItemType(type);
        room.bookingItemTypeId = type;
        room.date = new PmsBookingDateRange();
        room.date.start = start;
        room.date.end = end;
        room.guests.add(new PmsGuests());
        setPriceOnRoom(room, true, booking);

        List<PmsBookingRooms> toAdd = new ArrayList();
        toAdd.add(room);

        if (guestInfoFromRoom != null && !guestInfoFromRoom.isEmpty()) {
            PmsBooking bookingforguest = getBookingFromRoom(guestInfoFromRoom);
            PmsBookingRooms roomforGuest = bookingforguest.findRoom(guestInfoFromRoom);
            Gson gson = new Gson();
            String copyroom = gson.toJson(roomforGuest);
            PmsBookingRooms copiedRoom = gson.fromJson(copyroom, PmsBookingRooms.class);
            room.guests = copiedRoom.guests;
        }

        String res = addBookingToBookingEngine(booking, room);
        if (!res.isEmpty()) {
            room.addedToWaitingList = true;
            room.markAsOverbooking();
        }

        addDefaultAddonsToRooms(toAdd);

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
        if (getConfigurationSecure().defaultMessage == null) {
            return "";
        }
        String message = getConfigurationSecure().defaultMessage.get(booking.language);
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
        try {
            if(room.bookingId == null || room.bookingId.isEmpty()) {
                return;
            }
            List<BookingItem> items = bookingEngine.getAvailbleItemsWithBookingConsideredAndShuffling(room.bookingItemTypeId, room.date.start, room.date.end, room.bookingId);

            Collections.sort(items, new Comparator<BookingItem>() {
                public int compare(BookingItem o1, BookingItem o2) {
                    return o1.order.compareTo(o2.order);
                }
            });

            if (!getConfigurationSecure().avoidRandomRoomAssigning) {
                long seed = System.nanoTime();
                Collections.shuffle(items, new Random(seed));
                Collections.shuffle(items, new Random(seed));
            }

            if (items.isEmpty()) {
                if (!warnedAboutAutoassigning) {
                    messageManager.sendErrorNotification("Failed to autoassign room, its critical since someone will not recieve the code for the room now, roomid : " + room.pmsBookingRoomId, null);
                    warnedAboutAutoassigning = true;
                }
                logPrint("....");
                logPrint("No items available?");
            } else {
                BookingItem item = getBestBookingItem(items);
                room.bookingItemId = item.id;
                room.bookingItemTypeId = item.bookingItemTypeId;

                if (room.bookingId != null) {
                    try {
                        bookingEngine.changeBookingItemOnBooking(room.bookingId, item.id);
                    } catch (Exception e) {
                        if (warnedAbout.contains("Itemchangedfailed_" + room.pmsBookingRoomId)) {
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
        } catch (Exception d) {
            if(!warnedAboutAutoassigning) {
                warnedAboutAutoassigning = true;
                messageManager.sendErrorNotification("Exception happened while autoassigning room.", null);
                logPrintException(d);
            }
        }
    }

    @Override
    public void logEntry(String logText, String bookingId, String itemId) {
        logEntry(logText, bookingId, itemId, null, "api");

    }

    private void logEntry(String logText, String bookingId, String itemId, String roomId, String logType) {

        PmsLog log = new PmsLog();
        log.bookingId = bookingId;
        log.bookingItemId = itemId;
        log.roomId = roomId;
        log.logText = logText;
        log.logType = logType;
        logEntryObject(log);
    }

    @Override
    public List<PmsLog> getLogEntries(PmsLog filter) {

        List<PmsLog> res = new ArrayList();
        if (filter != null) {
            for (PmsLog log : logentries) {
                if (filter.tag != null && !filter.tag.isEmpty()) {
                    if (log.tag == null || !log.tag.equals(filter.tag)) {
                        continue;
                    }
                }
                if (!filter.bookingId.isEmpty()) {
                    if (filter.bookingId.equals(log.bookingId)) {
                        res.add(log);
                    }
                } else if (!filter.bookingItemId.isEmpty()) {
                    if (filter.bookingItemId.equals(log.bookingItemId)) {
                        res.add(log);
                    }
                } else if (filter.logType != null && !filter.logType.isEmpty()) {
                    if (filter.logType.equals(log.logType)) {
                        res.add(log);
                    }
                } else if ((!filter.roomId.isEmpty() && filter.roomId.equals(log.roomId)) || log.roomId == null || log.roomId.isEmpty()) {
                    res.add(log);
                } else if(filter.includeAll) {
                    res.add(log);
                }
            }
        } else {
            res = logentries;
        }

        Collections.sort(res, new Comparator<PmsLog>() {
            public int compare(PmsLog o1, PmsLog o2) {
                if (o2.rowCreatedDate != null && o1.rowCreatedDate != null) {
                    return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
                }
                return o2.dateEntry.compareTo(o1.dateEntry);
            }
        });

        if (res.size() > 100) {
            List<PmsLog> newres = new ArrayList();
            int i = 0;
            for (PmsLog test : res) {
                i++;
                newres.add(test);
                if (i > 100 && !filter.includeAll) {
                    break;
                }
            }
            res = newres;
        }

        for (PmsLog log : res) {
            User user = userManager.getUserById(log.userId);
            if (user != null) {
                log.userName = user.fullName;
            }
            if (log.bookingItemId != null && !log.bookingItemId.isEmpty()) {
                BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
                if (item != null) {
                    log.roomName = item.bookingItemName;
                }
            }
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
        if (booking == null) {
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
                    if (!room.isEnded()) {
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
                        if (item != null) {
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

    void warnAboutUnableToAutoExtend(PmsBookingRooms room, BookingItem item, String reason) {
        String bookingItemName = item.bookingItemName;
        Calendar cal = Calendar.getInstance();
        Integer day = cal.get(Calendar.DAY_OF_YEAR);
        String warningString = bookingItemName + "-" + day;
        String copyadress = storeManager.getMyStore().configuration.emailAdress;
//        messageManager.sendMail(copyadress, copyadress, "Unable to autoextend stay for room: " + bookingItemName + "(" + room.date.start + " - " + room.date.end + ")", "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
//        messageManager.sendMail("pal@getshop.com", copyadress, "Unable to autoextend stay for room: " + bookingItemName, "This happends when the room is occupied. Reason: " + reason, copyadress, copyadress);
        warnedAbout.add(warningString);
    }

    boolean needCheckOutCleaning(PmsBookingRooms room, Date toDate) {
        if (getConfigurationSecure().autoExtend && !room.keyIsReturned) {
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

    private List<PmsBookingRooms> sortRooms(List<PmsBookingRooms> result) {
        List<PmsBookingRooms> toSort = new ArrayList();
        List<PmsBookingRooms> notAdded = new ArrayList();
        for (PmsBookingRooms r : result) {
            if (r.item != null) {
                toSort.add(r);
            } else {
                notAdded.add(r);
            }
        }

        Collections.sort(toSort, new Comparator<PmsBookingRooms>() {
            public int compare(PmsBookingRooms o1, PmsBookingRooms o2) {
                return o1.item.bookingItemName.compareToIgnoreCase(o2.item.bookingItemName);
            }
        });

        notAdded.addAll(toSort);
        return notAdded;
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

                if (!room.addedToArx && hasLockSystemActive() && !getConfiguration().markDirtyEvenWhenCodeNotPressed) {
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
                    if (additional.isClean(false) && !additional.closed) {
                        additional.markDirty(room.pmsBookingRoomId);
                        needSaving = true;
                        logEntry("Marking item " + item.bookingItemName + " as dirty (failure in marking)", booking.id, item.id, room.pmsBookingRoomId, "cleaning");
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
        if (interval == 0) {
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
            logEntry("Same day checking move", booking.id, itemId, room.pmsBookingRoomId, "changestay");
            resetDoorLockCode(room);
            PmsAdditionalItemInformation add = getAdditionalInfo(itemId);
            if (!hasLockSystemActive()) {
                add.markDirty(room.pmsBookingRoomId);
            }
            saveObject(add);
            if (room.isStarted() && !room.isEnded()) {
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
            if (room.booking == null && !room.deleted && !room.addedToWaitingList) {
                messageManager.sendErrorNotification("Booking failure for booking: " + booking.id + ", rooms where not reserved in booking engine. address: " + storeManager.getMyStore().webAddress, null);
                return false;
            }
        }

        boolean deleteWhenAdded = getConfigurationSecure().deleteAllWhenAdded;
        if (!deleteWhenAdded) {
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
        return getNumberOfAvailable(itemType, start, end, true, true);
    }

    public Integer getNumberOfAvailable(String itemType, Date start, Date end, boolean includeMinMaxStay, boolean adminOverride) {
        if (start.after(end)) {
            return 0;
        }
        if (hasRestriction(itemType, start, end, adminOverride)) {
            return 0;
        }
        if (includeMinMaxStay) {
            if (isRestricted(itemType, start, end, TimeRepeaterData.TimePeriodeType.min_stay)) {
                return 0;
            }
            if (isRestricted(itemType, start, end, TimeRepeaterData.TimePeriodeType.max_stay)) {
                return 0;
            }
        }

        try {
            return bookingEngine.getNumberOfAvailableWeakButFaster(itemType, start, end);
        } catch (BookingEngineException e) {
            return 0;
        }

    }

    private boolean avoidSameDayDropIn(Date start, String itemType, boolean adminoverride) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, -7);
        Date checkDate = cal.getTime();
        //If booking is done in the midle of the night, the same day is yesterday.
        if (!PmsBookingRooms.isSameDayStatic(checkDate, start)) {
            return false;
        }

        Session sess = getSession();
        if (sess != null && sess.currentUser != null) {
            if (sess.currentUser.isAdministrator() && !sess.currentUser.isProcessUser() && adminoverride) {
                return false;
            }
        }

        List<TimeRepeaterData> openingshours = bookingEngine.getOpeningHoursWithType(itemType, TimeRepeaterData.TimePeriodeType.denySameDayBooking);
        if (openingshours.isEmpty()) {
            openingshours = bookingEngine.getOpeningHoursWithType(null, TimeRepeaterData.TimePeriodeType.denySameDayBooking);
        }

        if (openingshours.isEmpty()) {
            return false;
        }

        TimeRepeater repeater = new TimeRepeater();
        for (TimeRepeaterData res : openingshours) {
            LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
            for (TimeRepeaterDateRange range : ranges) {
                if (range.isBetweenTime(new Date())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isRestricted(String itemType, Date start, Date end, Integer periodeType) {
        int days = pmsInvoiceManager.getNumberOfDays(start, end);
        Session sess = getSession();
        if (sess != null && sess.currentUser != null) {
            if (sess.currentUser.isAdministrator() && !sess.currentUser.isProcessUser()) {
                if (periodeType.equals(TimeRepeaterData.TimePeriodeType.open)) {
                    return true;
                }
                return false;
            }
        }

        List<TimeRepeaterData> openingshours = bookingEngine.getOpeningHoursWithType(itemType, periodeType);
        if (openingshours.isEmpty()) {
            openingshours = bookingEngine.getOpeningHoursWithType(null, periodeType);
        }

        if (periodeType.equals(TimeRepeaterData.TimePeriodeType.open)) {
            return true;
        }

        TimeRepeater repeater = new TimeRepeater();
        boolean isBetween = false;
        for (TimeRepeaterData res : openingshours) {
            LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(res);
            for (TimeRepeaterDateRange range : ranges) {
                if (isBetween) {
                    continue;
                }
                if ((periodeType.equals(TimeRepeaterData.TimePeriodeType.max_stay)
                        || periodeType.equals(TimeRepeaterData.TimePeriodeType.min_stay))) {
                    isBetween = range.isBetweenTime(start);
                } else {
                    isBetween = range.isBetweenTime(start) || range.isBetweenTime(end);
                }

                if (isBetween) {
                    if (periodeType.equals(TimeRepeaterData.TimePeriodeType.min_stay)) {
                        Integer daysInRestrioction = 1;
                        try {
                            daysInRestrioction = new Integer(res.timePeriodeTypeAttribute);
                        } catch (Exception e) {

                        }
                        if (daysInRestrioction > days) {
                            return true;
                        }
                    } else if (periodeType.equals(TimeRepeaterData.TimePeriodeType.max_stay)) {
                        Integer daysInRestrioction = 1;
                        try {
                            daysInRestrioction = new Integer(res.timePeriodeTypeAttribute);
                        } catch (Exception e) {

                        }
                        if (daysInRestrioction < days) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean canAdd(List<Booking> toCheck) {
        for (Booking book : toCheck) {
            if (hasRestriction(book.bookingItemTypeId, book.startDate, book.endDate, true)) {
                return false;
            }
        }
        gsTiming("   has restriction passed");
        boolean anyBookingWithId = toCheck.stream()
                .filter(o -> o.id != null && !o.id.isEmpty())
                .count() > 0;

        if (anyBookingWithId) {
            return bookingEngine.canAdd(toCheck);
        }
        gsTiming("   anybookingwith id passed");

        if (toCheck.isEmpty()) {
            return true;
        }

        HashMap<String, List<BookingItem>> resultsFound = new HashMap();
        
        if (BookingEngine.useNewEngine.contains(storeId)) {
            return bookingEngine.canAdd(toCheck);
        } else {
            for (Booking book : toCheck) {
                String key = book.bookingItemTypeId + "_" + book.startDate.getTime() + "-" + book.endDate.getTime();
                List<BookingItem> items = new ArrayList();
                if(resultsFound.containsKey(key)) {
                    items = resultsFound.get(key);
                } else {
                    items = bookingEngine.getAvailbleItems(book.bookingItemTypeId, book.startDate, book.endDate);
                    resultsFound.put(key, items);
                }
                HashMap<String, Integer> count = getCountedTypes(toCheck, book.startDate, book.endDate);
                if (items.isEmpty() || items.size() < count.get(book.bookingItemTypeId)) {
                    return false;
                }
                gsTiming("   getavailbeitem...");
            }
        }
        
        gsTiming("   getavailbeitems passed");

        return true;
    }

    private String getFromEmail() {
        String fromEmail = storeManager.getMyStore().configuration.emailAdress;
        if (!configuration.senderEmail.isEmpty()) {
            fromEmail = configuration.senderEmail;
        }
        return fromEmail;
    }

    private String getFromName() {
        String fromName = getFromEmail();
        if (!configuration.senderName.isEmpty()) {
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
        bookingUpdated(bookingId, "booking_undeleted", null);
    }

    private List<Booking> buildRoomsToAddToEngineList(PmsBooking booking) {
        List<Booking> bookingsToAdd = new ArrayList();
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            Booking bookingToAdd = createBooking(room);
            gsTiming("createbooking");
            if (getConfigurationSecure().hasNoEndDate && room.date.end == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.date.start);
                cal.add(Calendar.YEAR, 100);
                room.date.end = cal.getTime();
            }
            if (!room.isDeleted() && room.canBeAdded && !room.addedToWaitingList) {
                bookingsToAdd.add(bookingToAdd);
            }
        }

        boolean canAdd = bookingEngine.canAdd(bookingsToAdd);

        if (!canAdd || doAllDeleteWhenAdded()) {
            String text = "";
            for (PmsBookingRooms room : booking.rooms) {
                if (getConfigurationSecure().supportRemoveWhenFull || booking.isWubook() || room.addedToWaitingList) {
                    room.canBeAdded = false;
                    if(!room.isDeleted()) {
                        room.delete();
                        if (booking.isWubook() && !room.deletedByChannelManagerForModification) {
                            room.markAsOverbooking();
                        }
                    }

                    BookingItemType item = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                    String name = "";
                    if (item != null) {
                        name = item.name;
                    }
                    text = "Removed room: " + name + " since it can't be added: " + "<br><br>, channel: " + booking.channel + ", wubook rescode: " + booking.wubookreservationid;
                }
            }

            text += "<br>";
            text += "<br>";
            text += booking.createSummary(bookingEngine.getBookingItemTypes());
            if (booking.userId != null) {
                User user = userManager.getUserById(booking.userId);
                if (user != null) {
                    text += "User : " + userManager.getUserById(booking.userId).fullName;
                }
            }
            logEntry(text, booking.id, null);
            gsTiming("logged entry");

            if (!getConfigurationSecure().supportRemoveWhenFull) {
                boolean hasBeenWarned = false;
                if (booking.wubookreservationid != null && !booking.wubookreservationid.isEmpty()) {
                    if (failedWubooks.containsKey(booking.wubookreservationid)) {
                        hasBeenWarned = true;
                    } else {
                        markSentErrorMessageForWubookId(booking.wubookreservationid);
                    }
                }

                if (!hasBeenWarned && (booking.channel != null && !booking.channel.isEmpty())) {
                    messageManager.sendErrorNotification("Failed to add room, since its full, this should not happend and happends when people are able to complete a booking where its fully booked, " + text + "<br><bR><br>booking dump:<br>" + dumpBooking(booking, false), null);
                }
            }
            gsTiming("removed when full maybe");
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
        if (getSession().currentUser == null) {
            return new ArrayList();
        }
        String userId = getSession().currentUser.id;
        PmsBookingFilter filter = new PmsBookingFilter();
        List<PmsBooking> allBookings = getAllBookings(filter);
        List<PmsBooking> result = new ArrayList();
        for (PmsBooking booking : allBookings) {
            if (booking.userId != null && booking.userId.equals(userId)) {
                result.add(booking);
            }
        }
        return result;
    }

    private void checkSecurity(PmsBooking booking) {
        if (booking == null) {
            logPrint("Nullbooking happened, on checksecurity");
        }

        String secretId = storeManager.currentSecretId;
        if (secretId != null && !secretId.isEmpty() && booking.secretBookingId != null && booking.secretBookingId.equals(secretId)) {
            return;
        }

        User loggedonuser = getSession().currentUser;
        if (booking.sessionId != null && getSession().id.equals(booking.sessionId)) {
            return;
        }
        if (loggedonuser != null && (booking.userId == null || booking.userId.isEmpty())) {
            return;
        }

        if (booking.sessionId != null
                && !booking.sessionId.isEmpty()
                && getSession() != null
                && getSession().id != null
                && booking.sessionId.equals(getSession().id)) {
            return;
        }

        if (loggedonuser == null) {
            throw new ErrorException(26);
        }

        if (loggedonuser.isEditor() || loggedonuser.isAdministrator()) {
            return;
        }

        if (booking.userId.equals(loggedonuser.id)) {
            return;
        }

        throw new ErrorException(26);
    }

    @Override
    public List<Integer> updateRoomByUser(String bookingId, PmsBookingRooms room) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        PmsBookingRooms oldRoom = booking.getRoom(room.pmsBookingRoomId);

        makeSureMinuteAndHourAreTheSame(oldRoom, room);

        List<Integer> errors = new ArrayList();
        if (!oldRoom.date.start.equals(room.date.start)) {
            logPrint("Need to set a new start date");
            if (room.isStarted()) {
                errors.add(1);
                room.date.start = oldRoom.date.start;
            }
        }
        if (!room.date.end.equals(room.date.end)) {
            if (room.isEnded()) {
                errors.add(2);
                room.date.end = oldRoom.date.end;
            }
        }

        if (!oldRoom.date.start.equals(room.date.start) || !oldRoom.date.end.equals(room.date.end)) {
            PmsBookingRooms res = changeDates(room.pmsBookingRoomId, bookingId, room.date.start, room.date.end);
            if (res == null) {
                errors.add(3);
            }
        }

        oldRoom.numberOfGuests = room.numberOfGuests;
        oldRoom.guests = room.guests;

        return errors;
    }

    private void makeSureMinuteAndHourAreTheSame(PmsBookingRooms oldRoom, PmsBookingRooms room) {
        //Make sure end and start is the same hour.
        Calendar oldStartCal = Calendar.getInstance();
        oldStartCal.setTime(oldRoom.date.start);

        Calendar oldEndCal = Calendar.getInstance();
        oldEndCal.setTime(oldRoom.date.end);

        Calendar newStartCal = Calendar.getInstance();
        newStartCal.setTime(room.date.start);

        Calendar newEndCal = Calendar.getInstance();
        newEndCal.setTime(room.date.end);

        newStartCal.set(Calendar.HOUR_OF_DAY, oldStartCal.get(Calendar.HOUR_OF_DAY));
        newStartCal.set(Calendar.MINUTE, oldStartCal.get(Calendar.MINUTE));
        newStartCal.set(Calendar.SECOND, oldStartCal.get(Calendar.SECOND));

        newEndCal.set(Calendar.HOUR_OF_DAY, oldEndCal.get(Calendar.HOUR_OF_DAY));
        newEndCal.set(Calendar.MINUTE, oldEndCal.get(Calendar.MINUTE));
        newEndCal.set(Calendar.SECOND, oldEndCal.get(Calendar.SECOND));

        room.date.start = newStartCal.getTime();
        room.date.end = newEndCal.getTime();
    }

    private Coupon getCouponCode(PmsBooking booking) {
        String couponCode = booking.couponCode;
        if (booking.discountType !=null && booking.discountType.equals("partnership")) {
            if (couponCode.contains(":")) {
                String[] couponCodes = couponCode.split(":");
                if (couponCodes.length > 1) {
                    couponCode = "partnership:" + couponCodes[0];
                }
            }
        }

        return cartManager.getCoupon(couponCode);
    }

    public boolean hasLockSystemActive() {
        if (getConfiguration().hasLockSystem()) {
            return true;
        }

        return getShopLockSystemManager.isActivated();
    }

    @Override
    public List<PmsRoomSimple> getSimpleRooms(PmsBookingFilter filter) {
        if(!convertedDiscountSystem) { 
            cartManager.checkIfNeedsToConvertToNewCouponSystem(bookingEngine.getBookingItemTypes()); convertedDiscountSystem = true; 
        }
        List<PmsRoomSimple> res = new ArrayList();
        gsTiming("filtering 1");
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        gsTiming("filtering 2");
        res = filtering.filterRooms(filter);
        gsTiming("Before sorting");
        doSorting(res, filter);
        gsTiming("After sorting");
        List<PmsRoomSimple> remove = new ArrayList();
        if (filter.includeCleaningInformation) {
            for (PmsRoomSimple r : res) {
                if (r.bookingItemId != null && !r.bookingItemId.isEmpty()) {
                    if (getAdditionalInfo(r.bookingItemId).hideFromCleaningProgram) {
                        remove.add(r);
                    }

                    r.roomCleaned = isClean(r.bookingItemId);
                    r.hasBeenCleaned = (r.roomCleaned || isUsedToday(r.bookingItemId));
                }
            }
        }
        res.removeAll(remove);
        gsTiming("Before removebycustomercodesandaddons");
        res = removeByCustomersCodesAndAddons(res, filter);
        gsTiming("After removebycustomercodesandaddons");

        res = sortDeletedLast(res);
        return res;
    }

    @Override
    public List<PmsRoomSimple> getRoomsNeedingIntervalCleaningSimple(Date day) {
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        List<PmsBookingRooms> rooms = getRoomsNeedingIntervalCleaning(day);
        List<PmsRoomSimple> res = new ArrayList();
        for (PmsBookingRooms r : rooms) {
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
        String newguestinfo = "";
        String newGuestName = "";
        for (PmsGuests guest : guests) {
            if (guest.name != null) {
                newguestinfo += guest.name + " - ";
                if (newGuestName.isEmpty()) {
                    newGuestName = guest.name;
                }
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
            newguestinfo += "<br>";
        }

        logEntry("Changed guest information, new guest information:<br> " + newguestinfo, bookingId, roomId);

        for (String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            if (!order.closed && !newGuestName.isEmpty()) {
                for (CartItem item : order.cart.getItems()) {
                    if (item.getProduct().externalReferenceId != null && item.getProduct().externalReferenceId.equals(room.pmsBookingRoomId)) {
                        item.getProduct().metaData = newGuestName;
                    }
                }
            }
        }

        verifyAddons(room);

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
        if (configuration.smsName != null && configuration.smsName.isEmpty()) {
            from = configuration.smsName;
        }
        for (PmsRoomSimple simple : allRooms) {
            for (PmsGuests guest : simple.guest) {
                if (guest.prefix.equals("47") || guest.prefix.equals("+47")) {
                    messageManager.sendSms("sveve", guest.phone, message, guest.prefix);
                } else {
                    messageManager.sendSms("nexmo", guest.phone, message, guest.prefix);
                }
                logEntry(message + "<br>+" + guest.prefix + " " + guest.phone, storeId, null, simple.pmsRoomId, "sms");
            }
        }
    }

    public PmsConfiguration getConfigurationSecure() {
        String timeZone = storeManager.getMyStore().timeZone;
        configuration.setTimeZone(timeZone);
        configuration.setIsPikStore(storeManager.isPikStore());
        return configuration;
    }

    @Override
    public void markKeyDeliveredForAllEndedRooms() {
        for (PmsBooking booking : bookings.values()) {
            boolean needsaving = false;
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.isEnded() && !room.keyIsReturned) {
                    room.keyIsReturned = true;
                    needsaving = true;
                }
            }
            if (needsaving) {
                saveBooking(booking);
            }
        }
    }

    @Override
    public void changeInvoiceDate(String roomId, Date newDate) {
        PmsBooking booking = getBookingFromRoom(roomId);
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.pmsBookingRoomId.equals(roomId)) {
                room.invoicedTo = newDate;
            }
        }
        saveBooking(booking);
    }

    private List<PmsBooking> filterByUser(List<PmsBooking> finalized, String userId) {
        if (userId == null || userId.isEmpty()) {
            return finalized;
        }

        User loggedon = userManager.getLoggedOnUser();
        if (loggedon == null) {
            return new ArrayList();
        }
        if (!loggedon.isAdministrator() && !loggedon.isEditor()) {
            if (!loggedon.id.equals(userId)) {
                return new ArrayList();
            }
        }

        List<PmsBooking> res = new ArrayList();
        for (PmsBooking booking : finalized) {
            if (booking.userId != null && booking.userId.equals(userId)) {
                res.add(booking);
            }
        }
        return res;
    }

    public PmsBooking getBookingFromBookingEngineIdUnfinalized(String bookingEngineId) {
        HashMap<String, String> map = buildBookingIdMap();
        PmsBooking booking = getBookingUnfinalized(map.get(bookingEngineId));
        return booking;
    }

    @Override
    public PmsBooking getBookingFromBookingEngineId(String bookingEngineId) {
        return finalize(getBookingFromBookingEngineIdUnfinalized(bookingEngineId));
    }

    /**
     * If a book has been started, it the old one need to keep staying on the
     * old room ( for statistics etc) Also, if a person moves between rooms, it
     * has to gain access to two rooms at the same time.
     *
     * @param booking
     * @param room
     * @return
     */
    private PmsBookingRooms splitBookingIfNesesary(PmsBooking booking, PmsBookingRooms room) {
        if (!room.isStarted() || room.isStartingToday()) {
            return room;
        }

        Gson gson = new Gson();
        String copy = gson.toJson(room);

        room.date.end = new Date();
        bookingEngine.changeDatesOnBooking(room.bookingId, room.date.start, room.date.end);

        PmsBookingRooms newRoom = gson.fromJson(copy, PmsBookingRooms.class);
        newRoom.clear();

        String res = addBookingToBookingEngine(booking, newRoom);
        if (res.isEmpty()) {
            removeAddonsByDate(room);
            removeAddonsByDate(newRoom);
        }

        return newRoom;
    }

    private String addBookingToBookingEngine(PmsBooking booking, PmsBookingRooms room) {
        Booking bookingToAdd = createBooking(room);

        List<Booking> bookingToAddList = new ArrayList();
        bookingToAddList.add(bookingToAdd);
        if (!canAdd(bookingToAddList)) {
            return "The room can not be added, its not available.";
        }

        room.unmarkOverBooking();
        room.addedToWaitingList = false;
        bookingEngine.addBookings(bookingToAddList);
        booking.addRoom(room);
        booking.attachBookingItems(bookingToAddList);

        return "";
    }

    @Override
    public void sendPaymentLink(String orderId, String bookingId, String email, String prefix, String phone) {
        PmsBooking booking = getBookingUnsecure(bookingId);
        if (booking == null) {
            booking = getBookingFromRoom(bookingId);
            if (booking != null) {
                bookingId = booking.id;
            }
        }
        orderIdToSend = orderId;
        emailToSendTo = email;
        prefixToSend = prefix;
        phoneToSend = phone;
        doNotification("booking_sendpaymentlink", bookingId);
    }

    @Override
    public void sendPaymentLinkWithText(String orderId, String bookingId, String email, String prefix, String phone, String message) {
        orderIdToSend = orderId;
        emailToSendTo = email;
        prefixToSend = prefix;
        phoneToSend = phone;
        messageToSend = message;
        doNotification("booking_sendpaymentlink", bookingId);
        messageToSend = null;
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
        if (cal.get(Calendar.DAY_OF_MONTH) == 25 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) == 26 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) == 27 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) == 28 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_MONTH) == 29 && cal.get(Calendar.MONTH) == 3) {
            return true;
        }
        return false;
    }

    public String dumpBooking(PmsBooking booking, boolean onlyoverbooking) {
        String res = "";
        res += booking.dump();
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            if(onlyoverbooking && !room.isOverBooking()) {
                continue;
            }
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            if (room.deletedByChannelManagerForModification) {
                continue;
            }
            if (type != null) {
                res += "   " + type.name + " - ";
            }
            if(room.isDeleted()) {
                res += " DELETED - ";
            } else if(room.isOverBooking()) {
                res += " OVERBOOKING - ";
            } else if(room.addedToWaitingList) {
                res += " WAITING - ";
            } else {
                res += " ACTIVE -";
            }
            if (room.date != null) {
                res += room.date.start + " frem til : " + room.date.end;
            }
            res += "<br>";

        }

        User user = userManager.getUserById(booking.userId);
        if (user != null) {
            res += "Full name: " + user.fullName + "<br>";
        }
        res += "Channel: " + booking.channel + "<bR>";
        res += "Channel id: " + booking.wubookChannelReservationId + "<bR>";
        res += "Wubookid: " + booking.wubookreservationid + "<br>";
        res += "GetShop booking id: " + booking.incrementBookingId + "<br>";
        res += "Address: " + storeManager.getMyStore().webAddressPrimary + "<br>";

        return res;
    }
    
    HashMap<String, PmsBooking> getBookingMap() {
        return bookings;
    }

    @Override
    public void addAddonsToBooking(Integer type, String roomId, boolean remove) {
        boolean foundRoom = true;
        PmsBooking booking = getBookingFromRoomSecure(roomId);
        if (booking == null) {
            foundRoom = false;
            booking = getBooking(roomId);
        }
        if (booking == null) {
            logPrint("Failed to find a booking while adding addons: " + roomId);
        }
        checkSecurity(booking);
        PmsBookingAddonItem addonConfig = configuration.addonConfiguration.get(type);

        if (!remove) {
            PmsBookingRooms room = booking.getRoom(roomId);
            if (room != null && !addonConfig.isValidForPeriode(room.date.start, room.date.end, booking.rowCreatedDate)) {
                return;
            }
        }

        List<String> roomIds = new ArrayList();
        if (!foundRoom) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                roomIds.add(room.pmsBookingRoomId);
            }
        } else {
            roomIds.add(roomId);
        }

        for (String tmpRoomId : roomIds) {
            PmsBookingRooms room = booking.getRoom(tmpRoomId);
            if (remove) {
                room.clearAddonType(type);
            }
            changeTimeFromAddon(addonConfig, room, remove);
            if (remove) {
                continue;
            }

            List<PmsBookingAddonItem> result = createAddonForTimePeriodeWithDiscount(type, room, booking);
            result = combineExistingAddons(room.addons, result);
            room.addons.addAll(result);
            for (PmsBookingAddonItem toReturn : result) {
                if (!toReturn.productId.equals(addonConfig.productId)) {
                    continue;
                }

                if (addonConfig.addonType == PmsBookingAddonItem.AddonTypes.BREAKFAST || addonConfig.dependsOnGuestCount) {
                    toReturn.count = room.numberOfGuests;
                }
            }

            setAddonPricesOnRoom(room, booking);
            updateRoomPriceFromAddons(room, booking);
        }
        saveBooking(booking);
    }

    public PmsBookingAddonItem createAddonToAdd(PmsBookingAddonItem addonConfig, Date date, PmsBookingRooms room) {

        Product product = productManager.getProduct(addonConfig.productId);
        PmsBookingAddonItem toReturn = new PmsBookingAddonItem();
        toReturn.addonType = addonConfig.addonType;
        if (product != null) {
            toReturn.price = product.price;
            toReturn.priceExTaxes = product.priceExTaxes;
            toReturn.productId = product.id;
        }
        toReturn.date = date;
        toReturn.bookingicon = addonConfig.bookingicon;
        toReturn.descriptionWeb = addonConfig.descriptionWeb;
        toReturn.isAvailableForBooking = addonConfig.isAvailableForBooking;
        toReturn.isAvailableForCleaner = addonConfig.isAvailableForCleaner;
        toReturn.isActive = addonConfig.isActive;
        toReturn.isSingle = addonConfig.isSingle;
        toReturn.isIncludedInRoomPrice = addonConfig.isIncludedInRoomPrice;
        toReturn.validDates = addonConfig.validDates;
        toReturn.dependsOnGuestCount = addonConfig.dependsOnGuestCount;
        toReturn.noRefundable = addonConfig.noRefundable;
        toReturn.displayInBookingProcess = addonConfig.displayInBookingProcess;
        toReturn.includedInBookingItemTypes = addonConfig.includedInBookingItemTypes;
        toReturn.setTranslationStrings(addonConfig.getTranslations());
        toReturn.onlyForBookingItems = addonConfig.onlyForBookingItems;
        toReturn.alwaysAddAddon = addonConfig.alwaysAddAddon;

        if (addonConfig.price != null && addonConfig.price > 0) {
            toReturn.price = addonConfig.price;
            toReturn.priceExTaxes = addonConfig.priceExTaxes;
        }

        if (room != null && room.bookingItemTypeId != null && addonConfig.includedInBookingItemTypes != null && addonConfig.includedInBookingItemTypes.contains(room.bookingItemTypeId)) {
            toReturn.isIncludedInRoomPrice = true;
        }

        return toReturn;
    }

    @Override
    public List<PmsBookingAddonItem> getAddonsWithDiscount(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        PmsPricing prices = getPriceObjectFromBooking(booking);
        User loggedOn = null;
        if (getSession() != null) {
            loggedOn = getSession().currentUser;
        }

        List<PmsBookingAddonItem> allAddons = new ArrayList(getConfiguration().addonConfiguration.values());
        List<PmsBookingAddonItem> result = new ArrayList();
        for (PmsBookingAddonItem item : allAddons) {
            List<PmsBookingAddonItem> addons = createAddonForTimePeriodeWithDiscount(item.addonType, room, booking);
            PmsBookingAddonItem addon = createAddonToAdd(item, room.date.start, room);
            Date created = booking.rowCreatedDate;
            if (created == null) {
                created = new Date();
            }
            if (!addon.isValidForPeriode(room.date.start, room.date.end, created)) {
                continue;
            }
            addon.count = item.count;

            Double price = 0.0;
            Integer count = 0;
            for (PmsBookingAddonItem tmp : addons) {
                if (tmp == null) {
                    continue;
                }
                double tmpPrice = tmp.price == null ? 0L : tmp.price;
                if (prices.productPrices.containsKey(item.productId)) {
                    tmpPrice = prices.productPrices.get(item.productId);
                }
                price += tmpPrice;
                count += tmp.count;
            }
            if (count > 0) {
                addon.price = price / count;
            }

            if (loggedOn != null && loggedOn.showExTaxes) {
                Product product = productManager.getProduct(addon.productId);
                addon.price = (double) Math.round(addon.price / ((100 + product.taxGroupObject.taxRate) / 100));
            }
            result.add(addon);
        }
        return result;
    }

    private Date addTimeUnit(Date start, Integer priceType) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);

        if (priceType == PmsBooking.PriceType.daily) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        } else if (priceType == PmsBooking.PriceType.weekly) {
            cal.add(Calendar.DAY_OF_YEAR, 7);
        } else if (priceType == PmsBooking.PriceType.hourly) {
            cal.add(Calendar.HOUR, 1);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        return cal.getTime();
    }

    @Override
    public void updateAddons(List<PmsBookingAddonItem> items, String bookingId) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        for (PmsBookingAddonItem item : items) {
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

    public void setPriceOnRoom(PmsBookingRooms room, boolean avgPrice, PmsBooking booking) {
        room.price = pmsInvoiceManager.calculatePrice(room.bookingItemTypeId, room.date.start, room.date.end, avgPrice, booking);
        room.priceWithoutDiscount = new Double(room.price);
        if (getConfigurationSecure().getUsePriceMatrixOnOrder()) {
            room.price = pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
            if (room.price.isNaN() || room.price.isInfinite()) {
                room.price = 0.0;
            }
        }
        room.taxes = pmsInvoiceManager.calculateTaxes(room.bookingItemTypeId);
        setAddonPricesOnRoom(room, booking);
        updateRoomPriceFromAddons(room, booking);
    }

    private void createUserForBooking(PmsBooking booking) {
        if (getSession() != null && getSession().currentUser != null) {
            booking.bookedByUserId = getSession().currentUser.id;
        }

        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.isCustomer()) {
            booking.userId = getSession().currentUser.id;
        }

        if (booking.userId != null && booking.userId.equals("quickreservation")) {
            User user = userManager.getUserByIdUnfinalized("quickreservation");
            if (user == null) {
                user = new User();
                user.fullName = "Quick reservation user";
                user.id = "quickreservation";
                userManager.saveUser(user);
            }
        } else if (booking.userId == null || booking.userId.isEmpty()) {
            User newuser = null;
            Company curcompany = createCompany(booking);
            if (curcompany != null) {
                List<User> existingUsers = userManager.getUsersThatHasCompany(curcompany.id);
                if (!existingUsers.isEmpty()) {
                    newuser = existingUsers.get(0);
                    for (User tmp : existingUsers) {
                        if (tmp.isCompanyMainContact) {
                            newuser = tmp;
                            break;
                        }
                    }
                } else {
                    newuser = createUser(booking);
                    curcompany = userManager.saveCompany(curcompany);
                    newuser.company.add(curcompany.id);
                    newuser.fullName = curcompany.name;
                    newuser.emailAddress = curcompany.email;
                    newuser.cellPhone = curcompany.phone;
                    newuser.prefix = curcompany.prefix;
                    newuser.address = curcompany.address;
                    newuser.isCompanyMainContact = true;
                }

                userManager.saveUserSecure(newuser);
                booking.userId = newuser.id;
            } else {
                newuser = createUser(booking);
                booking.userId = newuser.id;
            }
        }

        PmsUserDiscount disc = pmsInvoiceManager.getDiscountsForUser(booking.userId);
        if (disc != null && disc.supportInvoiceAfter) {
            booking.createOrderAfterStay = true;
        }
    }

    @Override
    public void splitBooking(List<String> roomIds) {
        PmsBooking booking = getBookingFromRoom(roomIds.get(0));
        if (booking.getActiveRooms().size() == 1) {
            return;
        }
        PmsBooking copy = booking.copy();
        List<PmsBookingRooms> roomsToSplit = new ArrayList();
        for (String roomId : roomIds) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.pmsBookingRoomId.equals(roomId)) {
                    roomsToSplit.add(room);
                    break;
                }
            }
        }

        if (roomsToSplit.isEmpty()) {
            return;
        }

        User user = userManager.getUserById(booking.userId);
        Order firstOrder = null;
        if (!booking.orderIds.isEmpty()) {
            firstOrder = orderManager.getOrderSecure(booking.orderIds.get(0));
        }
        List<CartItem> allItemsToMove = pmsInvoiceManager.removeOrderLinesOnOrdersForBooking(booking.id, roomIds);

        copy.removeAllRooms();
        copy.addRooms(roomsToSplit);
        copy.id = null;
        copy.orderIds.clear();
        copy.rowCreatedDate = new Date();

        if (!allItemsToMove.isEmpty()) {
            cartManager.getCart().addCartItems(allItemsToMove);
            orderManager.deleteVirtualOrders(booking.id);
            Order order = orderManager.createOrder(user.address);
            if (firstOrder != null) {
                order.payment = firstOrder.payment;
            }
            orderManager.saveOrder(order);
            addOrderToBooking(copy, order.id);
        }

        booking.removeRooms(roomsToSplit);
        saveObject(copy);
        bookings.put(copy.id, copy);

        saveBooking(booking);
        saveBooking(copy);

    }

    @Override
    public void deleteAllBookings(String code) {
        if (!code.equals("23424242423423455ggbvvcx")) {
            return;
        }
        Date now = new Date();
        List<PmsBooking> allToRemove = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            if (booking.isActiveOnDay(now) && booking.userId.equals("fastchecking_user")) {
                continue;
            }
            deleteObject(booking);
            allToRemove.add(booking);
        }

        for (PmsBooking remove : allToRemove) {
            for (PmsBookingRooms room : remove.getActiveRooms()) {
                bookingEngine.deleteBooking(room.bookingId);
            }
            bookings.remove(remove.id);
        }

        orderManager.deleteAllOrders();
        userManager.deleteAllUsers();
        pmsEventManager.deleteAllEvents();
    }

    private void checkForMissingEndDate(PmsBooking booking) {
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.date.end == null) {
                room.date.end = createInifinteDate();
            }
        }
    }

    @Override
    public void sendMessageOnRoom(String email, String title, String message, String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        message = configuration.emailTemplate.replace("{content}", message);
        messageManager.sendMail(email, "", title, message, getFromEmail(), getFromName());
        if (title == null || title.isEmpty()) {
            message = "Message sent to : " + email + " Message: " + message;
        } else {
            message = "Message sent to : " + email + " Message: " + message + ", title: " + title;
        }
        logEntry(message, booking.id, null, roomId, "email");
    }

    @Override
    public void sendSmsOnRoom(String prefix, String phone, String message, String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        logEntry(message + "<br> +" + prefix + " " + phone, booking.id, null, roomId, "sms");
        message = configuration.emailTemplate.replace("{content}", message);
        if (prefix.equals("47")) {
            messageManager.sendSms("sveve", phone, message, prefix, configuration.smsName);
        } else {
            messageManager.sendSms("nexmo", phone, message, prefix, configuration.smsName);
        }
    }

    @Override
    public void sendMessage(String bookingId, String email, String title, String message) {
        message = configuration.emailTemplate.replace("{content}", message);
        messageManager.sendMail(email, "", title, message, getFromEmail(), getFromName());
        message = "Message sent to : " + email + " Message: " + message + ", title: " + title;
        logEntry(message, bookingId, null, null, "email");
    }

    void setOrderIdToSend(String id) {
        this.orderIdToSend = id;
    }

    @Override
    public void sendCode(String prefix, String phoneNumber, String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.pmsBookingRoomId.equals(roomId)) {
                phoneToSend = phoneNumber;
                prefixToSend = prefix;
                doNotification("room_added_to_arx", booking, room);
            }
        }
    }

    @Override
    public void updateAdditionalInformationOnRooms(PmsAdditionalItemInformation info) {
        List<PmsAdditionalItemInformation> toRemove = new ArrayList();
        for (PmsAdditionalItemInformation test : addiotionalItemInfo.values()) {
            if (test.itemId.equals(info.itemId)) {
                System.out.println("Match: " + info.itemId);
                toRemove.add(test);
            }
        }

        for (PmsAdditionalItemInformation remove : toRemove) {
            deleteObject(remove);
            addiotionalItemInfo.remove(remove.id);
        }

        saveObject(info);
        addiotionalItemInfo.put(info.itemId, info);
    }

    @Override
    public HashMap<String, String> getChannelMatrix() {
        HashMap<String, String> res = new HashMap();
        res.put("web", "Website");
        HashMap<String, PmsChannelConfig> getChannels = configuration.getChannels();
        for (String key : getChannels.keySet()) {
            if (getChannels.get(key).channel != null && !getChannels.get(key).channel.isEmpty()) {
                res.put(key, getChannels.get(key).channel);
            }
        }
        for (PmsBooking booking : bookings.values()) {
            if (booking.channel != null && !booking.channel.trim().isEmpty()) {
                res.put(booking.channel, booking.channel);
            }
        }

        for (String key : res.keySet()) {
            if (getChannelConfig(key).humanReadableText != null
                    && !configuration.getChannelConfiguration(key).humanReadableText.isEmpty()) {
                res.put(key, configuration.getChannelConfiguration(key).humanReadableText);
            }
        }

        return res;
    }

    private List<PmsBooking> filterByChannel(List<PmsBooking> finalized, String channel) {
        if (channel == null || channel.trim().isEmpty()) {
            return finalized;
        }

        List<PmsBooking> res = new ArrayList();
        for (PmsBooking booking : finalized) {
            if (booking.channel != null && booking.channel.equals(channel)) {
                res.add(booking);
            }
            if (channel.equals("web") && (booking.channel == null || booking.channel.trim().isEmpty())) {
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
        for (String orderId : booking.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            for (CartItem item : order.cart.getItems()) {
                if (item.getProduct().externalReferenceId.equals(roomId)) {
                    if (item.getEndingDate().after(end)) {
                        end = item.getEndingDate();
                    }
                }
            }
        }

        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.pmsBookingRoomId.equals(roomId)) {
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
        for (BookingItemType type : types) {
            HashMap<Integer, Double> result = new HashMap();
            for (BookingItem item : items) {
                if (item.bookingItemTypeId == null || !item.bookingItemTypeId.equals(type.id)) {
                    continue;
                }
                PmsAdditionalItemInformation additional = getAdditionalInfo(item.id);
                List<Date> cleaningDates = additional.getAllCleaningDates();
                for (Date date : cleaningDates) {
                    if (date.before(start)) {
                        continue;
                    }
                    if (date.after(end)) {
                        continue;
                    }
                    cal.setTime(date);
                    int weekday = cal.get(Calendar.DAY_OF_WEEK);
                    weekday--;
                    if (weekday == -1) {
                        weekday = 7;
                    }
                    Double tmpCount = 0.0;
                    if (result.containsKey(weekday)) {
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
        if (fetchedProducts.containsKey(productId)) {
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
        if (filter == null) {
            return finalized;
        }

        if (!filter.onlyUntransferredToBookingCom) {
            return finalized;
        }

        List<PmsBooking> res = new ArrayList();
        for (PmsBooking book : finalized) {
            if (!book.transferredToRateManager) {
                res.add(book);
            }
        }

        return res;
    }

    @Override
    public void massUpdatePrices(PmsPricing price, String bookingId) throws Exception {
        PmsBooking booking = getBooking(bookingId);
        checkAndReportPriceMatrix(booking, "When massupdating prices");
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            for (String day : room.priceMatrix.keySet()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = sdf.parse(day);
                LocalDate ldate = new LocalDate(date);
                int dayofweek = ldate.dayOfWeek().get();
                if (dayofweek == 1) {
                    if (price.price_mon != null) {
                        room.priceMatrix.put(day, price.price_mon);
                    }
                }
                if (dayofweek == 2) {
                    if (price.price_tue != null) {
                        room.priceMatrix.put(day, price.price_tue);
                    }
                }
                if (dayofweek == 3) {
                    if (price.price_wed != null) {
                        room.priceMatrix.put(day, price.price_wed);
                    }
                }
                if (dayofweek == 4) {
                    if (price.price_thu != null) {
                        room.priceMatrix.put(day, price.price_thu);
                    }
                }
                if (dayofweek == 5) {
                    if (price.price_fri != null) {
                        room.priceMatrix.put(day, price.price_fri);
                    }
                }
                if (dayofweek == 6) {
                    if (price.price_sat != null) {
                        room.priceMatrix.put(day, price.price_sat);
                    }
                }
                if (dayofweek == 7) {
                    if (price.price_sun != null) {
                        room.priceMatrix.put(day, price.price_sun);
                    }
                }
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

        } catch (Exception e) {

        }
        return contract;
    }

    void setEmailToSendTo(String email) {
        emailToSendTo = email;
    }

    private void feedGrafanaNotificationDone(String key) {
        HashMap<String, Object> toAdd = new HashMap();
        toAdd.put("key", key);
        grafanaManager.addPoint("pmsmanager", "notificationsent", toAdd);
    }

    private void feedGrafana(PmsBooking booking) {
        HashMap<String, Object> toAdd = new HashMap();
        int guests = 0;
        int addons = 0;
        for (PmsBookingRooms room : booking.rooms) {
            guests += room.numberOfGuests;
            addons += room.addons.size();
        }
        toAdd.put("reservations", (Number) booking.rooms.size());
        toAdd.put("booking", 1);
        toAdd.put("guests", (Number) guests);
        toAdd.put("addons", (Number) addons);
        toAdd.put("storeid", (String) storeId);

        GrafanaFeederImpl feeder = new GrafanaFeederImpl();
        grafanaManager.addPoint("pmsmanager", "booking", toAdd);
    }

    @Override
    public List<PmsAdditionalTypeInformation> getAdditionalTypeInformation() throws Exception {
        return additionDataForTypes;
    }

    @Override
    public PmsAdditionalTypeInformation getAdditionalTypeInformationById(String typeId) throws Exception {
        for (PmsAdditionalTypeInformation add : additionDataForTypes) {
            if (add != null && add.typeId != null && add.typeId.equals(typeId)) {
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
        if (addonConfig.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
            String[] defaultStartSplitted = configuration.getDefaultStart().split(":");
            int hour = new Integer(defaultStartSplitted[0]);
            if (!remove) {
                hour = hour - 3;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.start);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            if (getConfigurationSecure().isArx()) {
                resetDoorLockCode(room);
            }
            room.date.start = cal.getTime();
            if (room.bookingId != null) {
                updateBooking(room);
            }
        }
        if (addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
            String[] defaultEndSplitted = configuration.getDefaultEnd().split(":");
            Integer addHours = getConfigurationSecure().numberOfHoursToExtendLateCheckout;
            int hour = new Integer(defaultEndSplitted[0]);
            if (!remove) {
                hour = hour + addHours;
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(room.date.end);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            room.date.end = cal.getTime();
            if (getConfigurationSecure().isArx()) {
                resetDoorLockCode(room);
            }
            if (room.bookingId != null) {
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
        if (userManager.getLoggedOnUser() != null && userManager.getLoggedOnUser().isAdministrator()) {
            return false;
        }
        return configuration.deleteAllWhenAdded;
    }

    @Override
    public void createChannel(String channel) {
        getChannelConfig(channel);
    }

    private PmsChannelConfig getChannelConfig(String channel) {
        if (!getConfigurationSecure().channelExists(channel)) {
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
        for (PmsBooking booking : bookings.values()) {
            for (String oId : booking.orderIds) {
                if (oId.equals(orderId)) {
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

        for (PmsBooking booking : bookings.values()) {
            test.add(booking.id);
            System.out.println("Merging: " + booking.id);
            boolean found = false;
            for (PmsBooking booking1 : bookings.values()) {
                if (test.contains(booking1.id)) {
                    continue;
                }
                for (String orderId : booking1.orderIds) {
                    if (booking.orderIds.contains(orderId)) {
                        booking.rooms.addAll(booking1.getAllRoomsIncInactive());
                        toRemove.add(booking1);
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                saveBooking(booking);
            }
        }
        for (PmsBooking book : toRemove) {
            bookings.remove(book.id);
            hardDeleteBooking(book, "mergebooking");
        }
    }

    private void doSorting(List<PmsRoomSimple> res, PmsBookingFilter filter) {
        if (filter.sorting == null || filter.sorting.isEmpty()) {
            return;
        }

        if (filter.sorting.equals("room")) {
            Collections.sort(res, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    return o1.room.compareTo(o2.room);
                }
            });
        }
    }

    private boolean verifyPhoneOnBooking(PmsBooking booking, boolean verifyPrefixFromCountryCode) {
        String countryCode = booking.countryCode;
        if (booking.countryCode == null || booking.countryCode.trim().isEmpty()) {
            try {
                HashMap<Integer, String> list = PhoneCountryCodeList.getList();
                Integer prefix = new Integer(booking.rooms.get(0).guests.get(0).prefix);
                if (list.containsKey(prefix)) {
                    countryCode = list.get(prefix);
                }
            } catch (Exception e) {
                countryCode = "NO";
            }
        }

        if (booking.userId != null) {
            User user = userManager.getUserById(booking.userId);
            if (user == null) {
                return false;
            }
            String prefix = user.prefix;

            HashMap<String, String> res = SmsHandlerAbstract.validatePhone("+" + prefix, user.cellPhone, countryCode, verifyPrefixFromCountryCode);
            if (res != null) {
                prefix = res.get("prefix");
                String phone = res.get("phone");
                if (prefix != null && phone != null) {
                    boolean save = false;
                    if (!prefix.equals(user.prefix)) {
                        user.prefix = prefix;
                        save = true;
                    }
                    if (!phone.equals(user.cellPhone)) {
                        user.cellPhone = phone;
                        save = true;
                    }
                    if (save) {
                        userManager.saveUserSecure(user);
                    }
                }
            } else {
                //Warn about wrong phone number.
            }
        }

        boolean save = false;
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            for (PmsGuests guest : room.guests) {
                HashMap<String, String> res = SmsHandlerAbstract.validatePhone("+" + guest.prefix, guest.phone, countryCode, verifyPrefixFromCountryCode);
                if (res != null) {
                    String prefix = res.get("prefix");
                    String phone = res.get("phone");
                    if (prefix != null && phone != null) {
                        if (!prefix.equals(guest.prefix)) {
                            guest.prefix = prefix;
                            save = true;
                        }
                        if (!phone.equals(guest.phone)) {
                            guest.phone = phone;
                            save = true;
                        }
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
        if (getShopLockSystemManager.isActivated()) {
            checkIfGuestHasArrivedApac();
            PmsManagerProcessor processor = new PmsManagerProcessor(this);
            processor.processStartEndings();
            return;
        }
        
        if (!hasLockSystemActive()) {
            PmsManagerProcessor processor = new PmsManagerProcessor(this);
            processor.processStartEndings();
            return;
        }
        if (getConfigurationSecure().isArx()) {
            long end = System.currentTimeMillis();
            long start = end - (1000 * 60 * 25);
            try {
                HashMap<String, List<AccessLog>> doors = doorManager.getLogForAllDoor(start, end);
                for (List<AccessLog> log : doors.values()) {
                    for (AccessLog l : log) {
                        if (l.card != null && !l.card.isEmpty()) {
                            markAsArrived(l.card);
                        }
                    }
                }
            }catch(Exception e) {
                //Ignore this message.
            }
        }
        if (getConfigurationSecure().isGetShopHotelLock()) {
            long end = System.currentTimeMillis();
            long start = end - (1000 * 60 * 10);
            HashMap<String, List<AccessLog>> doors = doorManager.getLogForAllDoor(start, end);
            List<BookingItem> items = bookingEngine.getBookingItems();
            for (String deviceId : doors.keySet()) {
                List<AccessLog> log = doors.get(deviceId);
                for (AccessLog l : log) {

                    BookingItem item = null;
                    for (BookingItem tmpItem : items) {
                        if (tmpItem.bookingItemAlias != null && tmpItem.bookingItemAlias.equals(deviceId)) {
                            item = tmpItem;
                            break;
                        }
                    }

                    if (item == null) {
                        continue;
                    }

                    for (PmsBooking booking : bookings.values()) {
                        for (PmsBookingRooms room : booking.rooms) {
                            if (!room.isStarted() || room.isEnded() || room.checkedin) {
                                continue;
                            }
                            if (room.bookingItemId == null || !room.bookingItemId.equals(item.id)) {
                                continue;
                            }
                            markGuestArrivedInternal(booking, room);
                        }
                    }
                }
            }
        }
        PmsManagerProcessor processor = new PmsManagerProcessor(this);
        processor.processStartEndings();
    }

    private void markAsArrived(String card) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.rooms) {
                if (!room.isStarted() || room.isEnded()) {
                    continue;
                }
                if (room.code != null && room.code.equals(card) && !room.checkedin) {
                    markGuestArrivedInternal(booking, room);
                }
            }
        }
    }

    private void markGuestArrivedInternal(PmsBooking booking, PmsBookingRooms room) throws ErrorException {
        if (room.checkedin) {
            return;
        }

        doNotification("room_dooropenedfirsttime", booking, room);
        room.checkedin = true;
        saveBooking(booking);
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
        if (log.logText == null || log.logText.trim().isEmpty()) {
            return;
        }
        String userId = "";
        if (getSession() != null && getSession().currentUser != null) {
            userId = getSession().currentUser.id;
        }
        if (log.bookingItemId != null) {
            BookingItem item = bookingEngine.getBookingItem(log.bookingItemId);
            if (item != null) {
                log.bookingItemType = item.bookingItemTypeId;
            }
        }
        log.userId = userId;
        logentries.add(log);
        saveObject(log);

        if (log.tag != null && log.tag.equals("mobileapp")) {
            List<String> emailsToNotify = configuration.emailsToNotify.get("applogentry");
            if (emailsToNotify != null) {
                for (String email : emailsToNotify) {
                    String text = "";
                    text += "<br/>Store email: " + getStoreEmailAddress();
                    text += "<br/>Store name: " + getStoreName();
                    text += "<br/>Store default address: " + getStoreDefaultAddress();
                    text += "<br/>Entry added:<br>" + log.logText;

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

        PmsMailStatistics mailer = new PmsMailStatistics(webAddress, user.username, user.internalPassword, null, "");
        Thread t = new Thread(mailer, "My Thread");
        t.start();
    }

    public void checkForRoomsToClose() {
        if (!getConfigurationSecure().automaticallyCloseRoomIfDirtySameDay) {
            return;
        }
        Integer closeHour = getConfigurationSecure().getCloseRoomNotCleanedAtHour();
        Date start = new Date();
        Calendar cal = Calendar.getInstance();
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay < closeHour || hourOfDay > (closeHour+2)) {
            return;
        }

        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 20);
        start = cal.getTime();
        
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        Date end = cal.getTime();

        List<BookingItem> items = bookingEngine.getAllAvailbleItems(start, end);

        List<PmsAdditionalItemInformation> additionals = getAllAdditionalInformationOnRooms();
        HashMap<String, PmsAdditionalItemInformation> additionalMap = new HashMap();
        for (PmsAdditionalItemInformation t : additionals) {
            additionalMap.put(t.itemId, t);
        }

        for (BookingItem item : items) {
            PmsAdditionalItemInformation additional = additionalMap.get(item.id);
            if (!additional.isClean(false)) {
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
        if (!bookingEngine.canAdd(booking)) {
            return false;
        }

        bookingEngine.addBookings(toAdd);
        wubookManager.setAvailabilityChanged(booking.startDate, booking.endDate);

        return true;
    }

    @Override
    public String generateNewCodeForRoom(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.findRoom(roomId);
        resetDoorLockCode(room);
        room.code = generateCode();
        if (room.isStarted() && !room.isEnded()) {
            room.forceUpdateLocks = true;
        }
        saveBooking(booking);
        processor();
        return room.code;
    }

    public List<PmsBookingAddonItem> getAddonsForRoom(String roomId) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);
        for (PmsBookingAddonItem item : room.addons) {
            if (item.productId != null && !item.productId.isEmpty()) {
                Product product = productManager.getProduct(item.productId);
                product.updateTranslation(getSession().language);
                item.setName(product);
            }
        }
        return room.addons;
    }

    public void addProductToRoomDefaultCount(String productId, String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);

        HashMap<Integer, PmsBookingAddonItem> addons = getConfigurationSecure().addonConfiguration;
        int size = 1;
        for (PmsBookingAddonItem item : addons.values()) {
            if (!item.productId.equals(productId)) {
                continue;
            }
            if (item.dependsOnGuestCount) {
                size = room.numberOfGuests;
            }
        }
        addProductToRoom(productId, room.pmsBookingRoomId, size);
    }

    @Override
    public void addProductToRoom(String productId, String pmsRoomId, Integer count) {
        addProductToRoom(productId, pmsRoomId, count, false);
    }

    public void addAddonsToBookingWithCount(Integer type, String pmsBookingRoomId, boolean b, int count) {
        addAddonsToBooking(type, pmsBookingRoomId, b);
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
            if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                for (PmsBookingAddonItem item : room.addons) {
                    if (item.addonType.equals(type)) {
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
        for (PmsBookingAddonItem item : addons.values()) {
            if (item.productId != null && !item.productId.isEmpty()) {
                Product product = productManager.getProduct(item.productId);
                if (product != null) {
                    product.updateTranslation(getSession().language);
                    item.setName(product);
                    result.add(item);
                }
            }
        }
        return result;
    }

    public PmsBooking doCompleteBooking(PmsBooking booking) {
        String rawBooking = "";
        if (booking != null) {
            Gson gson = new Gson();
            rawBooking = gson.toJson(booking);
            gsTiming("Created booking from json object");
        }
        
        if(booking.language == null || booking.language.trim().isEmpty()) {
            String lang = getStoreSettingsApplicationKey("language");
            if(lang == null || lang.isEmpty()) {
                lang = "en_en";
            }
            booking.language = lang;
            if(getSession().language != null && !getSession().language.isEmpty()) {
                booking.language = getSession().language;
            }
        }
        
        if (getConfigurationSecure().notifyGetShopAboutCriticalTransactions) {
            messageManager.sendErrorNotification("Booking completed.", null);
        }
        if (booking.getActiveRooms().isEmpty() && !booking.hasWaitingRooms()) {
            logPrint("COMPLETECURRENTBOOKING : No rooms active on booking." + booking.id);
            return null;
        }
        gsTiming("Notified admins");
        if (!bookingEngine.isConfirmationRequired()) {
            bookingEngine.setConfirmationRequired(true);
        }

        Integer result = 0;
        try {
            convertCheckInAndCheckoutToLocalTimeZone(booking);
            checkForMissingEndDate(booking);

            gsTiming("Checked for missing end dates");
            booking.isDeleted = false;

            List<Booking> bookingsToAdd = buildRoomsToAddToEngineList(booking);
            gsTiming("Getting cupons");
            Coupon coupon = getCouponCode(booking);
            gsTiming("Got cupons");
            if (coupon != null) {
                cartManager.subtractTimesLeft(coupon.code);
               gsTiming("Subsctracted coupons");
            }
            createUserForBooking(booking);
            addDefaultAddons(booking);
            checkIfBookedBySubAccount(booking);
            if (userManager.getUserById(booking.userId) == null || userManager.getUserById(booking.userId).suspended) {
                logPrint("User is suspended." + booking.id);
                return null;
            }
            gsTiming("Created user for booking");
            
            boolean canAdd = canAdd(bookingsToAdd);
            if (configuration.payAfterBookingCompleted() && !booking.createOrderAfterStay && !booking.hasOverBooking() && canAdd) {
                gsTiming("get priceobject from booking");
                booking.priceType = getPriceObjectFromBooking(booking).defaultPriceType;
                gsTiming("Got priceobject from booking");
                pmsInvoiceManager.createPrePaymentOrder(booking);
                gsTiming("Created payment for order");
            }

            if(configuration.deleteAllWhenAdded || canAdd || booking.hasOverBooking() || booking.hasWaitingRooms()) {
                result = completeBooking(bookingsToAdd, booking, canAdd);
            } else {
                result = -10;
            }
            gsTiming("Completed booking");

            if (result == 0) {
                if (!configuration.payAfterBookingCompleted()) {
                    if (bookingIsOK(booking)) {
                        if (!booking.confirmed || storeManager.isPikStore()) {
                            doNotification("booking_completed", booking, null);
                        } else {
                            doNotification("booking_confirmed", booking, null);
                        }
                        gsTiming("Notified booking confirmed");
                    }
                }
                checkIfNeedToBeAssignedToRoomWithSpecialAddons(booking);
                bookingUpdated(booking.id, "created", null);
                gsTiming("Booking confirmed");
                return booking;
            }
        } catch (Exception e) {
            messageManager.sendErrorNotification("This should never happen and need to be investigated : Unknown booking exception occured for booking id: " + booking.id + ", raw: " + rawBooking, e);
            logPrintException(e);
        }
        logPrint("COMPLETECURRENTBOOKING : Result is : " + result);
        return null;
    }

    @Override
    public List<SimpleInventory> getSimpleInventoryList(String roomName) {
        List<BookingItem> items = bookingEngine.getBookingItems();
        String itemId = null;
        for (BookingItem item : items) {
            if (item.bookingItemName.equals(roomName)) {
                itemId = item.id;
            }
        }
        List<SimpleInventory> res = new ArrayList();
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        for (PmsInventory inv : additional.inventory.values()) {
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
        for (SimpleInventory simple : inventories) {
            if (getConfigurationSecure().autoAddMissingItemsToRoom && roomId != null) {
                addProductToRoom(simple.productId, roomId, 0);
                if (simple.missingCount > 0) {
                    if (!hasCaretakerTask(itemId, simple.productId)) {
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
        for (PmsBookingAddonItem item : room.addons) {
            if (item.addonId.equals(addonId)) {
                toRemove = item;
            }
        }
        if (toRemove != null) {
            room.addons.remove(toRemove);
        }
        saveBooking(booking);
    }

    @Override
    public void saveCareTakerJob(PmsCareTaker job) {
        if (job.id == null || job.id.isEmpty()) {
            List<String> emails = getConfigurationSecure().emailsToNotify.get("caretaker");
            if (emails != null) {
                finalizeCareTakerJob(job);
                for (String email : emails) {
                    String msg = "Task: " + job.description;
                    if (job.productName != null) {
                        msg += ", " + job.productName;
                    }
                    if (job.roomName != null) {
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
        for (PmsCareTaker taker : res) {
            finalizeCareTakerJob(taker);
        }
        return res;
    }

    @Override
    public PmsCareTaker getCareTakerJob(String id) {
        return careTaker.get(id);
    }

    private void removeCaretakerMissingInventory(String itemId, String productId) {
        if (itemId == null) {
            return;
        }

        List<PmsCareTaker> toRemove = new ArrayList();
        for (PmsCareTaker taker : careTaker.values()) {
            if (taker.dateCompleted != null) {
                continue;
            }
            if (taker.inventoryProductId.equals(productId) && taker.roomId.equals(itemId)) {
                toRemove.add(taker);
            }
        }

        for (PmsCareTaker remove : toRemove) {
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
        if (taker.inventoryProductId != null) {
            taker.productName = productManager.getProduct(taker.inventoryProductId).name;
        }
        if (taker.roomId != null && !taker.roomId.isEmpty()) {
            taker.roomName = bookingEngine.getBookingItem(taker.roomId).bookingItemName;
        }
        if (taker.dateCompleted != null) {
            taker.completed = true;
        }
    }

    private boolean hasCaretakerTask(String itemId, String productId) {
        if (itemId == null || productId == null) {
            return false;
        }
        for (PmsCareTaker taker : careTaker.values()) {
            if (taker.dateCompleted == null && taker.inventoryProductId.equals(productId) && taker.roomId.equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sendConfirmation(String email, String bookingId, String type) {
        emailToSendTo = email;

        if (type == "confirmation") {
            doNotification("booking_confirmed", bookingId);
        } else {
            doNotification("booking_completed", bookingId);
        }

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
        filter.normalizeStartEndDateByBeginningEndOfDay();
        
        List<PmsRoomSimple> checkoutRooms = getSimpleRooms(filter);

        filter.filterType = "checkin";
        List<PmsRoomSimple> checkinRooms = getSimpleRooms(filter);

        List<PmsBookingRooms> intervalCleanings = getRoomsNeedingIntervalCleaning(new Date());

        for (PmsAdditionalItemInformation room : allRooms) {
            
            if(room.itemId.equals("4544add4-13da-42cf-aa16-6455a3983092")) {
                System.out.println("check");
            }

            
            RoomCleanedInformation info = new RoomCleanedInformation();
            info.roomId = room.itemId;
            info.hideFromCleaningProgram = room.hideFromCleaningProgram;
            boolean checkoutToday = false;
            boolean checkedoutToday = false;
            boolean checkinToday = false;
            boolean needInterval = false;

            for (PmsRoomSimple simple : checkoutRooms) {
                if (simple.bookingItemId != null && simple.bookingItemId.equals(room.itemId)) {
                    checkoutToday = true;
                    if(simple.checkedOut || simple.end < System.currentTimeMillis()) {
                        checkedoutToday = true;
                    }
                }
            }
            for (PmsRoomSimple simple : checkinRooms) {
                if (simple.bookingItemId != null && simple.bookingItemId.equals(room.itemId)) {
                    checkinToday = true;
                }
            }
            for (PmsBookingRooms tmproom : intervalCleanings) {
                if (tmproom.bookingItemId.equals(room.itemId)) {
                    needInterval = true;
                }
            }
            if (room.inUse && !checkoutToday && !room.closed && !checkinToday && !room.closedByCleaningProgram) {
                info.cleaningState = RoomCleanedInformation.CleaningState.inUse;
            } else if (room.isClean() || room.isUsedToday()) {
                info.cleaningState = RoomCleanedInformation.CleaningState.isClean;
            } else {
                if(checkedoutToday) {
                    info.cleaningState = RoomCleanedInformation.CleaningState.needCleaningCheckedOut;
                } else {
                    info.cleaningState = RoomCleanedInformation.CleaningState.needCleaning;
                }
            }
            if (needInterval && !room.isClean(true)) {
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
        for (PmsBooking booking : bookings.values()) {
            if (booking.userId.equals(userId)) {
                for (PmsBookingRooms room : booking.getActiveRooms()) {
                    if (room.isDeleted() || booking.isDeleted) {
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
        for (PmsRoomSimple r : rooms) {
            if (r.pmsRoomId.equals(pmsRoomId)) {
                found = true;
            }
        }
        if (found) {
            PmsBooking booking = getBookingFromRoom(pmsRoomId);
            for (PmsBookingRooms r : booking.rooms) {
                if (r.pmsBookingRoomId.equals(pmsRoomId)) {
                    r.requestedEndDate = date;
                }
            }
            saveBooking(booking);
        }
    }

    /**
     * Check this
     *
     * @param type
     * @param room
     * @param booking
     * @return
     */
    private List<PmsBookingAddonItem> createAddonForTimePeriodeWithDiscount(Integer type, PmsBookingRooms room, PmsBooking booking) {
        List<PmsBookingAddonItem> result = createAddonForTimePeriode(type, room, booking.priceType);

        for (PmsBookingAddonItem item : result) {
            int days = pmsInvoiceManager.getNumberOfDays(room.date.start, room.date.end);
            if (cartManager.couponIsValid(booking.rowCreatedDate, booking.couponCode, item.date, item.date, item.productId, days)) {
                Coupon coupon = cartManager.getCoupon(booking.couponCode);
                if (coupon.containsAddonProductToInclude(item.productId)) {
                    item.price = cartManager.calculatePriceForCouponWithoutSubstract(booking.couponCode, item.price, days, room.numberOfGuests, room.bookingItemTypeId);
                }
            }
            if (item.includedInBookingItemTypes.contains(room.bookingItemTypeId)) {
                item.isIncludedInRoomPrice = true;
            }
        }

        return result;
    }

    private List<PmsBookingAddonItem> createAddonForTimePeriode(Integer type, PmsBookingRooms room, Integer priceType) {
        Date start = room.date.start;
        Date end = room.date.end;

        List<PmsBookingAddonItem> result = new ArrayList();
        PmsBookingAddonItem addonConfig = configuration.addonConfiguration.get(type);
        if (addonConfig.isSingle) {
            if (addonConfig.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start, room);
                result.add(toAdd);
            } else if (addonConfig.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, end, room);
                result.add(toAdd);
            } else {
                PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start, room);
                result.add(toAdd);
            }
        } else {
            start = pmsInvoiceManager.normalizeDate(start, true);
            if (end != null) {
                end = pmsInvoiceManager.normalizeDate(end, false);
                while (true) {
                    PmsBookingAddonItem toAdd = createAddonToAdd(addonConfig, start, room);
                    result.add(toAdd);
                    start = addTimeUnit(start, priceType);
                    if (start.after(end)) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void endStay(String itemId) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.bookingItemId != null && room.bookingItemId.equals(itemId)) {
                    if (room.isEndingToday() && !room.isEnded()) {
                        changeDatesOnRoom(room, room.date.start, new Date());
                        logEntry("Cleaning program is ending the stay.", booking.id, itemId, room.pmsBookingRoomId, "endstay");
                    }
                }
            }
        }
    }

    private void changeDatesOnRoom(PmsBookingRooms room, Date start, Date end) {
        PmsBooking booking = getBookingFromRoom(room.pmsBookingRoomId);
        Date now = new Date();
        if (!room.isStartingToday() && room.isStarted() && (!room.isEnded() || room.isEndingToday())
                && (start.before(now) && end.after(now))) {
            //This is extending a stay, we need to remove cleaning and mark it as cleaned.
            if (!getConfiguration().isGetShopHotelLock() && !getShopLockSystemManager.isActivated()) {
                room.forceUpdateLocks = true;
            }
        }
        bookingUpdated(getBookingFromRoom(room.pmsBookingRoomId).id, "date_changed", room.pmsBookingRoomId);
        if (room.bookingId != null && !room.bookingId.isEmpty() && !room.isDeleted()) {
            bookingEngine.changeDatesOnBooking(room.bookingId, start, end);
        }
        room.date.start = start;
        room.date.end = end;
        room.date.exitCleaningDate = null;
        room.date.cleaningDate = null;
        if (room.addedToArx) {
            if (room.isStarted() && !room.isEnded()) {
                if (!getConfigurationSecure().isGetShopHotelLock() && !room.isEnded() && !getShopLockSystemManager.isActivated()) {
                    room.forceUpdateLocks = true;
                }
            }
        }

        if (configuration.updatePriceWhenChangingDates) {
            setPriceOnRoom(room, true, booking);
        }
        List<PmsBookingRooms> list = new ArrayList();
        list.add(room);
        addDefaultAddonsToRooms(list);
        pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
        bookingUpdated(getBookingFromRoom(room.pmsBookingRoomId).id, "date_changed", room.pmsBookingRoomId);
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
        if (current.containsKey(code)) {
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
        for (PmsBookingAddonItem item : room.addons) {
            types.put(item.addonType, "1");
        }

        for (Integer type : types.keySet()) {
            if (type == 1 || getConfiguration().addonConfiguration.get(type).dependsOnGuestCount) {
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
        if (filter.filterType == null || !filter.filterType.equals("unpaid")) {
            return finalized;
        }

        List<PmsBooking> unpaidBookings = new ArrayList();

        for (PmsBooking booking : finalized) {
            if (!booking.payedFor) {
                unpaidBookings.add(booking);
            }
        }

        return unpaidBookings;
    }

    @Override
    public List<PmsBookingAddonViewItem> getItemsForView(String viewId, Date date) {

        PmsMobileView view = getConfiguration().mobileViews.get(viewId);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.add(Calendar.DAY_OF_YEAR, view.daysDisplacement);
        Date startDate = startCal.getTime();

        List<PmsBookingAddonViewItem> items = new ArrayList();

        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                for (PmsBookingAddonItem item : room.addons) {
                    if (!view.products.contains(item.productId)) {
                        continue;
                    }
                    boolean toBeAdded = pmsInvoiceManager.isSameDay(item.date, startDate);
                    if (view.viewType == PmsMobileView.PmsMobileViewType.ALLACTIVE) {
                        if (room.isActiveOnDay(date)) {
                            toBeAdded = true;
                        }
                    }
                    if (toBeAdded) {
                        if (view.paidFor && !pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId)) {
                            if (!room.forceAccess) {
                                continue;
                            }
                        }
                        PmsBookingAddonViewItem toAdd = new PmsBookingAddonViewItem();
                        toAdd.count = item.count;
                        toAdd.addonId = item.addonId;
                        toAdd.productName = productManager.getProduct(item.productId).name;
                        if (!room.guests.isEmpty()) {
                            toAdd.name = room.guests.get(0).name;
                        }

                        toAdd.delivered = 0;
                        for (PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
                            if (entry.addonId.equals(item.addonId)) {
                                toAdd.delivered++;
                            }
                        }

                        BookingItem bookingItem = bookingEngine.getBookingItem(room.bookingItemId);
                        if (bookingItem != null) {
                            toAdd.roomName = bookingItem.bookingItemName;
                        }
                        User user = userManager.getUserById(booking.userId);
                        if (user != null) {
                            toAdd.owner = user.fullName;
                        }
                        items.add(toAdd);
                    }
                }
            }
        }

        if (view.sortType == PmsMobileView.PmsMobileSortyType.BYOWNER) {
            Collections.sort(items, new Comparator<PmsBookingAddonViewItem>() {
                public int compare(PmsBookingAddonViewItem s1, PmsBookingAddonViewItem s2) {
                    if (s1 == null || s1.owner == null || s2 == null || s2.owner == null) {
                        return 0;
                    }
                    return s1.owner.compareTo(s2.owner);
                }
            });
        }
        if (view.sortType == PmsMobileView.PmsMobileSortyType.BYROOM) {
            Collections.sort(items, new Comparator<PmsBookingAddonViewItem>() {
                public int compare(PmsBookingAddonViewItem s1, PmsBookingAddonViewItem s2) {
                    if (s1.roomName == null || s2.roomName == null) {
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

        if (getSession() != null && getSession().currentUser != null) {
            PmsUserDiscount discounts = pmsInvoiceManager.getDiscountsForUser(getSession().currentUser.id);
            if (discounts != null && discounts.pricePlan != null && !discounts.pricePlan.isEmpty()) {
                code = discounts.pricePlan;
            }
        }
        if (booking.userId != null && !booking.userId.isEmpty()) {
            PmsUserDiscount discounts = pmsInvoiceManager.getDiscountsForUser(booking.userId);
            if (discounts != null && discounts.pricePlan != null && !discounts.pricePlan.isEmpty()) {
                code = discounts.pricePlan;
            }
        }

        Coupon coupon = getCouponCode(booking);
        if (coupon != null) {
            code = coupon.priceCode;
        }
        PmsPricing priceplan = getPriceObject(code);
        if (priceplan == null) {
            priceplan = new PmsPricing();
        }
        return priceplan;
    }

    @Override
    public void addCartItemToRoom(CartItem item, String pmsBookingRoomId, String addedBy) {

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
        if (addon.date == null) {
            addon.date = new Date();
        }
        addon.description = product.description;

        addAddonOnRoom(pmsBookingRoomId, addon);
    }

    public void addAddonOnRoom(String pmsBookingRoomId, PmsBookingAddonItem addon) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);

        room.addons.add(addon);
        saveBooking(booking);

        Product product = productManager.getProduct(addon.productId);
        logEntry("Added addon: " + product.name + "(" + addon.getName() + ")", booking.id, room.bookingItemId, room.pmsBookingRoomId, "addaddon");
    }

    private void updateRoomPriceFromAddons(PmsBookingRooms room, PmsBooking booking) {
        setAddonPricesOnRoom(room, booking);

        checkAndReportPriceMatrix(booking, "update room prices from addon 1");
        for (PmsBookingAddonItem item : room.addons) {
            if (isPackage(item)) {
                Double roomPrice = getRoomPriceFromPackage(item);
                room.price = roomPrice;
                for (String key : room.priceMatrix.keySet()) {
                    room.priceMatrix.put(key, roomPrice);
                }
            }
        }
        checkAndReportPriceMatrix(booking, "update room prices from addon 2");
    }

    private void setAddonPricesOnRoom(PmsBookingRooms room, PmsBooking booking) {
        PmsPricing priceplan = getPriceObjectFromBooking(booking);
        for (PmsBookingAddonItem item : room.addons) {
            if (priceplan.productPrices.containsKey(item.productId)) {
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
        for (PmsBookingAddonItem item2 : getConfiguration().addonConfiguration.values()) {
            if (item2.addonType == item.addonType) {
                return item2;
            }
        }
        return null;
    }

    public void addDefaultAddons(PmsBooking booking) {
        addDefaultAddonsToRooms(booking.getAllRooms());
    }
    
    public void setDefaultAddons(String bookingId) {
        addDefaultAddons(getBooking(bookingId));
    }

    @Override
    public void removeAddonFromRoom(String id, String pmsBookingRooms) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRooms);
        PmsBookingRooms room = booking.getRoom(pmsBookingRooms);
        PmsBookingAddonItem toremove = null;
        for (PmsBookingAddonItem item : room.addons) {
            if (item.addonId.equals(id)) {
                toremove = item;
            }
        }
        room.addons.remove(toremove);
        saveBooking(booking);
    }

    @Override
    public void markAddonDelivered(String id) {
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.rooms) {
                for (PmsBookingAddonItem item : room.addons) {
                    if (item.addonId.equals(id)) {
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
                        if (bitem != null) {
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
        if (cal.getTime().after(new Date())) {
            return;
        }
        deleteObject(object);
        deliveredAddons.remove(id);
    }

    @Override
    public List<PmsAddonDeliveryLogEntry> getDeliveryLog(List<String> productIds, Date start, Date end) {
        List<PmsAddonDeliveryLogEntry> res = new ArrayList();
        for (PmsAddonDeliveryLogEntry entry : deliveredAddons.values()) {
            if (productIds.contains(entry.productId)) {
                if (entry.rowCreatedDate.after(start) && entry.rowCreatedDate.before(end)) {
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
        if (res == null) {
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
        for (PmsBookingAddonItem item : getConfigurationSecure().addonConfiguration.values()) {
            if (item != null && item.productId != null && item.productId.equals(productId)) {
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
        for(ConferenceDataDay day : data.days) {
            Collections.sort(day.conferences, new Comparator<ConferenceDataRow>() {
                @Override
                public int compare(ConferenceDataRow o1, ConferenceDataRow o2) {
                    if(o1 == null || o2 == null || o1.from == null || o2.from == null) {
                        return 0;
                    }
                    return o1.from.compareTo(o2.from);
                }
            });

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
    public List<ConferenceData> getFutureConferenceData(Date fromDate) {
        List<ConferenceData> retList = conferenceDatas.values().stream()
                .filter(conference -> conference.days != null && !conference.days.isEmpty())
                .filter(conf -> isInFuture(conf, fromDate))
                .collect(Collectors.toList());

        retList.stream().forEach(conf -> finalize(conf));

        return retList;
    }

    private boolean isInFuture(ConferenceData data, Date fromDate) {
        PmsBooking booking = getBooking(data.bookingId);

        return booking != null && (!booking.isEndedBefore(fromDate) || booking.isActiveOnDay(new Date()));
    }

    private List<PmsBooking> filterByUnsettledAmounts(List<PmsBooking> finalized) {
        List<PmsBooking> result = new ArrayList();
        for (PmsBooking test : finalized) {
            if (isUnsettledAmount(test)) {
                result.add(test);
            }
        }

        return result;
    }
    
    public boolean isUnsettledAmount(PmsBooking test) {
        if (test.testReservation) {
            return false;
        }
        if (!test.payedFor) {
            return false;
        }
        test.calculateTotalCost();
        double total = test.getTotalPrice();
        double orderTotal = 0.0;
        for (String orderId : test.orderIds) {
            Order order = orderManager.getOrderSecure(orderId);
            orderTotal += orderManager.getTotalAmount(order);
        }
        double diff = total - orderTotal;
        if (diff < -3 || diff > 3) {
            test.totalUnsettledAmount = diff;
            return true;
        }
        
        return false;
    }

    public List<String> getListOfAllRoomsThatHasPriceMatrix() {
        List<String> ids = new ArrayList();

        for (PmsBooking book : bookings.values()) {
            for (PmsBookingRooms room : book.rooms) {
                if (!room.isActiveInPeriode(new Date(), new Date())) {
                    continue;
                }
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
    public Map<Long, List<ConferenceData>> getGroupedConferenceData(Date fromDate) {
        if(fromDate == null) {
            fromDate = new Date();
        }
        List<ConferenceData> futureConfData = getFutureConferenceData(fromDate);
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
        if (savedFilters.get(name) != null) {
            PmsBookingFilter tmpFilter = savedFilters.get(name);
            filter.id = tmpFilter.id;
        }
        filter.filterName = name;
        saveObject(filter);
        savedFilters.put(name, filter);
    }

    @Override
    public PmsBookingFilter getPmsBookingFilter(String name) {
        PmsBookingFilter res = savedFilters.get(name);
        Gson gson = new Gson();
        String test = gson.toJson(res);
        PmsBookingFilter copy = gson.fromJson(test, PmsBookingFilter.class);
        return copy;
    }

    @Override
    public List<PmsBookingFilter> getAllPmsFilters() {
        return new ArrayList(savedFilters.values());
    }

    @Override
    public void deletePmsFilter(String name) {
        PmsBookingFilter object = savedFilters.get(name);
        if (object != null) {
            savedFilters.remove(name);
            deleteObject(object);
        }
    }

    private void convertTextDates(PmsBookingFilter filter) {
        if (filter.startDate == null && filter.startDateAsText != null) {
            filter.startDate = convertTextDate(filter.startDateAsText);
        }
        if (filter.endDate == null && filter.endDateAsText != null) {
            filter.endDate = convertTextDate(filter.endDateAsText);
        }
    }

    @Override
    public Date convertTextDate(String text) {
        Calendar now = Calendar.getInstance();
        switch (text) {
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
        switch (text) {
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

    private void bookingUpdated(String bookingId, String type, String roomId) {
        virtualOrdersCreated = null;
        PmsBooking booking = getBookingUnsecure(bookingId);
        try {
            if (type.equals("created")) {
                bookingComRateManagerManager.pushBooking(booking, "Commit", true);
            } else if (type.equals("room_removed")
                    || type.equals("room_changed")
                    || type.equals("date_changed")
                    || type.equals("booking_undeleted")) {
                bookingComRateManagerManager.pushBooking(booking, "Modify", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (type.equals("room_removed")
                    || type.equals("room_changed")
                    || type.equals("date_changed")
                    || type.equals("booking_undeleted")
                    || type.equals("created")) {
                wubookManager.setAvailabilityChanged(booking.getStartDate(), booking.getEndDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bookingIdMap = new HashMap();
    }

    private boolean needConfirmation(PmsBooking booking) {
        if(storeManager.isPikStore()) {
            return false;
        }
        
        if (configuration.needConfirmationInWeekEnds && booking.isWeekendBooking() && booking.isStartingToday()) {
            return true;
        }

        boolean foundAutoConfirm = false;
        boolean foundNonAutoConfirm = false;
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            if (type == null) {
                continue;
            }
            if (room.date != null && room.date.start != null && room.date.end != null) {
                if (!isRestricted(type.id, room.date.start, room.date.start, TimeRepeaterData.TimePeriodeType.autoconfirm)) {
                    foundNonAutoConfirm = true;
                } else if (!isRestricted(type.id, room.date.end, room.date.end, TimeRepeaterData.TimePeriodeType.autoconfirm)) {
                    foundNonAutoConfirm = true;
                } else if (isRestricted(type.id, room.date.start, room.date.end, TimeRepeaterData.TimePeriodeType.autoconfirm)) {
                    foundAutoConfirm = true;
                } else {
                    foundNonAutoConfirm = true;
                }

                if (pmsInvoiceManager.isSameDay(new Date(), room.date.start) && isRestricted(type.id, room.date.start, room.date.end, TimeRepeaterData.TimePeriodeType.forceConfirmationSameDay)) {
                    return true;
                }

            }
            if (type.autoConfirm) {
                return false;
            }
        }

        if (!foundNonAutoConfirm && foundAutoConfirm) {
            return false;
        }

        User user = userManager.getUserById(booking.userId);
        if (user != null && user.autoConfirmBookings) {
            return false;
        }
        return configuration.needConfirmation;
    }

    private String getMessageToSend(String key, String type, PmsBooking booking, String language) {
        
        language = makeSureLanguageIsCorrect(language);
        
        String message = "";
        overrideNotificationTitle = "";
        if(key.equals("booking_completed") && booking.channel != null && booking.channel.contains("wubook")) {
            boolean isChargedByOta = isChargedByOta(booking);
            if (type.equals("email")) {
                message = configuration.emails.get("booking_completed_ota_" + language);
                if(message != null && !message.isEmpty()) { overrideNotificationTitle = "booking_completed_ota"; }
            } else {
                message = configuration.smses.get("booking_completed_ota_" + language);
            }
            if(isChargedByOta) {
                if (type.equals("email")) {
                    message = configuration.emails.get("booking_completed_payed_ota_" + language);
                    if(message != null && !message.isEmpty()) { overrideNotificationTitle = "booking_completed_payed_ota"; }
                } else {
                    message = configuration.smses.get("booking_completed_payed_ota_" + language);
                }
            }
        }
        
        if(message == null || message.isEmpty()) {
            if (type.equals("email")) {
                message = configuration.emails.get(key + "_" + language);
            } else {
                message = configuration.smses.get(key + "_" + language);
            }
        }
            
        if(message == null || message.isEmpty()) {
            if (type.equals("email")) {
                for(String tmpKey : configuration.emails.keySet()) {
                    if(tmpKey.startsWith(key) && configuration.emails.get(tmpKey) != null && !configuration.emails.get(tmpKey).isEmpty()) {
                        message = configuration.emails.get(tmpKey);
                        break;
                    }
                }
            } else {
                for(String tmpKey : configuration.smses.keySet()) {
                    if(tmpKey.startsWith(key) && configuration.smses.get(tmpKey) != null && !configuration.smses.get(tmpKey).isEmpty()) {
                        message = configuration.smses.get(tmpKey);
                        break;
                    }
                }
            }
        }
        

        if (messageToSend != null && !messageToSend.isEmpty()) {
            message = messageToSend;
        }

        if (key.startsWith("booking_sendpaymentlink")
                || key.startsWith("booking_unabletochargecard")
                || key.startsWith("booking_paymentmissing")
                || key.startsWith("order_")) {
            if (booking.orderIds != null && booking.orderIds.size() >= 2) {
                PmsProductMessageConfig orderMessage = getOrderMessage(orderIdToSend);
                if (orderMessage != null) {
                    if (type.equals("email")) {
                        message = orderMessage.email;
                    } else {
                        message = orderMessage.sms;
                    }
                }
            }
        }

        if (type.equals("email")) {
            if (message != null) {
                if (message.contains("http") && !message.contains("<a")) {
                    PmsBookingMessageFormatter formater = new PmsBookingMessageFormatter();
                    message = formater.formatHtml(message);
                }

                message = configuration.emailTemplate.replace("{content}", message);
                message = message.trim();
                message = message.replace("\n", "<br>\n");
            }
        }

        if ((message == null || message.isEmpty()) && key.toLowerCase().contains("room_resendcode")) {
            message = "Code {code} room {roomName}.";
        }

        if((message == null || message.isEmpty()) && key.startsWith("sendreciept") && type.equals("email")) {
            message = "reciept for your stay";
        }
        if((message == null || message.isEmpty()) && key.startsWith("sendinvoice") && type.equals("email")) {
            message = "invoice for your stay";
        }
        
        if (message == null || message.isEmpty()) {
            return "";
        }

        if (key.startsWith("booking_sendpaymentlink")
                || key.startsWith("booking_unabletochargecard")
                || key.startsWith("booking_paymentmissing")
                || key.startsWith("order_")) {
            if (orderIdToSend != null) {
                message = message.replace("{orderid}", this.orderIdToSend);
                Order order = orderManager.getOrderSecure(this.orderIdToSend);
                String link = pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/p.php?id=" + order.incrementOrderId;
                if (type.equals("email")) {
                    message = message.replace("{paymentlink}", "<a href='" + link + "'>" + link + "</a>");
                } else {
                    message = message.replace("{paymentlink}", link);
                }
                message = message.replace("{selfmanagelink}", pmsInvoiceManager.getPaymentLinkConfig().webAdress + "/?page=booking_self_management&id=" + booking.secretBookingId);
            }
        }

        return message;
    }

    private PmsProductMessageConfig getOrderMessage(String orderIdToSend) {

        Order order = orderManager.getOrderSecure(orderIdToSend);
        if (order == null || order.cart == null || order.cart.getItems() == null) {
            return null;
        }

        if (hasBookingEngineProducts(order)) {
            return null;
        }

        PmsPaymentLinksConfiguration paymentlinkConfig = pmsInvoiceManager.getPaymentLinkConfig();
        for (CartItem item : order.cart.getItems()) {
            for (PmsProductMessageConfig toCheck : paymentlinkConfig.productPaymentLinks) {
                if (toCheck.productIds.contains(item.getProduct().id)) {
                    return toCheck;
                }
            }
        }
        return null;
    }

    private boolean hasBookingEngineProducts(Order order) {
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        List<String> productIds = new ArrayList();
        for (BookingItemType type : types) {
            productIds.add(type.productId);
        }

        for (CartItem item : order.cart.getItems()) {
            if (productIds.contains(item.getProduct().id)) {
                return true;
            }
        }
        return false;
    }

    private void test() {
        for (PmsAdditionalItemInformation pinfo : addiotionalItemInfo.values()) {
            if (pinfo.itemId.equals("c0a8454a-6ebf-4d82-88c6-e2dae164fbbf")) {
                System.out.println("Cleaned: " + pinfo.isClean());
                List<Date> dates = pinfo.getAllCleaningDates();
                System.out.println("Last used: " + pinfo.getLastUsed());
                for (Date d : dates) {
                    System.out.println("Cleaning date : " + d);
                }
            }
        }
    }

    @Override
    public List<PmsRoomSimple> getRoomsToSwap(String roomId, String moveToType) {
        PmsBookingSimpleFilter filter = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        PmsBooking roomToCheckBooking = getBookingFromRoom(roomId);
        PmsBookingRooms room = roomToCheckBooking.getRoom(roomId);

        List<PmsRoomSimple> roomsToSwap = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms tmpRoom : booking.getActiveRooms()) {
                if (!tmpRoom.bookingItemTypeId.equals(moveToType)) {
                    continue;
                }
                if (tmpRoom.isActiveInPeriode(room.date.start, room.date.end)) {
                    roomsToSwap.add(filter.convertRoom(tmpRoom, booking));
                }
            }
        }
        return roomsToSwap;
    }

    @Override
    public String swapRoom(String roomId, List<String> roomIds) {
        PmsBooking bookingToSwap1 = getBookingFromRoom(roomId);
        PmsBookingRooms roomToSwap1 = bookingToSwap1.getRoom(roomId);
        String typeToSwap1 = roomToSwap1.bookingItemTypeId;
        String typeToSwap2 = "";

        HashMap<String, PmsBookingRooms> bookingsToSwap2 = new HashMap();

        for (String id : roomIds) {
            PmsBooking bookingToSwap2 = getBookingFromRoom(id);
            PmsBookingRooms roomToSwap2 = bookingToSwap2.getRoom(id);
            typeToSwap2 = roomToSwap2.bookingItemTypeId;
            bookingsToSwap2.put(id, bookingToSwap2.getRoom(id));
        }

        //Okey, try to swap.
        try {
            doSwapRoom(roomToSwap1, bookingsToSwap2, typeToSwap2, typeToSwap1);
        } catch (Exception e) {
            //It failed, revert the changes.
            try {
                doSwapRoom(roomToSwap1, bookingsToSwap2, typeToSwap1, typeToSwap2);
                return "The swap you tried to do is not possible";
            } catch (Exception d) {
                String swap = "";
                for (String a : roomIds) {
                    swap += a + ",";
                }
                messageManager.sendErrorNotification("Critical swap error happend. This should never occure and could cause overbooking, swap: " + roomId + " -> " + swap, d);
                return "A critical error occured, getshop has been notified.";
            }
        }

        return "Swap complete!";
    }

    private void doSwapRoom(PmsBookingRooms mainRoomToSwap, HashMap<String, PmsBookingRooms> bookingsToSwapWith, String typeToSwapTo, String mainTypeToSwap) throws ErrorException, Exception {
        //First delete the bookings.
        bookingEngine.deleteBooking(mainRoomToSwap.bookingId);
        for (String rid : bookingsToSwapWith.keySet()) {
            PmsBookingRooms room = bookingsToSwapWith.get(rid);
            bookingEngine.deleteBooking(room.bookingId);
        }
        String error = "";

        //Now insert new bookings swapped.
        mainRoomToSwap.bookingItemTypeId = typeToSwapTo;
        error = addBookingToBookingEngine(getBookingFromRoom(mainRoomToSwap.pmsBookingRoomId), mainRoomToSwap);
        if (!error.isEmpty()) {
            throw new Exception("Failed to add room");
        }

        for (String rid : bookingsToSwapWith.keySet()) {
            PmsBookingRooms room = bookingsToSwapWith.get(rid);
            room.bookingItemTypeId = mainTypeToSwap;
            error = addBookingToBookingEngine(getBookingFromRoom(room.pmsBookingRoomId), room);
            if (!error.isEmpty()) {
                throw new Exception("Failed to add room");
            }
        }

        //Then save the bookings.
        saveBooking(getBookingFromRoom(mainRoomToSwap.pmsBookingRoomId));
        for (String rid : bookingsToSwapWith.keySet()) {
            saveBooking(getBookingFromRoom(mainRoomToSwap.pmsBookingRoomId));
        }
    }

    private PmsBookingRooms getRoomFromOrderToSend() {
        Order order = orderManager.getOrderSecure(orderIdToSend);
        if (order == null) {
            return null;
        }
        if (order.attachedToRoom != null && !order.attachedToRoom.isEmpty()) {
            PmsBooking booking = getBookingFromRoom(order.attachedToRoom);
            return booking.getRoom(order.attachedToRoom);
        }
        return null;
    }

    @Override
    public void undoLastCleaning(String itemId) {
        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        if (additional == null) {
            return;
        }

        Date latestCleaning = null;
        Date latestCleaningBeforeThat = null;
        for (Date test : additional.getAllCleaningDates()) {
            if (latestCleaning == null || latestCleaning.before(test)) {
                if (latestCleaning != null) {
                    latestCleaningBeforeThat = latestCleaning;
                }
                latestCleaning = test;
            }
        }

        additional.setLastUsed(latestCleaningBeforeThat);
        additional.setLastCleaned(latestCleaningBeforeThat);
        additional.removeCleaning(latestCleaning);
        saveObject(additional);
    }

    private List<PmsBookingAddonItem> combineExistingAddons(List<PmsBookingAddonItem> addons, List<PmsBookingAddonItem> newAddons) {
        List<PmsBookingAddonItem> newAddonList = new ArrayList();
        for (PmsBookingAddonItem item : newAddons) {
            if (!item.isSingle) {
                boolean found = false;
                for (PmsBookingAddonItem existingAddon : addons) {
                    if (existingAddon.isSame(item)) {
                        found = true;
                    }
                }
                if (!found) {
                    newAddonList.add(item);
                }
            } else {
                newAddonList.add(item);
            }
        }
        return newAddonList;
    }

    public void markSentErrorMessageForWubookId(String wubookId) {
        FailedWubookInsertion failed = new FailedWubookInsertion();
        failed.wubookResId = wubookId;
        failed.when = new Date();
        saveObject(failed);
        failedWubooks.put(failed.wubookResId, failed);
    }

    public boolean hasSentErrorNotificationForWubookId(String wubookId) {
        return failedWubooks.containsKey(wubookId);
    }

    @Override
    public void checkOutRoom(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                room.checkedout = true;
                if (room.date.end.after(new Date())) {
                    changeDates(room.pmsBookingRoomId, booking.id, room.date.start, new Date());
                }
                logEntry("Room checked out", booking.id, room.bookingItemId, room.pmsBookingRoomId, "checkout");
            }
        }
        saveBooking(booking);
    }

    @Override
    public void undoCheckOut(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                room.checkedout = false;
            }
            logEntry("Undo checkout", booking.id, room.bookingItemId, room.pmsBookingRoomId, "checkout");
        }
        saveBooking(booking);
    }
    
    
    private void changeCheckoutTimeForGuestOnRoom(String itemId) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "checkout";
        filter.startDate = new Date();
        filter.endDate = new Date();

        List<PmsRoomSimple> simplerooms = getSimpleRooms(filter);
        for (PmsRoomSimple simple : simplerooms) {
            if (simple.bookingItemId.equals(itemId)) {
                checkOutRoom(simple.pmsRoomId);
            }
        }
    }

    private void diffPricesFromBooking(PmsBooking booking, PmsBooking oldBooking) {
        checkAndReportPriceMatrix(booking, "Old and (new) booking diff prices 1");
        checkAndReportPriceMatrix(oldBooking, "(Old) and new booking diff prices 1");
        try {
            if (booking.priceType == PmsBooking.PriceType.daily) {
                for (PmsBookingRooms room : booking.rooms) {
                    PmsBookingRooms oldRoom = oldBooking.getRoom(room.pmsBookingRoomId);
                    if (oldRoom == null) {
                        return;
                    }
                    String logText = "";
                    for (String day : room.priceMatrix.keySet()) {
                        Double roomDayPrice = room.priceMatrix.get(day);
                        Double oldRoomDayPrice = oldRoom.priceMatrix.get(day);
                        if (oldRoomDayPrice == null) {
                            oldRoomDayPrice = 0.0;
                        }
                        if (roomDayPrice == null) {
                            roomDayPrice = 0.0;
                        }

                        double diff = roomDayPrice - oldRoomDayPrice;
                        if (diff != 0.0) {
                            String guest = "";
                            if (!room.guests.isEmpty()) {
                                guest = room.guests.get(0).name;
                            }
                            logText += "Price changed from " + oldRoomDayPrice + " to " + roomDayPrice + ", at day: " + day + " for guest: " + guest + "<bR>";
                        }
                    }
                    if (!logText.isEmpty()) {
                        logEntry(logText, booking.id, room.bookingItemId);
                    }
                }
            }

        } catch (Exception e) {
            logPrintException(e);
        }
        checkAndReportPriceMatrix(booking, "Old and (new) booking diff prices 1");
        checkAndReportPriceMatrix(oldBooking, "(Old) and new booking diff prices 1");

    }

    @Override
    public void sendSmsToGuest(String guestId, String message) {
        if(guestId== null || guestId.isEmpty()) {
            return;
        }
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                for (PmsGuests guest : room.guests) {
                    if (guest.guestId.equals(guestId)) {
                        messageManager.sendSms("sveve", guest.phone, message, guest.prefix, configuration.smsName);
                        logEntry(message + "<bR> +" + guest.prefix + " " + guest.phone, booking.id, room.bookingItemId, room.pmsBookingRoomId, "sms");
                        return;
                    }
                }
            }
        }
    }

    public void forceRemoveFromBooking(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        List<PmsBookingRooms> newRoomList = new ArrayList();
        PmsBookingRooms toRemoveRoom = booking.getRoom(pmsBookingRoomId);
        for (PmsBookingRooms room : booking.rooms) {
            if (!room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                newRoomList.add(room);
            }
        }
        if (toRemoveRoom.bookingId != null && !toRemoveRoom.bookingId.isEmpty()) {
            bookingEngine.deleteBooking(toRemoveRoom.bookingId);
        }
        booking.rooms = newRoomList;
        saveBooking(booking);
    }

    @Override
    public Date getEarliestEndDate(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(room.invoicedTo);
        if (room.bookingItemTypeId != null) {
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            if (type.minStay > 0) {
                Calendar minStayCal = Calendar.getInstance();
                minStayCal.setTime(room.date.start);
                minStayCal.add(Calendar.MONTH, type.minStay);
                if (minStayCal.getTime().after(cal.getTime())) {
                    return minStayCal.getTime();
                }
            }
        }
        return cal.getTime();
    }

    @Override
    public void freezeSubscription(String pmsBookingRoomId, Date freezeUntil) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        System.out.println("Want to freeze between: " + room.invoicedTo + " - " + freezeUntil);
        cartManager.clear();
        Product freezeProduct = productManager.getProduct("freezeSubscription");
        if (freezeProduct == null) {
            freezeProduct = new Product();
            freezeProduct.id = "freezeSubscription";
            freezeProduct.name = "Freeze subscription";
            freezeProduct.price = 0.0;
            productManager.saveProduct(freezeProduct);
        }
        CartItem item = cartManager.addProductItem(freezeProduct.id, 1);
        item.startDate = room.invoicedTo;
        item.endDate = freezeUntil;
        item.getProduct().externalReferenceId = room.pmsBookingRoomId;
        Order order = orderManager.createOrderForUser(booking.userId);
        addOrderToBooking(booking, order.id);
        order.status = Order.Status.PAYMENT_COMPLETED;
        pmsInvoiceManager.validateInvoiceToDateForBooking(booking, new ArrayList());
        saveBooking(booking);
    }

    public void checkAndReportPriceMatrix(PmsBooking booking, String message) {
        try {
            if (!storeId.equals("178330ad-4b1d-4b08-a63d-cca9672ac329")) {
                return;
            }

            if (booking.isRegisteredToday()) {
                return;
            }

            for (PmsBookingRooms room : booking.rooms) {
                if (room.priceMatrix == null || room.priceMatrix.isEmpty()) {
                    logPrint("price jump detected, " + message + " booking id: " + booking.id);
                    logPrintException(new Exception());
                }
            }
        } catch (Exception e) {
            logPrintException(e);
        }
    }

    @Override
    public boolean isActive() {
        Calendar test = Calendar.getInstance();
        test.add(Calendar.MONTH, -1);
        Date check = test.getTime();
        for (PmsBooking booking : bookings.values()) {
            if (booking.rowCreatedDate.after(check)) {
                return true;
            }
        }
        return false;
    }

    private void addDiscountAddons(PmsBookingRooms room, PmsBooking booking) {
        if (booking.couponCode == null || booking.couponCode.isEmpty()) {
            return;
        }

        Coupon coupon = cartManager.getCoupon(booking.couponCode);
        if (coupon == null) {
            return;
        }
        String typeId = room.bookingItemTypeId;
        String productId = null;
        if (typeId != null) {
            BookingItemType type = bookingEngine.getBookingItemType(typeId);
            if (type != null) {
                productId = type.productId;
            }
        }

        int days = pmsInvoiceManager.getNumberOfDays(room.date.start, room.date.end);

        if (!cartManager.couponIsValid(booking.rowCreatedDate, booking.couponCode, room.date.start, room.date.end, productId, days)) {
            return;
        }

        for (AddonsInclude inc : coupon.addonsToInclude) {
            List<PmsBookingAddonItem> toRemove = new ArrayList();
            for (PmsBookingAddonItem remove : room.addons) {
                if (remove.productId.equals(inc.productId)) {
                    toRemove.add(remove);
                }
            }
            room.addons.removeAll(toRemove);

            List<PmsBookingAddonItem> items = createAddonToAddFromProductId(inc.productId, room);
            for (PmsBookingAddonItem item : items) {
                item.isIncludedInRoomPrice = inc.includeInRoomPrice;
            }
            room.addons.addAll(items);
        }
    }

    private List<PmsBookingAddonItem> createAddonToAddFromProductId(String productId, PmsBookingRooms room) {
        List<PmsBookingAddonItem> res = new ArrayList();
        PmsConfiguration config = getConfigurationSecure();
        Date toSet = room.date.start;

        for (PmsBookingAddonItem item : config.addonConfiguration.values()) {
            if (item.productId.equals(productId)) {
                if (item.isSingle) {
                    if (item.atEndOfStay) {
                        toSet = room.date.end;
                    }
                    res.add(createAddonToAdd(item, toSet, room));
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(room.date.start);
                    while (true) {
                        res.add(createAddonToAdd(item, cal.getTime(), room));
                        cal.add(Calendar.DAY_OF_YEAR, 1);
                        if (cal.getTime().after(room.date.end) || room.isSameDay(cal.getTime(), room.date.end)) {
                            break;
                        }
                    }
                }
            }
        }
        return res;
    }

    private void markRoomCleanInternal(String itemId, boolean logCleaning) {
        if (getConfiguration().getWhenCleaningEndStayForGuestCheckinOut()) {
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
        processor();
    }

    @Override
    public List<SimpleCleaningOverview> getSimpleCleaningOverview(Date start, Date end) {
        Calendar test = Calendar.getInstance();
        List<SimpleCleaningOverview> result = new ArrayList<SimpleCleaningOverview>();
        while (true) {
            PmsBookingFilter filter = new PmsBookingFilter();
            filter.startDate = test.getTime();

            List<PmsBookingRooms> checkout = getRoomsNeedingCheckoutCleaning(test.getTime());
            List<PmsBookingRooms> interval = getRoomsNeedingIntervalCleaning(test.getTime());

            SimpleCleaningOverview simple = new SimpleCleaningOverview();
            simple.date = test.getTime();
            simple.checkoutCleaningCount = checkout.size();
            simple.intervalCleaningCount = interval.size();

            result.add(simple);

            test.add(Calendar.DAY_OF_YEAR, 1);
            if (test.getTime().after(end)) {
                break;
            }
        }

        return result;
    }

    boolean recentlyStarter() {
        if (startedDate == null) {
            startedDate = new Date();
            return true;
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -4);
        if (cal.getTime().before(startedDate)) {
            return true;
        }
        return false;
    }

    public PmsRoomSimple checkPinCode(String bookingId, String pmsRoomId, String pincode) {
        PmsBooking booking = getBooking(bookingId);
        if (booking == null) {
            return null;
        }

        for (PmsBookingRooms room : booking.rooms) {
            if (room.pmsBookingRoomId.equals(pmsRoomId) && room.code.equals(pincode)) {
                PmsBookingSimpleFilter filter = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
                return filter.convertRoom(room, booking);
            }
        }

        return null;
    }

    @Override
    public void warnFailedBooking(PmsBooking booking) {
        Gson gson = new Gson();
        String rawBooking = gson.toJson(booking);
        messageManager.sendErrorNotification("Someone booked error message shown to enduser, booking raw:" + rawBooking, null);
    }

    @Override
    public void detachOrderFromBooking(String bookingId, String orderId) {
        PmsBooking booking = getBooking(bookingId);

        if (booking == null) {
            return;
        }

        booking.orderIds.remove(orderId);
        saveBooking(booking);
    }

    public PmsBooking getBookingBySecretId(String id) {
        PmsBooking booking = bookings.values()
                .stream()
                .filter(b -> b.secretBookingId.equals(id))
                .findAny()
                .orElse(null);

        if (booking != null) {
            booking = finalize(booking);
        }

        return booking;
    }

    @Override
    public List<PmsBookingAddonItem> getAddonsWithDiscountForBooking(String pmsBookingRoomId) {
        List<PmsBookingAddonItem> addons = getAddonsWithDiscount(pmsBookingRoomId);
        List<PmsBookingAddonItem> res = addons.stream()
                .filter(add -> add.isActive || add.isAvailableForBooking)
                .collect(Collectors.toList());

        Gson gson = new Gson();
        String copy = gson.toJson(res);

        Type listType = new TypeToken<List<PmsBookingAddonItem>>() {
        }.getType();

        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);

        res = gson.fromJson(copy, listType);
        for (PmsBookingAddonItem item : res) {
            item.setName(productManager.getProduct(item.productId));
            item.addedToRoom = false;
            for (PmsBookingAddonItem addonOnRoom : room.addons) {
                if (addonOnRoom.productId.equals(item.productId)) {
                    item.addedToRoom = true;
                    break;
                }
            }
        }

        return res;
    }

    public void removeAllUnclosedOrders(String id) {
        PmsBooking booking = getBooking(id);

        new ArrayList<String>(booking.orderIds).stream().forEach(orderId -> {
            Order order = orderManager.getOrderSecure(orderId);

            if (order.closed) {
                return;
            }

            order.cart.clear();
            orderManager.saveObject(order);
            booking.orderIds.remove(orderId);
        });

    }

    public boolean isAddonPaidFor(PmsBookingAddonItem addonItem) {
        // Pål, can you figure out how to implement this function properly?
        // Would also be nice to have "isPaid" on PmsBookingAddonItem as a propery.
        return false;
    }

    @Override
    public LinkedList<TimeRepeaterDateRange> generateRepeatDateRanges(TimeRepeaterData data) {
        TimeRepeater generator = new TimeRepeater();
        return generator.generateRange(data);
    }

    @Override
    public void addAddonToRoom(PmsBookingAddonItem addonToAdd, String pmsRoomId) {

        String productId = addonToAdd.productId;
        Date date = addonToAdd.date;

        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);

        PmsBookingAddonItem toAdd = null;
        for (PmsBookingAddonItem item : room.addons) {
            if (item.productId.equals(productId) && pmsInvoiceManager.isSameDay(item.date, date)) {
                toAdd = item;
                break;
            }
        }

        if (toAdd == null) {
            PmsBookingAddonItem original = getAddonFromProductId(productId);
            toAdd = createAddonToAdd(original, date, room);
            room.addons.add(toAdd);
        }

        toAdd.count = addonToAdd.count;
        toAdd.price = addonToAdd.price;
        toAdd.setName(addonToAdd.getName());

        saveBooking(booking);
    }

    private PmsBookingAddonItem getAddonFromProductId(String productId) {
        for (PmsBookingAddonItem item : getConfigurationSecure().addonConfiguration.values()) {
            if (item.productId.equals(productId)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Integer getNumberOfCustomers(PmsBookingFilter state) {
        List<PmsBooking> toCheck = getAllBookings(state);
        HashMap<String, Integer> count = new HashMap();
        for (PmsBooking book : toCheck) {
            count.put(book.userId, 1);
        }
        return count.keySet().size();
    }

    @Override
    public List<PmsCustomerRow> getAllUsers(PmsBookingFilter filter) {

        List<PmsBooking> toCheck = getAllBookings(filter);
        HashMap<String, Integer> count = new HashMap();
        for (PmsBooking book : toCheck) {
            count.put(book.userId, 1);
        }

        List<User> users = userManager.findUsers(filter.searchWord);
        for (User user : users) {
            count.put(user.id, 1);
        }

        HashMap<String, Integer> bookingCount = new HashMap();
        HashMap<String, Integer> roomCount = new HashMap();
        HashMap<String, Date> bookingLatest = new HashMap();
        for (PmsBooking book : bookings.values()) {
            if (bookingCount.containsKey(book.userId)) {
                bookingCount.put(book.userId, bookingCount.get(book.userId) + 1);
            } else {
                bookingCount.put(book.userId, 1);
            }
            if (roomCount.containsKey(book.userId)) {
                roomCount.put(book.userId, roomCount.get(book.userId) + book.getActiveRooms().size());
            } else {
                roomCount.put(book.userId, book.getActiveRooms().size());
            }
            if (bookingLatest.containsKey(book.userId)) {
                if (book.rowCreatedDate.after(bookingLatest.get(book.userId))) {
                    bookingLatest.put(book.userId, book.rowCreatedDate);
                }
            } else {
                bookingLatest.put(book.userId, book.rowCreatedDate);
            }
        }

        List<PmsCustomerRow> result = new ArrayList();
        for (String userId : count.keySet()) {
            PmsCustomerRow row = new PmsCustomerRow();
            User user = userManager.getUserById(userId);
            if (user == null) {
                continue;
            }
            row.customerId = user.customerId;
            row.userId = user.id;
            row.name = user.fullName;
            row.numberOfBookings = 0;
            if (bookingCount.containsKey(user.id)) {
                row.numberOfBookings = bookingCount.get(user.id);
            }
            if (roomCount.containsKey(user.id)) {
                row.numberOfRooms = roomCount.get(user.id);
            }
            row.numberOfOrders = orderManager.getAllOrdersForUser(user.id).size();
            row.latestBooking = bookingLatest.get(user.id);
            row.customerType = "UKNOWN";
            if (user.isCompanyMainContact) {
                row.customerType = "Company";
            } else if (!user.company.isEmpty()) {
                row.customerType = "Subuser";
            } else {
                row.customerType = "Private";
            }

            PmsUserDiscount discount = pmsInvoiceManager.getDiscountsForUser(userId);
            row.invoiceAfterStay = discount.supportInvoiceAfter;
            row.preferredPaymentType = user.preferredPaymentType;
            row.hasDiscount = discount.discounts.keySet().size() > 0;
            row.suspended = user.suspended;
            row.autoConfirmBookings = user.autoConfirmBookings;

            result.add(row);
        }

        return result;
    }

    @Override
    public void createNewUserOnBooking(String bookingId, String name, String orgId) {
        User newuser = null;
        PmsBooking booking = getBooking(bookingId);
        if (orgId != null && !orgId.trim().isEmpty()) {
            List<Company> companies = userManager.getCompaniesByVatNumber(orgId);
            if (companies != null) {
                for (Company comp : companies) {
                    List<User> existingUsers = userManager.getUsersThatHasCompany(comp.id);
                    if (!existingUsers.isEmpty()) {
                        newuser = existingUsers.get(0);
                        for (User tmp : existingUsers) {
                            if (tmp.isCompanyMainContact) {
                                newuser = tmp;
                                break;
                            }
                        }
                    }
                    if (newuser != null) {
                        break;
                    }
                }
            }
        }
        if (newuser == null) {
            newuser = new User();
            if (orgId != null && !orgId.trim().isEmpty()) {
                Company company = new Company();
                company.vatNumber = orgId;
                company.name = name;
                company = userManager.saveCompany(company);
                newuser.company.add(company.id);
            }
            newuser.fullName = name;
            newuser.isCompanyMainContact = true;
            userManager.saveUserSecure(newuser);
            userManager.getUserById(newuser.id);
        }

        booking.userId = newuser.id;
        saveBooking(booking);
    }

    @Override
    public void removeProductFromRoom(String pmsBookingRoomId, String productId) {
        removeProductFromRoomInternal(pmsBookingRoomId, productId, false);
    }

    private int numberOfYearsBetween(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        int startYear = cal.get(Calendar.YEAR);
        cal.setTime(end);
        int endYear = cal.get(Calendar.YEAR);

        return endYear - startYear;
    }

    private void setTotalFromIncomeReport(PmsStatistics result, PmsBookingFilter filter) {
        if (!filter.itemFilter.isEmpty()) {
            return;
        }

        List<BookingItemType> allRooms = bookingEngine.getBookingItemTypes();

        List<String> roomProductIds = new ArrayList();
        for (BookingItemType item : allRooms) {
            if (!filter.typeFilter.isEmpty() && !filter.typeFilter.contains(item.id)) {
                continue;
            }
            if (!roomProductIds.contains(item.productId)) {
                roomProductIds.add(item.productId);
            }
            for (String history : item.historicalProductIds) {
                if (!roomProductIds.contains(history)) {
                    roomProductIds.add(history);
                }
            }
        }
        Date now = new Date();
        
        if(filter.fromPms) {
            PmsOrderStatsFilter orderFilterToUse = createDefaultOrderStatsFilter(filter);
            setResultFromIncomeReportByFilter(result, orderFilterToUse, roomProductIds);
        } else {
            //In the midle of the periode selection.
            if (now.before(filter.endDate) && now.after(filter.startDate)) {
                PmsOrderStatsFilter orderFilterToUse = createDefaultOrderStatsFilter(filter);
                orderFilterToUse.end = getEndOfToday();
                setResultFromIncomeReportByFilter(result, orderFilterToUse, roomProductIds);
                orderFilterToUse.start = getEndOfToday();
                orderFilterToUse.end = filter.endDate;
                orderFilterToUse.includeVirtual = true;
                setResultFromIncomeReportByFilter(result, orderFilterToUse, roomProductIds);
            } else if (now.after(filter.endDate)) {
                //Before the selected periode.
                PmsOrderStatsFilter orderFilterToUse = createDefaultOrderStatsFilter(filter);
                setResultFromIncomeReportByFilter(result, orderFilterToUse, roomProductIds);
            } else {
                //After the selected periode
                PmsOrderStatsFilter orderFilterToUse = createDefaultOrderStatsFilter(filter);
                orderFilterToUse.includeVirtual = true;
                setResultFromIncomeReportByFilter(result, orderFilterToUse, roomProductIds);
            }
        }
    }

    private PmsOrderStatsFilter createDefaultOrderStatsFilter(PmsBookingFilter pmsFilter) {
        PmsOrderStatsFilter filter = new PmsOrderStatsFilter();
        filter.start = pmsFilter.startDate;
        filter.end = pmsFilter.endDate;
        filter.methods.clear();
        filter.savedPaymentMethod = "allmethods";
        filter.displayType = "dayslept";
        filter.priceType = "extaxes";
        filter.includeVirtual = pmsFilter.includeVirtual;
        filter.channel = pmsFilter.channel;

        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.start);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        filter.start = cal.getTime();

        cal.setTime(filter.end);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        filter.end = cal.getTime();

        List<Application> paymentApps = storeApplicationPool.getActivatedPaymentApplications();
        for (Application app : paymentApps) {
            PmsPaymentMethods method = null;
            if (app.id.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66") || app.id.equals("cbe3bb0f-e54d-4896-8c70-e08a0d6e55ba")) {
                //Invoices and samlefaktura needs to include everything
                method = new PmsPaymentMethods();
                method.paymentMethod = app.id;
                method.paymentStatus = 0;
                filter.methods.add(method);
                continue;
            }
            method = new PmsPaymentMethods();
            method.paymentMethod = app.id;
            method.paymentStatus = 7;
            filter.methods.add(method);

            method = new PmsPaymentMethods();
            method.paymentMethod = app.id;
            method.paymentStatus = -9;
            filter.methods.add(method);
        }

        if (filter.includeVirtual) {
            filter.methods.clear();
        }

        return filter;
    }

    public void logChanged(GetShopDeviceLog log) {
        if (!log.isOpen()) {
            return;
        }

        for (PmsBooking booking : bookings.values()) {

            if (booking.isEnded()) {
                continue;
            }

            for (PmsBookingRooms room : booking.rooms) {
                if (room.checkedin) {
                    continue;
                }

                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);

                if (item != null && item.bookingItemAlias.equals(log.getShopDeviceId) && room.code.equals(log.code) && log.event == 2) {
                    System.out.println("Marking as arrived: " + room.guests.get(0).name);
                    markGuestArrivedInternal(booking, room);
                }
            }
        }
    }

    @Override
    public void addToWorkSpace(String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);
        try {
            removeFromBooking(booking.id, pmsRoomId);
            room.inWorkSpace = true;
            saveBooking(booking);
        } catch (Exception e) {
            logPrintException(e);
        }
    }

    @Override
    public List<PmsBookingRooms> getWorkSpaceRooms() {
        List<PmsBookingRooms> res = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                if (room.inWorkSpace) {
                    res.add(room);
                }
            }
        }
        return res;
    }

    private boolean hasRoomsInWorkspace(Date start, Date end) {
        List<PmsBookingRooms> rooms = getWorkSpaceRooms();

        for (PmsBookingRooms room : rooms) {
            if (room.isActiveInPeriode(start, end)) {
                return true;
            }
        }
        return false;
    }

    private void verifyAddons(PmsBookingRooms room) {
        HashMap<String, Integer> products = new HashMap();
        for (PmsBookingAddonItem item : room.addons) {
            products.put(item.productId, 1);
        }

        for (String prodId : products.keySet()) {
            PmsBookingAddonItem addon = getConfigurationSecure().getAddonFromProductId(prodId);
            if (addon.dependsOnGuestCount || addon.isIncludedInRoomPrice || addon.addedToRoom || !addon.isSingle) {
                addAddonsToBooking(addon.addonType, room.pmsBookingRoomId, false);
            }
        }

        checkIfAddonsShouldBeRemoved(room);
    }

    private void checkIfAddonsShouldBeRemoved(PmsBookingRooms room) {
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        for (PmsBookingAddonItem item : room.addons) {
            if (!item.isSingle && !room.isActiveOnDay(item.date)) {
                toRemove.add(item);
            }
        }
        room.addons.removeAll(toRemove);
    }

    private void checkIfBookedBySubAccount(PmsBooking booking) {
        if (booking.alternativeOrginasation == null || booking.alternativeOrginasation.trim().isEmpty()) {
            return;
        }

        String[] altData = booking.alternativeOrginasation.split(";");
        String id = altData[0];
        User user = userManager.getUserById(id);
        if (user == null) {
            List<User> users = userManager.getUsersByCompanyId(id);
            id = null;
            for (User usr : users) {
                if (usr.isCompanyMainContact) {
                    id = usr.id;
                }
            }
        }

        if (id == null) {
            Company company = brRegEngine.getCompany(altData[0], true);
            userManager.saveCompany(company);
            User newuser = new User();
            company = userManager.saveCompany(company);
            newuser.company.add(company.id);
            newuser.isCompanyMainContact = true;
            newuser.address = company.address;
            newuser.fullName = company.name;
            newuser.cellPhone = company.phone;
            newuser.prefix = company.prefix;

            userManager.saveUserSecure(newuser);
            id = newuser.id;
        }

        booking.bookedByUserId = getSession().currentUser.id;
        booking.userId = id;

        user = userManager.getUserById(booking.bookedByUserId);
        if (!user.subUsers.contains(id)) {
            user.subUsers.add(id);
            userManager.saveUserSecure(user);
        }
    }

    private boolean hasRestriction(String itemType, Date start, Date end, boolean adminOverride) {
        //Check for user.
        if (getSession() != null && getSession().currentUser != null && (getSession().currentUser.isAdministrator() || getSession().currentUser.isEditor()) && adminOverride) {
            return false;
        }
        if (!isRestricted(itemType, start, end, TimeRepeaterData.TimePeriodeType.open)) {
            return true;
        }
        if (isRestricted(itemType, start, end, TimeRepeaterData.TimePeriodeType.closed)) {
            return true;
        }
        if (hasRoomsInWorkspace(start, end)) {
            return true;
        }
        if (avoidSameDayDropIn(start, itemType, adminOverride)) {
            return true;
        }
        if(closedForPeriode(start, end)) {
            return true;
        }
        
        return false;
    }

    private void addPrintablePrice(PmsBooking result) {
        boolean displayWithoutTaxes = false;
        if (getSession() != null && getSession().currentUser != null && getSession().currentUser.showExTaxes) {
            displayWithoutTaxes = true;
        }
        for (PmsBookingRooms room : result.rooms) {
            room.printablePrice = room.price;
            if (displayWithoutTaxes) {
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
                Product product = productManager.getProduct(type.productId);
                room.printablePrice = room.printablePrice / ((100 + product.taxGroupObject.taxRate) / 100);
            }
        }
    }

    @Override
    public List<PmsRoomSimple> getSimpleRoomsForGroup(String bookingEngineId) {
        PmsBookingSimpleFilter filtering = new PmsBookingSimpleFilter(this, pmsInvoiceManager);
        PmsBooking booking = getBookingFromBookingEngineId(bookingEngineId);
        return booking.rooms.stream()
                .map(room -> filtering.convertRoom(room, booking))
                .collect(Collectors.toList());
    }

    @Override
    public void orderCreated(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);
        HashMap<String, List<CartItem>> grouped = orderManager.groupItemsOnOrder(order.cart);
        for (String roomId : grouped.keySet()) {
            addOrderBasedOnReference(roomId, order);
        }
    }

    private void addOrderBasedOnReference(String roomId, Order order) {
        if (roomId == null || roomId.isEmpty()) {
            return;
        }

        if (order.isUnderConstruction) {
            return;
        }

        PmsBooking booking = getBookingFromRoom(roomId);

        if (booking == null) {
            return;
        }

        order.shippingDate = pmsInvoiceManager.getPaymentLinkSendingDate(booking.id);
        order.createByManager = "PmsDailyOrderGeneration";
        order.userId = booking.userId;

        orderManager.saveOrder(order);

        if (booking.orderIds == null) {
            booking.orderIds = new ArrayList();
        }
        if (!booking.orderIds.contains(order.id)) {
            addOrderToBooking(booking, order.id);
            saveBooking(booking);
        }
    }

    @Override
    public void orderChanged(String orderId) {
        Order order = orderManager.getOrderSecure(orderId);

        if (!order.cart.getItems().isEmpty()) {
            return;
        }

        bookings.values()
                .stream()
                .filter(o -> o != null && o.orderIds != null && o.orderIds.contains(orderId))
                .forEach(booking -> {
                    if (order.cart.getItems().isEmpty()) {
                        booking.orderIds.remove(orderId);
                    }
                });
    }

    @Override
    public User createUser(PmsNewUser newUser) {
        User user = new User();
        user.emailAddress = "";
        /* Fields found in FieldGenerator.php */
        user.fullName = newUser.name;
        user.emailAddress = newUser.email;

        user.address = new Address();
        user.address.address = "";
        user.address.postCode = "";
        user.address.city = "";
        user.address.countrycode = "";
        user.address.countryname = "";

        if (newUser.userlevel.equals("admin")) {
            user.type = User.Type.ADMINISTRATOR;
        }

        user = userManager.createUser(user);

        if (newUser.orgId != null && !newUser.orgId.isEmpty()) {

            Company company = null;
            if (newUser.orgId != null) {
                newUser.orgId = newUser.orgId.trim();
                List<Company> companies = userManager.getCompaniesByVatNumber(newUser.orgId);
                if (!companies.isEmpty()) {
                    company = companies.get(0);
                }
            }

            if (company == null) {
                company = new Company();
                company.vatNumber = newUser.orgId;
                company.name = newUser.name;
                company.vatRegisterd = true;

                company.address = new Address();
                company.address.address = "";
                company.address.postCode = "";
                company.address.city = "";
                company.address.countrycode = "";
                company.address.countryname = "";

                company.invoiceAddress = new Address();
                company.invoiceAddress.address = "";
                company.invoiceAddress.postCode = "";
                company.invoiceAddress.city = "";
                company.invoiceAddress.countrycode = "";
                company.invoiceAddress.countryname = "";
                userManager.saveCompany(company);
            }
            user.company.add(company.id);
            userManager.saveUser(user);
        }

        return user;

    }

    private void removeAddonsByDate(PmsBookingRooms room) {
        List<PmsBookingAddonItem> toRemove = new ArrayList();
        for (PmsBookingAddonItem addon : room.addons) {
            Date addonDate = addon.date;
            boolean remove = false;
            if (addonDate.after(room.date.end)) {
                remove = true;
            }
            if (addonDate.before(room.date.start) && !room.isSameDay(addonDate, room.date.start)) {
                remove = true;
            }

            if (remove) {
                toRemove.add(addon);
            }
        }
        room.addons.removeAll(toRemove);
    }

    @Override
    public PmsRoomTypeAccessory saveAccessory(PmsRoomTypeAccessory accessory) {
        saveObject(accessory);
        accesories.put(accessory.id, accessory);
        return accessory;
    }

    @Override
    public List<PmsRoomTypeAccessory> getAccesories() {
        return new ArrayList(accesories.values());
    }

    @Override
    public PmsBookingRooms getPrecastedRoom(String roomId, String bookingItemTypeId, Date from, Date to) {
        PmsBooking booking = getBookingFromRoom(roomId);
        PmsBookingRooms room = booking.rooms.stream()
                .filter(r -> r.pmsBookingRoomId.equals(roomId))
                .findFirst()
                .orElse(null);

        if (room == null) {
            return null;
        }

        Gson gson = new Gson();
        booking = gson.fromJson(gson.toJson(booking), PmsBooking.class);
        room = gson.fromJson(gson.toJson(room), PmsBookingRooms.class);

        room.date.start = from;
        room.date.end = to;
        room.bookingItemTypeId = bookingItemTypeId;

        pmsInvoiceManager.updatePriceMatrix(booking, room, booking.priceType);
        return room;
    }

    PmsBookingAddonItem getAddonByProductId(String productId) {
        List<PmsBookingAddonItem> addons = getAddonsAvailable();
        for (PmsBookingAddonItem addon : addons) {
            if (addon.productId.equals(productId)) {
                return addon;
            }
        }
        return null;
    }

    private void setResultFromIncomeReportByFilter(PmsStatistics result, PmsOrderStatsFilter orderFilterToUse, List<String> roomProductIds) {
        gsTiming("Before generate statistics");
        PmsOrderStatistics incomeReportResult = pmsInvoiceManager.generateStatistics(orderFilterToUse);
        gsTiming("After generate statistics");
        for (PmsOrderStatisticsEntry entry : incomeReportResult.entries) {
            for (StatisticsEntry statEntry : result.entries) {
                if (PmsBookingRooms.isSameDayStatic(statEntry.date, entry.day)) {
                    double total = 0.0;
                    String offset = PmsBookingRooms.convertOffsetToString(entry.day);
                    for (String roomId : statEntry.roomsPrice.keySet()) {
                        Double price = 0.0;
                        if (entry.priceExRoom.containsKey(roomId + "_" + offset)) {
                            price = entry.priceExRoom.get(roomId + "_" + offset);
                        }
                        statEntry.roomsPrice.put(roomId, price);
                    }
                    
                    statEntry.addRoomsNotIncluded(entry, statEntry, offset);

                    for (String productId : roomProductIds) {
                        Double productPrice = entry.priceEx.get(productId);
                        if (productPrice == null) {
                            productPrice = 0.0;
                        }
                        total += productPrice;
                    }
                    statEntry.totalForcasted = statEntry.totalPrice;
                    statEntry.totalPrice = (double) Math.round(total);
                    statEntry.totalRemaining = statEntry.totalForcasted - statEntry.totalPrice;
                    if (statEntry.roomsRentedOut == 0) {
                        statEntry.avgPrice = 0.0;
                        statEntry.avgPriceForcasted = 0.0;
                    } else {
                        statEntry.avgPrice = (double) Math.round(statEntry.totalPrice / statEntry.roomsRentedOut);
                        statEntry.avgPriceForcasted = (double) Math.round(statEntry.totalForcasted / statEntry.roomsRentedOut);
                    }
                    statEntry.ordersUsed.addAll(entry.getOrderIds());
                    break;
                }
                statEntry.finalize();
            }
        }
    }

    private Date getEndOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    @Override
    public void transferTicketsAsAddons() {
        smsHistoryManager.generateSmsUsage();

        List<Ticket> tickets = ticketManager.getTicketsToTransferToAccounting();

        Date invoiceDate = getFirstDateInMonth();

        tickets.stream().forEach(ticket -> {
            Product ticketProduct = productManager.getProduct("TICKET-" + ticket.type);
            if (ticketProduct == null) {
                ticketProduct = new Product();
                ticketProduct.id = "TICKET-" + ticket.type;
                ticketProduct.price = 1000;
                productManager.saveProduct(ticketProduct);
            }

            double totalPrice = (ticketProduct.price * ticket.timeInvoice);
            PmsBookingAddonItem addon = new PmsBookingAddonItem();
            addon.productId = ticketProduct.id;
            addon.count = 1;
            addon.price = (double) Math.round(totalPrice);
            addon.isSingle = true;
            addon.priceExTaxes = ticketProduct.priceExTaxes;
            addon.variations = ticketProduct.variationCombinations;
            addon.date = invoiceDate;
            addon.addedBy = "TICKET_SYSTEM";

            int seconds = (int) (ticket.timeInvoice * 60 * 60);
            String timeSpent = timeConversion(seconds);

            addon.setOverrideName("Ticket: " + ticket.incrementalId + " - " + ticket.title + " ( " + timeSpent + " )");

            if (addon.date == null) {
                addon.date = new Date();
            }

            addAddonOnRoom(ticket.externalId, addon);

            ticketManager.markTicketAsTransferredToAccounting(ticket.id);
        });

    }

    private String timeConversion(int seconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int minutes = seconds / SECONDS_IN_A_MINUTE;
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        int hours = minutes / MINUTES_IN_AN_HOUR;
        minutes -= hours * MINUTES_IN_AN_HOUR;

        if (hours < 1) {
            return minutes + " minutter";
        }

        if (minutes < 1 && hours > 1) {
            return hours + " timer";
        }

        if (minutes < 1 && hours == 1) {
            return hours + " time";
        }

        return hours + " timer og " + minutes + " minutter";
    }

    private HashMap<String, Integer> getCountedTypes(List<Booking> toCheck, Date start, Date end) {
        HashMap<String, Integer> result = new HashMap();
        for (Booking book : toCheck) {
            String typeId = book.bookingItemTypeId;
            Integer count = 0;
            if (result.containsKey(typeId)) {
                count = result.get(typeId);
            }
            boolean isSame = start.equals(book.startDate) && end.equals(book.endDate);
            if (book.interCepts(start, end) || isSame) {
                count++;
            }
            result.put(typeId, count);
        }
        return result;
    }

    private void checkIfNeedToBeAssignedToRoomWithSpecialAddons(PmsBooking booking) {
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            for (PmsBookingAddonItem item : room.addons) {
                if (!item.onlyForBookingItems.isEmpty()) {
                    List<BookingItem> items = bookingEngine.getAvailbleItems(room.bookingItemTypeId, room.date.start, room.date.end);
                    BookingItem bookingItem = null;
                    for (BookingItem tmpItem : items) {
                        if (item.onlyForBookingItems.contains(tmpItem.id)) {
                            bookingItem = tmpItem;
                            break;
                        }
                    }
                    if (bookingItem == null) {
                        messageManager.sendErrorNotification("Booking failed to autoassigned ot special addons room, no rooms to give", null);
                    } else {
                        room.bookingItemId = bookingItem.id;
                        room.bookingItemTypeId = bookingItem.bookingItemTypeId;

                        if (room.bookingId != null) {
                            try {
                                bookingEngine.changeBookingItemOnBooking(room.bookingId, bookingItem.id);
                            } catch (Exception e) {
                                messageManager.sendErrorNotification("Booking failed to autoassigned ot special addons room, no rooms to give (2)", e);
                            }
                        }
                        saveBooking(booking);
                        logEntry("Autoassigned due to special addon", booking.id, room.bookingItemId);
                    }
                }
            }
        }
    }

    private HashMap<String, String> buildBookingIdMap() {
        if (!bookingIdMap.isEmpty()) {
            return bookingIdMap;
        }

        bookingIdMap = new HashMap();
        for (PmsBooking booking : bookings.values()) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                if (room.bookingId != null) {
                    bookingIdMap.put(room.bookingId, booking.id);
                }
            }
        }

        return bookingIdMap;
    }

    @Override
    public void setNewStartDateAndAssignToRoom(String pmsRoomId, Date newStartDate, String bookingItemId) {

        System.out.println("In date: " + newStartDate);
        PmsBooking pmsBooking = getBookingFromRoom(pmsRoomId);
        if (pmsBooking == null) {
            return;
        }

        PmsBookingRooms room = pmsBooking.getRoom(pmsRoomId);

        if (room == null) {
            return;
        }

        Booking booking = bookingEngine.getBooking(room.bookingId);

        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.startDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(newStartDate);
        cal2.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        cal2.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        cal2.set(Calendar.SECOND, cal.get(Calendar.SECOND));
        cal2.set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
        newStartDate = cal2.getTime();

        System.out.println("Transformed date: " + newStartDate);

        System.out.println(booking.startDate + " | " + booking.endDate);

        long newTime = newStartDate.getTime();
        long diff = booking.startDate.getTime() - newTime;

        long newStart = booking.startDate.getTime() - diff;
        long newEnd = booking.endDate.getTime() - diff;

        Date start = new Date(newStart);
        Date end = new Date(newEnd);

        bookingEngine.changeBookingItemAndDateOnBooking(booking.id, bookingItemId, start, end);
        String logText = "Changed start date and item <b>" + newStartDate + "</b> New room : " + bookingEngine.getBookingItem(bookingItemId).bookingItemName;
        logEntry(logText, pmsBooking.id, room.bookingItemId, room.pmsBookingRoomId, "changestay");
    }

    public void addOrderToBooking(PmsBooking booking, String orderId) {
        if (checkDuplicateOrders(orderId, booking.id)) {
            Order order = orderManager.getOrder(orderId);
            messageManager.sendErrorNotification("Order added to a different booking: " + " : " + order.incrementOrderId + " - " + orderId + " booking: " + booking.id, new Exception());
            return;
        }
        booking.orderIds.add(orderId);
    }

    private boolean checkDuplicateOrders(String orderId, String currentBookingId) {
        List<String> orderIds = new ArrayList();
        for (PmsBooking booking : bookings.values()) {
            if (booking.id.equals(currentBookingId)) {
                continue;
            }
            if (booking.orderIds != null && booking.orderIds.contains(orderId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<PmsBookingAddonItem> createAddonsThatCanBeAddedToRoom(String productId, String pmsBookingRoomId) {
        PmsBookingAddonItem addon = getAddonByProductId(productId);
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);

        List<PmsBookingAddonItem> result = new ArrayList();
        if (addon.isSingle) {
            PmsBookingAddonItem addonToReturn = createAddonToAdd(addon, new Date(), room);
            addonToReturn.setOverrideName(productManager.getProduct(productId).name);
            result.add(addonToReturn);
            return result;
        }

        for (String day : room.priceMatrix.keySet()) {
            Date date = PmsBookingRooms.convertOffsetToDate(day);
            if (room.hasAddon(productId, date) != null) {
                continue;
            }
            PmsBookingAddonItem addonToAdd = createAddonToAdd(addon, date, room);
            if (addon.dependsOnGuestCount) {
                addonToAdd.count = room.numberOfGuests;
            }
            addonToAdd.setOverrideName(productManager.getProduct(productId).name);
            result.add(addonToAdd);
        }
        return result;
    }

    public PmsBooking getBookingUnsecureUnfinalized(String id) {
        return bookings.get(id);
    }

    private PmsRoomSimple getCurrentRoomOnItem(String itemId) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.filterType = "active";
        filter.startDate = new Date();
        filter.endDate = new Date();
        long now = filter.startDate.getTime();
        List<PmsRoomSimple> simplerooms = getSimpleRooms(filter);
        for (PmsRoomSimple simple : simplerooms) {
            if (simple.bookingItemId.equals(itemId) && simple.start < now && simple.end > now) {
                return simple;
            }
        }
        return null;
    }

    @Override
    public List<PmsCleaningHistory> getCleaningHistoryForItem(String itemId) {
        List<PmsCleaningHistory> returnList = new ArrayList();

        PmsAdditionalItemInformation additional = getAdditionalInfo(itemId);
        for (Date date : additional.getAllCleaningDates()) {
            PmsCleaningHistory res = new PmsCleaningHistory();
            String userId = additional.cleanedByUser.get(date.getTime());
            if (userId != null) {
                User usr = userManager.getUserByIdUnfinalized(userId);
                if(usr != null) {
                    res.name = usr.fullName;
                }
            }
            res.cleaned = true;
            res.date = date;
            returnList.add(res);
        }

        for (Long timezone : additional.markedDirtyDatesLog.keySet()) {
            Date date = new Date();
            date.setTime(timezone);
            PmsCleaningHistory res = new PmsCleaningHistory();
            String roomId = additional.markedDirtyDatesLog.get(timezone);
            if (roomId != null) {
                PmsBooking booking = getBookingFromRoom(roomId);
                if(booking != null) {
                    PmsBookingRooms room = booking.getRoom(roomId);
                    if (!room.guests.isEmpty()) {
                        res.name = room.guests.get(0).name;
                    }
                }
            }
            res.cleaned = false;
            res.date = date;
            returnList.add(res);
        }

        Collections.sort(returnList, new Comparator<PmsCleaningHistory>() {
            public int compare(PmsCleaningHistory o1, PmsCleaningHistory o2) {
                return o2.date.compareTo(o1.date);
            }
        });

        return returnList;
    }

    private Date getFirstDateInMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public void dumpRenaTrening() {
        System.out.println("------------------");
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = new Date();
        filter.endDate = new Date();
        filter.filterType = "active";
        List<PmsBooking> allBookings = getAllBookings(filter);

        //Etternavn,fornavn
        for (PmsBooking booking : allBookings) {
            for (PmsBookingRooms room : booking.getActiveRooms()) {
                User user = userManager.getUserById(booking.userId);
                if (user.fullName != null && user.fullName.contains(" ")) {
                    String[] names = user.fullName.split(" ");
                    System.out.print(names[1] + "\t" + names[0]);
                } else {
                    System.out.print(user.fullName + "\t");
                }
                BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);

                System.out.print("\t");
                //Kommentar
                System.out.print("\t");
                //Senteravtale
                System.out.print("\t");

                //Meldem siden
                SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
                System.out.print(sm.format(room.date.start) + "\t");
                
                System.out.print(sm.format(room.date.end) + "\t");

                //Binding
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.date.start);

                if (type.name.equals("Abonnement uten binding")) {
                    System.out.print("\t");
                } else if (type.name.equals("Forsvarsabonnement med binding")) {
                    cal.add(Calendar.MONTH, 12);
                    System.out.print(sm.format(cal.getTime()) + "\t");
                } else if (type.name.equals("Studentabonnement med binding 10")) {
                    cal.add(Calendar.MONTH, 10);
                    System.out.print(sm.format(cal.getTime()) + "\t");
                } else if (type.name.equals("Studentabonnement med binding 6")) {
                    cal.add(Calendar.MONTH, 6);
                    System.out.print(sm.format(cal.getTime()) + "\t");
                } else {
                    System.out.print("\t");
                }

                //Adgangskort
                System.out.print(room.code + "\t");

                //Epost
                System.out.print(user.emailAddress + "\t");

                //Mobiltlf
                System.out.print(user.cellPhone + "\t");

                //Kjønn
                System.out.print("\t");

                //Fødselsdato
                System.out.print(user.birthDay + "\t");

                //Adresse
                System.out.print(user.address.address + "\t");

                //Postnr
                System.out.print(user.address.postCode + "\t");

                //poststed
                System.out.print(user.address.city + "\t");

                //senter
                System.out.print("\t");

                //aktiv/inaktiv
                System.out.print("aktiv\t");

                //medlemsksapstype
                System.out.print(type.name + "\t");

                //betalingskort
                String cards = "";
                for (UserCard card : user.savedCards) {
                    cards += card.mask + ",";
                }
                if (cards.length() > 0) {
                    cards = cards.substring(0, cards.length() - 1);
                }
                System.out.print(cards + "\t");

                //dibs tickets
                cards = "";
                for (UserCard card : user.savedCards) {
                    cards += card.card + ",";
                }
                if (cards.length() > 0) {
                    cards = cards.substring(0, cards.length() - 1);
                }
                System.out.print(cards + "\t");
                if(room.bookingItemId != null) {
                    BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                    if(item != null) {
                        System.out.print(item.bookingItemName);
                    } else {
                        System.out.print("");
                    }
                }
                System.out.print("\t");
                
                System.out.print(cards + "\t");

                System.out.println();
            }
        }

    }

    private void addDefaultAddonsToRooms(List<PmsBookingRooms> allRooms) {
        HashMap<Integer, PmsBookingAddonItem> addons = getConfigurationSecure().addonConfiguration;
        for (PmsBookingRooms room : allRooms) {
            for (PmsBookingAddonItem item : addons.values()) {
                if(!item.isValidForPeriode(room.date.start, room.date.end, new Date())) {
                    continue;
                }
                if (room.bookingItemTypeId != null) {
                    if (item.includedInBookingItemTypes.contains(room.bookingItemTypeId) || item.alwaysAddAddon) {
                        int size = 1;
                        if (item.dependsOnGuestCount) {
                            size = room.numberOfGuests;
                        }
                        moveToWithinPeriode(room, item);
                        
                        if(item.isSingle && room.hasAddonOfProduct(item.productId)) {
                            continue;
                        }
                        
                        removeProductFromRoomInternal(room.pmsBookingRoomId, item.productId, true);
                        addProductToRoom(item.productId, room.pmsBookingRoomId, size, true);
                    }
                }
            }
        }
    }

    private List<PmsRoomSimple> removeByCustomersCodesAndAddons(List<PmsRoomSimple> res, PmsBookingFilter filter) {
        List<PmsRoomSimple> finalList = new ArrayList();
        for (PmsRoomSimple r : res) {
            if (!filter.customers.isEmpty() && !filter.customers.contains(r.userId)) {
                continue;
            }
            if (!filter.codes.isEmpty() && !filter.containsCode(getBooking(r.bookingId).couponCode)) {
                continue;
            }

            if (!filter.addons.isEmpty() && !filter.containsAddon(r.addons)) {
                continue;
            }

            finalList.add(r);
        }
        return finalList;
    }

    @Override
    public boolean removeFromWaitingList(String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);
        if (!room.addedToWaitingList) {
            return true;
        }

        String res = addBookingToBookingEngine(booking, room);
        if (res.isEmpty()) {
            room.addedToWaitingList = false;
            room.deleted = false;
            saveBooking(booking);
            return true;
        }
        return false;
    }

    @Override
    public boolean addToWaitingList(String pmsRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        PmsBookingRooms room = booking.getRoom(pmsRoomId);
        if (room.addedToWaitingList) {
            return true;
        }

        try {
            removeFromBooking(booking.id, room.pmsBookingRoomId);
            room.addedToWaitingList = true;
            saveBooking(booking);
            return true;
        }catch(Exception e) {
            logPrintException(e);
        }
        return false;
    }

    /**
     * This function is manually invoked from
     * Apac -> Gateway and then the button
     * name "check for dead codes"
     */
    @Override
    public void checkForDeadCodesApac() {
        List<PmsBooking> bookings = getAllBookingsFlat();

        if (bookings == null || bookings.isEmpty()) {
            return;
        }

        for(PmsBooking booking : bookings) {
            boolean save = false;
            for(PmsBookingRooms room : booking.rooms) {
                if(room.isEnded() && !room.isEndingToday()) {
                    if(room.codeObject != null) {
                        room.codeObject = null;
                        room.code = "";
                        save = true;
                    }
                }
            }
            if(save) {
                saveBooking(booking);
            }
        }
        
        List<LockGroup> groups = getShopLockSystemManager.getAllGroups();

        groups.stream().forEach(group -> {
            group.getGroupLockCodes().values()
                    .stream()
                    .filter(masterUserSlot -> masterUserSlot.takenInUseDate != null)
                    .filter(masterUserSlot -> masterUserSlot.takenInUseManagerName != null && masterUserSlot.takenInUseManagerName.equals("PmsManagerProcessor"))
                    .forEach(masterUserSlot -> {
                        boolean isSlotInUse = isMasterUserSlotInUse(group, masterUserSlot, bookings);
                        if (!isSlotInUse) {
                            String logCode = masterUserSlot.code != null ? "" + masterUserSlot.code.pinCode : "";
                            logPrint("Dead code found, removing from group: " + group.name + ", code: " + logCode);
                            getShopLockSystemManager.renewCodeForSlot(group.id, masterUserSlot.slotId);
                        }
                    });
        });
    }

    private boolean isMasterUserSlotInUse(LockGroup group, MasterUserSlot masterUserSlot, List<PmsBooking> bookings) {
        for (PmsBooking booking : bookings) {

            if (booking.isDeleted) {
                continue;
            }

            for (PmsBookingRooms room : booking.rooms) {
                if (room.isDeleted() || room.isEnded() || room.codeObject == null) {
                    continue;
                }

                if (masterUserSlot.takenInUseReference.equals(room.pmsBookingRoomId) && room.codeObject.slotId == masterUserSlot.slotId) {
                    return true;
                }
            }
        }

        return false;
    }

    private void resetDoorLockCode(PmsBookingRooms room) {
        if (room.addedToArx) {
            if (room.isStarted() && !room.isEnded()) {
                if ((!getConfigurationSecure().isGetShopHotelLock() && !getShopLockSystemManager.isActivated()) && !room.isEnded()) {
                    room.forceUpdateLocks = true;
                }
            }
        }
        room.addedToArx = false;
        try {
            if (room.bookingItemId != null && room.codeObject != null) {
                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                if (item != null) {
                    getShopLockSystemManager.renewCodeForSlot(item.lockGroupId, room.codeObject.slotId);
                }
            }
        } catch (Exception e) {
            logPrintException(e);
        }
    }

    void createAllVirtualOrdersForPeriode(Date start, Date end) {
        List<PmsBooking> allBookings = new ArrayList();
        allBookings.addAll(bookings.values());

        for (PmsBooking booking : allBookings) {
            if (start != null && end != null) {
                if (!booking.isActiveInPeriode(start, end)) {
                    continue;
                }
            }
            orderManager.deleteVirtualOrders(booking.id);
            pmsInvoiceManager.createVirtualOrder(booking.id);
        }

    }

    @Override
    public void updatePriceMatrixOnRoom(String pmsBookingRoomId, LinkedHashMap<String, Double> priceMatrix) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        for(PmsBookingRooms r : booking.rooms) {
            if(r.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                r.priceMatrix = priceMatrix;
            }
        }
        logEntry("Prices changed", booking.id, room.bookingItemId, room.pmsBookingRoomId, "price");
        saveBooking(booking);
        finalize(booking);
    }

    @Override
    public void checkInRoom(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        if(!room.isStarted()) {
            room = changeDates(room.pmsBookingRoomId, booking.id, new Date(), room.date.end);
        }
        if (room != null) {
            logEntry("Room checkedin", booking.id, room.bookingItemId, room.pmsBookingRoomId, "checkin");
            room.checkedin = true;
            saveBooking(booking);
        }
    }

    @Override
    public FilteredData getAllCrmUsers(FilterOptions filter) {
        HashMap<String, Integer> bookingCount = new HashMap();
        HashMap<String, Integer> roomCount = new HashMap();
        HashMap<String, Integer> guestCount = new HashMap();
        for (PmsBooking booking : bookings.values()) {
            Integer bcount = bookingCount.get(booking.userId);
            Integer rcount = roomCount.get(booking.userId);
            Integer gcount = guestCount.get(booking.userId);
            if (rcount == null) {
                rcount = 0;
            }
            if (bcount == null) {
                bcount = 0;
            }
            if (gcount == null) {
                gcount = 0;
            }
            bcount++;
            rcount += booking.rooms.size();
            for (PmsBookingRooms r : booking.rooms) {
                gcount += r.numberOfGuests;
            }

            bookingCount.put(booking.userId, bcount);
            roomCount.put(booking.userId, rcount);
            guestCount.put(booking.userId, gcount);
        }

        FilteredData result = userManager.getAllUsersFiltered(filter);
        for (User usr : (List<User>) result.datas) {
            AdditionalCrmData additional = new AdditionalCrmData();
            additional.numberOfBookings = bookingCount.get(usr.id);
            additional.numberOfRooms = roomCount.get(usr.id);
            additional.numberOfGuests = guestCount.get(usr.id);

            PmsUserDiscount discount = pmsInvoiceManager.getDiscountsForUser(usr.id);
            additional.invoiceAfterStay = discount.supportInvoiceAfter;
            additional.hasDiscount = discount.discounts.keySet().size() > 0;
            additional.discountType = discount.discountType;
            if (additional.hasDiscount) {
                Double discountPrice = 0.0;
                for (Double disc : discount.discounts.values()) {
                    discountPrice += disc;
                }
                additional.discount = (discountPrice / discount.discounts.size());
            }

            usr.additionalCrmData = additional;
        }

        return result;
    }

    public boolean hasNoBookings() {
        boolean hasNoBooking = true;
        if (bookings.size() < 10) {
            for (PmsBooking book : bookings.values()) {
                if (book.isCompletedBooking()) {
                    hasNoBooking = false;
                }
            }
        } else {
            hasNoBooking = false;
        }

        return hasNoBooking;
    }

    public List<PmsBooking> getAllBookingsInternal(PmsBookingFilter filter) {

        boolean unsettled = false;
        if (filter != null && filter.filterType != null && filter.filterType.equals("unsettled")) {
            unsettled = true;
            filter.filterType = "checkout";
        }
        if (filter != null && filter.searchWord != null) {
            filter.searchWord = filter.searchWord.trim();
        }

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
        gsTiming("searching");

        if (filter.searchWord != null && !filter.searchWord.isEmpty()) {

            for (PmsBooking booking : bookings.values()) {
                User user = userManager.getUserById(booking.userId);
                if (booking.id != null && booking.id.equals(filter.searchWord)) {
                    result.add(booking);
                } else if (user != null && user.fullName != null && user.fullName.toLowerCase().contains(filter.searchWord.toLowerCase())) {
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
                            if (!result.contains(booking)) {
                                result.add(booking);
                                found = true;
                            }
                        }
                        if (room.containsSearchWord(filter.searchWord)) {
                            result.add(booking);
                            found = true;
                        }
                    }
                    if (found) {
                        continue;
                    }
                }
            }
        } else if (filter.filterType == null || filter.filterType.isEmpty() || filter.filterType.equals("registered")) {
            for (PmsBooking booking : bookings.values()) {
                if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                    if (filter.userId == null || filter.userId.isEmpty()) {
                        result.add(booking);
                    } else if (filter.userId.equals(booking.userId)) {
                        result.add(booking);
                    }
                }
            }
        } else if (filter.filterType.equals("active")
                || filter.filterType.equals("inhouse")
                || filter.filterType.equals("unpaid")
                || filter.filterType.equals("afterstayorder")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.isActiveInPeriode(filter.startDate, filter.endDate)) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("waiting")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.isActiveInPeriode(filter.startDate, filter.endDate) && (booking.hasWaitingRooms() || booking.hasOverBooking())) {
                    result.add(booking);
                }
            }
        } else if (filter.filterType.equals("requestedending")) {
            for (PmsBooking booking : bookings.values()) {
                if (booking.hasRequestedEnding(filter.startDate, filter.endDate)) {
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
        gsTiming("done searching");

        removeInactive(filter, result);

        List<PmsBooking> finalized = finalizeList(result);
        finalized = filterTypes(finalized, filter.typeFilter);
        finalized = filterByUser(finalized, filter.userId);
        finalized = filterByChannel(finalized, filter.channel);
        finalized = filterByBComRateManager(finalized, filter);
        finalized = filterByUnpaid(finalized, filter);
        if (unsettled) {
            finalized = filterByUnsettledAmounts(finalized);
        }
        gsTiming("done finalizing new list");
        return finalized;
    }
    
    private boolean closedForPeriode(Date start, Date end) {
        List<TimeRepeaterData> closedPeriodes = getConfigurationSecure().closedOfPeriode;
        if(closedPeriodes == null || closedPeriodes.isEmpty()) {
            return false;
        }
        
        for(TimeRepeaterData data : closedPeriodes) {
            TimeRepeater repeater = new TimeRepeater();
            LinkedList<TimeRepeaterDateRange> timeRanges = repeater.generateRange(data);
            for(TimeRepeaterDateRange range : timeRanges) {
                if(range.isBetweenTime(start) || range.isBetweenTime(end)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void convertCheckInAndCheckoutToLocalTimeZone(PmsBooking booking) {
        String profile = getConfigurationSecure().bookingProfile;
        if(profile == null || profile.equals("hotel") || profile.isEmpty()) {
            for(PmsBookingRooms room : booking.rooms) {
                if(room.isSameDay(room.date.start, room.date.end)) {
                    continue;
                }
                room.date.start = getConfigurationSecure().getDefaultStart(room.date.start);
                room.date.end = getConfigurationSecure().getDefaultEnd(room.date.end);
            }
        }
    }

    private String getMessageTitle(String key, String lang) {
        String tocheckkey = key;
        if(overrideNotificationTitle != null && !overrideNotificationTitle.isEmpty()) {
            tocheckkey = overrideNotificationTitle;
            overrideNotificationTitle = "";
        }
        String title = configuration.emailTitles.get(tocheckkey + "_" + lang);
        
        if(title != null && !title.isEmpty()) {
            return title;
        }
        
        if(title == null || title.isEmpty()) {
            for(String titlekey : configuration.emailTitles.keySet()) {
                if(titlekey.contains(tocheckkey)) {
                    title = configuration.emailTitles.get(titlekey);
                    if(title != null && !title.isEmpty()) {
                        return title;
                    }
                }
            }
        }
        
        if(title == null || title.isEmpty() && key.startsWith("sendreciept")) {
            return "reciept for your stay";
        }
        if(title == null || title.isEmpty() && key.startsWith("sendinvoice")) {
            return "invoice for your stay";
        }

        return "";
    }

    @Override
    public void transferFromOldCodeToNew(String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        
        for (PmsBookingRooms room : booking.getActiveRooms()) {
            if (room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                if (room.code == null || room.code.isEmpty())
                    continue;
                
                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                if (item == null)
                    continue;
                
                LockCode lockCode = getShopLockSystemManager.getCodeAndMarkAsInUse(item.lockGroupId, room.pmsBookingRoomId, PmsManagerProcessor.class.getSimpleName(), "Automatically assigned by PMS processor", room.code);
                if (lockCode == null)
                    continue;
                
                room.addedToArx = true;
                markRoomAsDirty(room.bookingItemId);
                room.forceUpdateLocks = false;
                room.code = ""+lockCode.pinCode;
                room.codeObject = lockCode;
            }
        }
        
        saveBooking(booking);
    }

    private List<PmsRoomSimple> sortDeletedLast(List<PmsRoomSimple> res) {
        List<PmsRoomSimple> newRes = new ArrayList();
        for(PmsRoomSimple s : res) {
            if(s.progressState != null && !s.progressState.equals("deleted")) {
                newRes.add(s);
            }
        }
        for(PmsRoomSimple s : res) {
            if(s.progressState != null && s.progressState.equals("deleted")) {
                newRes.add(s);
            }
        }
        return newRes;
    }

    private void checkIfGuestHasArrivedApac() {
        getShopLockSystemManager.getAccessEvents()
                .stream()
                .forEach(event -> {
                    for (PmsBooking booking : bookings.values()) {
                        for (PmsBookingRooms room : booking.rooms) {
                            if (!room.isStarted() || room.isEnded() || room.checkedin) {
                                continue;
                            }
                            if (room.bookingItemId != null && room.codeObject != null) {
                                BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
                                if (item != null && item.lockGroupId.equals(event.groupId) && event.date.after(room.date.start) && event.date.before(room.date.end)) {
                                    markGuestArrivedInternal(booking, room);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public Object preProcessMessage(Object object, Method executeMethod) {

        List<String> methodsThatShouldInvokeProcessor = new ArrayList();
        methodsThatShouldInvokeProcessor.add("saveBooking");
        methodsThatShouldInvokeProcessor.add("generateNewCodeForRoom");
        methodsThatShouldInvokeProcessor.add("markRoomDirty");
        methodsThatShouldInvokeProcessor.add("markRoomAsCleaned");
        methodsThatShouldInvokeProcessor.add("markRoomAsCleanedWithoutLogging");
        
        if (executeMethod != null && methodsThatShouldInvokeProcessor.contains(executeMethod.getName().toString())) {
            processor();
        }
        
        return object;
    }
    
    public void createDefaultAddonConfig(String productId) {
        List<PmsBookingAddonItem> addonConfigsForProduct = getConfigurationSecure().addonConfiguration.values()
                .stream()
                .filter(o -> o.productId.equals(productId))
                .collect(Collectors.toList());
        
        if (addonConfigsForProduct.isEmpty()) {
            PmsBookingAddonItem config = new PmsBookingAddonItem();
            config.productId = productId;
            config.isSingle = true;
            getConfigurationSecure().addonConfiguration.put(getConfigurationSecure().addonConfiguration.size(), config);
            saveObject(configuration);
        }
    }
    
    public Date getNextCleaningDate(String bookingId, String pmsRoomId) {
        PmsBookingRooms room = getBookingUnsecure(bookingId).getRoom(pmsRoomId);
        
        Date now = new Date();
        
        PmsBookingAddonItem firstCleaningAddonInFuture = room.addons.stream()
                .filter(item -> item.productId.equals("gs_pms_extra_cleaning"))
                .filter(item -> item.date.after(now))
                .sorted((PmsBookingAddonItem item1, PmsBookingAddonItem item2) -> {
                    return item1.date.compareTo(item2.date);
                })
                .findFirst()
                .orElse(null);
        
        if (firstCleaningAddonInFuture != null) {
            return firstCleaningAddonInFuture.date;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 4);
        return cal.getTime();
    }
    
    @Override
    public void generatePgaAccess(String pmsBookingId, String pmsBookingRoomId) {
        PmsBooking booking = getBooking(pmsBookingId);
        if (booking == null) {
            throw new RuntimeException("Did not find the Pms Booking for the requested PGA access");
        }
        
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        
        if (room == null) {
            throw new RuntimeException("Did not find the PMS Room for the requested PGA access");
        }
        
        room.pgaAccessToken = pgaManager.createAccessToken(pmsBookingId, pmsBookingRoomId);
        saveBooking(booking);
    }

    @Override
    public void removePgaAccess(String pmsBookingId, String pmsBookingRoomId) {
        long time = System.currentTimeMillis();
        pgaManager.removeAccessToken(pmsBookingId, pmsBookingRoomId);
        
        PmsBooking booking = getBooking(pmsBookingId);
        if (booking == null) {
            throw new RuntimeException("Did not find the Pms Booking for the requested PGA access");
        }
        
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        
        if (room == null) {
            throw new RuntimeException("Did not find the PMS Room for the requested PGA access");
        }
        
        room.pgaAccessToken = "";
        saveBooking(booking);
    }

    private BookingItem getBestBookingItem(List<BookingItem> items) {
        BookingItem item = null;
        for (BookingItem tmpitem : items) {
            item = tmpitem;
            PmsAdditionalItemInformation additionalroominfo = getAdditionalInfo(item.id);
            //Comment to commit.
            if(getConfigurationSecure().avoidRandomRoomAssigning) {
                return item;
            }
            if (additionalroominfo.isClean()) {
                return item;
            }
        }
        return item;
    }

    private void removeProductFromRoomInternal(String pmsBookingRoomId, String productId, boolean avoidProductInStayPeriode) {
        PmsBooking booking = getBookingFromRoomSecure(pmsBookingRoomId);
        if(booking != null) {
            PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
            List<PmsBookingAddonItem> toRemove = new ArrayList();
            for (PmsBookingAddonItem item : room.addons) {
                if (item.productId.equals(productId)) {
                    if(avoidProductInStayPeriode) {
                        if((room.date.start.after(item.date) || room.date.end.before(item.date)) && !PmsBookingRooms.isSameDayStatic(item.date, room.date.start)) {
                            toRemove.add(item);
                        }
                    } else {
                        toRemove.add(item);
                    }
                }
            }
            Product prod = productManager.getProduct(productId);
            String name = "deleted product";
            if(prod != null) {
                name = prod.name;
            }
            room.addons.removeAll(toRemove);
            logEntry("Removed addon from room:" + name, booking.id, room.bookingItemId, room.pmsBookingRoomId, "removeaddon");
            saveBooking(booking);
        }
    }

    private void addProductToRoom(String productId, String pmsRoomId, Integer count, boolean doNotAddIfExisting) {
        if(productManager.getProductUnfinalized(productId) == null) {
            return;
        }
        PmsBooking booking = getBookingFromRoom(pmsRoomId);
        if(booking == null) {
            return;
        }
        PmsBookingRooms room = booking.findRoom(pmsRoomId);
        if (count == 0) {
            List<PmsBookingAddonItem> toRemove = new ArrayList();
            for (PmsBookingAddonItem item : room.addons) {
                if (item.productId.equals(productId)) {
                    toRemove.add(item);
                }
            }
            if (!toRemove.isEmpty()) {
                room.addons.removeAll(toRemove);
            }
        } else {
            PmsBookingAddonItem item = new PmsBookingAddonItem();
            for (PmsBookingAddonItem test : getConfigurationSecure().addonConfiguration.values()) {
                if (test.productId != null && test.productId.equals(productId)) {
                    item = test;
                    break;
                }
            }
            if(count == -1) {
                if(item.dependsOnGuestCount) {
                    count = room.numberOfGuests;
                } else {
                    count = 1;
                }
            }
            
            List<PmsBookingAddonItem> addons = createAddonForTimePeriode(item.addonType, room, booking.priceType);
            for (PmsBookingAddonItem addon : addons) {
                addon.count = count;
            }
            
            if(doNotAddIfExisting) {
                List<PmsBookingAddonItem> avoidAddingAddon = new ArrayList();
                for(PmsBookingAddonItem addon : addons) {
                    PmsBookingAddonItem existingAddon = room.hasAddon(addon.productId, addon.date);
                    if(existingAddon != null) {
                        avoidAddingAddon.add(addon);
                    }
                }
                addons.removeAll(avoidAddingAddon);
            }
            
            room.addons.addAll(addons);
        }
        saveBooking(booking);    
    }

    private void moveToWithinPeriode(PmsBookingRooms room, PmsBookingAddonItem itemToCheck) {
        if(!itemToCheck.isSingle) {
            return;
        }
        for(PmsBookingAddonItem item : room.addons) {
            if(itemToCheck.productId.equals(item.productId) && (item.date.before(room.date.start) && !PmsBookingRooms.isSameDayStatic(room.date.start, item.date))) {
                item.date = room.date.start;
            }
            if(itemToCheck.productId.equals(item.productId) && (item.date.after(room.date.end) && !PmsBookingRooms.isSameDayStatic(room.date.end, item.date))) {
                item.date = room.date.end;
            }
        }
    }

    private boolean isChargedByOta(PmsBooking booking) {
        if(booking.orderIds == null) {
            return false;
        }
        for(String orderId : booking.orderIds) {
            Order ord = orderManager.getOrder(orderId);
            if(ord != null) {
                if(ord.isPrepaidByOTA()) {
                    return true;
                }
            }
        }
        return false;
    }

    private String makeSureLanguageIsCorrect(String language) {
        if(language != null) {
            if(language.equalsIgnoreCase("en")) { return "en_en"; }
            if(language.equalsIgnoreCase("no")) { return "nb_NO"; }
            if(language.equalsIgnoreCase("nb_no")) { return "nb_NO"; }
        }
        return language;
    }

    @Override
    public void printCode(String gdsDeviceId, String pmsBookingRoomId) {
        PmsBooking booking = getBookingFromRoom(pmsBookingRoomId);
        PmsBookingRooms room = booking.getRoom(pmsBookingRoomId);
        
        if (room.bookingItemId == null || room.bookingItemId.isEmpty()) {
            return;
        }
        
        if (room.code == null || room.code.isEmpty()) {
            return;
        }
        
        BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
        DevicePrintRoomCode message = new DevicePrintRoomCode();
        message.roomName = item.bookingItemName;
        message.code = room.code;
        
        gdsManager.sendMessageToDevice(gdsDeviceId, message);
    }
}